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

import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.util.FragmentAndParameters;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractPaginationModelRenderer implements PaginationModelRenderer {

    @Override
    public final FragmentAndParameters doRender(Long limit, Optional<Long> offset, AtomicInteger sequence, RenderingStrategy renderingStrategy) {
        if (offset.isEmpty()) {
            return renderLimitOnly(limit, sequence, renderingStrategy);
        } else {
            return renderLimitAndOffset(limit, offset.get(), sequence, renderingStrategy);
        }
    }

    private FragmentAndParameters renderLimitOnly(Long limit, AtomicInteger sequence, RenderingStrategy renderingStrategy) {
        String limitMapKey = renderingStrategy.formatParameterMapKey(sequence);
        return FragmentAndParameters.withFragment(String.format(Locale.ROOT,
                        limitOnlyPattern(),
                        renderPlaceholder(limitMapKey, renderingStrategy)))
                .withParameter(limitMapKey, limit)
                .build();
    }

    private FragmentAndParameters renderLimitAndOffset(Long limit, Long offset, AtomicInteger sequence, RenderingStrategy renderingStrategy) {
        String limitMapKey = renderingStrategy.formatParameterMapKey(sequence);
        String offsetMapKey = renderingStrategy.formatParameterMapKey(sequence);
        String limitPlaceholder = renderPlaceholder(limitMapKey, renderingStrategy);
        String offsetPlaceholder = renderPlaceholder(offsetMapKey, renderingStrategy);
        boolean limitIsFirstParameter = limitIsFirstParameter();
        return FragmentAndParameters.withFragment(String.format(Locale.ROOT,
                        limitAndOffsetPattern(),
                        limitIsFirstParameter ? limitPlaceholder : offsetPlaceholder,
                        limitIsFirstParameter ? offsetPlaceholder : limitPlaceholder))
                .withParameter(limitMapKey, limit)
                .withParameter(offsetMapKey, offset)
                .build();
    }

    protected abstract String limitOnlyPattern();

    protected abstract String limitAndOffsetPattern();

    protected abstract boolean limitIsFirstParameter();

}
