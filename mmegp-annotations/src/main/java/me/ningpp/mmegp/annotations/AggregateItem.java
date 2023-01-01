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
package me.ningpp.mmegp.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.ningpp.mmegp.enums.AggregateFunction;
import me.ningpp.mmegp.enums.DistinctStrategy;

@Incubating
@Documented
@Inherited
@Target({ })
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateItem {

    /**
     * column name can be empty only when aggregate function is {@link AggregateFunction#COUNT}
     */
    String column() default "";

    AggregateFunction function();

    DistinctStrategy distinct() default DistinctStrategy.AUTO;

}
