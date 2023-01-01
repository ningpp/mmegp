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
package me.ningpp.mmegp.enums;

public enum QueryType {

    /**
     * {@code = ? }
     */
    EQUAL, 

    /**
     * {@code <> ? }
     */
    NOT_EQUAL,


    /**
     * {@code is null }
     */
    IS_NULL, 

    /**
     * {@code is not null }
     */
    IS_NOT_NULL,


    /**
     * {@code between ? and ? }
     */
    BETWEEN_AND,

    /**
     * {@code not between ? and ? }
     */
    NOT_BETWEEN_AND,


    /**
     * {@code in (?, ?) }
     */
    IN,

    /**
     * {@code not in (?, ?) }
     */
    NOT_IN, 


    /**
     * {@code like "%searchWord%" }
     */
    LIKE,

    /**
     * {@code like "searchWord%" }
     */
    STARTS_WITH,

    /**
     * {@code like "%searchWord" }
     */
    ENDS_WITH,

    /**
     * {@code not like "%searchWord%" }
     */
    NOT_LIKE,


    /**
     * {@code = "" }
     */
    IS_EMPTY,

    /**
     * {@code <> "" }
     */
    IS_NOT_EMPTY,


    /**
     * {@code > ? }
     */
    GREATER_THAN,

    /**
     * {@code >= ? }
     */
    GREATER_THAN_OR_EQUAL_TO,

    /**
     * {@code < ? }
     */
    LESS_THAN,

    /**
     * {@code <= ? }
     */
    LESS_THAN_OR_EQUAL_TO

}
