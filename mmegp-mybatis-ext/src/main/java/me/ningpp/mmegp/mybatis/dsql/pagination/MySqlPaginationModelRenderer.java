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
 * <p>LIMIT n and LIMIT m,n </p>
 */
public class MySqlPaginationModelRenderer extends AbstractPaginationModelRenderer {

    @Override
    protected String limitOnlyPattern() {
        return "LIMIT %s";
    }

    @Override
    protected String limitAndOffsetPattern() {
        return "LIMIT %s, %s";
    }

    @Override
    protected boolean limitIsFirstParameter() {
        return false;
    }

}
