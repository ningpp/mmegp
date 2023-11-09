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

import java.util.ArrayList;
import java.util.List;

public class DefaultPageImpl<T> implements Page<T> {
    private final List<T> items;
    private final long totalCount;

    public DefaultPageImpl() {
        this(null, 0L);
    }

    public DefaultPageImpl(List<T> items, long totalCount) {
        this.items = items == null ? new ArrayList<>(0) : new ArrayList<>(items);
        this.totalCount = totalCount;
    }

    @Override
    public List<T> getItems() {
        return items;
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }
}
