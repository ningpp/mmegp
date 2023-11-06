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

/**
 * <p>FETCH FIRST n ROWS ONLY and OFFSET m ROWS FETCH NEXT n ROWS ONLY.</p>
 * <p>PostgreSQL, Oracle, H2 and many DBs support this ANSI SQL standard syntax.</p>
 */
public class OffsetFetchPaginationModelRenderer extends AbstractPaginationModelRenderer {

    @Override
    public String limitOnlyPattern() {
        return "FETCH FIRST %s ROWS ONLY";
    }

    @Override
    public String limitAndOffsetPattern() {
        return "OFFSET %s ROWS FETCH NEXT %s ROWS ONLY";
    }

    @Override
    protected boolean limitIsFirstParameter() {
        return false;
    }

}
