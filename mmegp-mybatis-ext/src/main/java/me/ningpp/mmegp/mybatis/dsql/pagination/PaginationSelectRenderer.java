/*
 *    Copyright 2021-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package me.ningpp.mmegp.mybatis.dsql.pagination;

import org.mybatis.dynamic.sql.common.OrderByModel;
import org.mybatis.dynamic.sql.common.OrderByRenderer;
import org.mybatis.dynamic.sql.configuration.StatementConfiguration;
import org.mybatis.dynamic.sql.render.RenderingContext;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.render.TableAliasCalculator;
import org.mybatis.dynamic.sql.select.QueryExpressionModel;
import org.mybatis.dynamic.sql.select.SelectDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.DefaultSelectStatementProvider;
import org.mybatis.dynamic.sql.select.render.QueryExpressionRenderer;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.FragmentAndParameters;
import org.mybatis.dynamic.sql.util.FragmentCollector;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * render SelectModel with custom PaginationModelRender
 * <p>copy source code from SelectRenderer</p>
 * @see org.mybatis.dynamic.sql.select.render.SelectRenderer
 */
public class PaginationSelectRenderer {
    private static final StatementConfiguration DEFAULT_STATEMENT_CONFIGURATION = new StatementConfiguration();
    private final SelectModel selectModel;
    private final LimitOffset limitOffset;
    private final PaginationModelRenderer paginationModelRender;
    private final RenderingStrategy renderingStrategy;
    private final AtomicInteger sequence;
    private final TableAliasCalculator parentTableAliasCalculator; // may be null

    public PaginationSelectRenderer(SelectDSL<SelectModel> listDsl, LimitOffset limitOffset,
            PaginationModelRenderer paginationModelRender) {
        this(listDsl.build(), limitOffset, paginationModelRender, RenderingStrategies.MYBATIS3, null, null);
    }

    public PaginationSelectRenderer(SelectModel selectModel, LimitOffset limitOffset,
            PaginationModelRenderer paginationModelRender,
            RenderingStrategy renderingStrategy,
            AtomicInteger sequence, TableAliasCalculator parentTableAliasCalculator) {
        this.selectModel = Objects.requireNonNull(selectModel);
        this.limitOffset = limitOffset;
        this.paginationModelRender = Objects.requireNonNull(paginationModelRender);
        this.renderingStrategy = Objects.requireNonNull(renderingStrategy);
        if (sequence == null) {
            this.sequence = new AtomicInteger(1);
        } else {
            this.sequence = sequence;
        }
        this.parentTableAliasCalculator = parentTableAliasCalculator == null
            ? TableAliasCalculator.empty() : parentTableAliasCalculator;
    }

    private <R> Stream<R> mapQueryExpressions(Function<QueryExpressionModel, R> mapper) {
        return selectModel.queryExpressions().map(mapper);
    }

    public SelectStatementProvider render() {
        FragmentCollector fragmentCollector = mapQueryExpressions(this::renderQueryExpression)
                .collect(FragmentCollector.collect());

        Optional<FragmentAndParameters> orderByFragmentAndParam = renderOrderBy();
        orderByFragmentAndParam.ifPresent(fragmentCollector::add);

        Optional<FragmentAndParameters> pagingFragmentAndParam = renderPagingModel();
        if (pagingFragmentAndParam.isPresent()) {
            fragmentCollector.add(pagingFragmentAndParam.get());

            if (orderByFragmentAndParam.isEmpty()) {
                throw new IllegalArgumentException("when using paging query, you should set order by value.");
            }
        }

        return toSelectStatementProvider(fragmentCollector);
    }

    private SelectStatementProvider toSelectStatementProvider(FragmentCollector fragmentCollector) {
        return DefaultSelectStatementProvider
                .withSelectStatement(fragmentCollector.collectFragments(Collectors.joining(" ")))
                .withParameters(fragmentCollector.parameters())
                .build();
    }

    private FragmentAndParameters renderQueryExpression(QueryExpressionModel queryExpressionModel) {
        return QueryExpressionRenderer.withQueryExpression(queryExpressionModel)
                .withRenderingContext(
                    RenderingContext
                        .withRenderingStrategy(renderingStrategy)
                        .withSequence(sequence)
                        .withTableAliasCalculator(parentTableAliasCalculator)
                        .withStatementConfiguration(DEFAULT_STATEMENT_CONFIGURATION)
                    .build()
                )
                .build()
                .render();
    }

    private Optional<FragmentAndParameters> renderOrderBy() {
        return selectModel.orderByModel().map(this::renderOrderBy);
    }

    private FragmentAndParameters renderOrderBy(OrderByModel orderByModel) {
        return new OrderByRenderer().render(orderByModel);
    }

    private Optional<FragmentAndParameters> renderPagingModel() {
        return paginationModelRender.render(limitOffset, sequence, renderingStrategy);
    }

}
