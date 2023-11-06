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
import org.mybatis.dynamic.sql.select.PagingModel;
import org.mybatis.dynamic.sql.util.FragmentAndParameters;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public interface PaginationModelRenderer {

    default Optional<FragmentAndParameters> render(Optional<PagingModel> pagingModel, AtomicInteger sequence, RenderingStrategy renderingStrategy) {
        if (pagingModel != null && pagingModel.isPresent()) {
            if (pagingModel.get().fetchFirstRows().isPresent()) {
                throw new IllegalArgumentException("can't set fetchFirstRows value, just use limit and offset parameter!");
            }

            if (pagingModel.get().limit().isPresent()) {
                return Optional.ofNullable(doRender(pagingModel.get().limit().get(), pagingModel.get().offset(), sequence, renderingStrategy));
            }
        }
        return Optional.empty();
    }

    default FragmentAndParameters doRender(Long limit, Optional<Long> offset, AtomicInteger sequence, RenderingStrategy renderingStrategy) {
        return null;
    }

    default String renderPlaceholder(String parameterName, RenderingStrategy renderingStrategy) {
        return renderingStrategy.getFormattedJdbcPlaceholder(
                RenderingStrategy.DEFAULT_PARAMETER_PREFIX,
                parameterName
        );
    }

}
