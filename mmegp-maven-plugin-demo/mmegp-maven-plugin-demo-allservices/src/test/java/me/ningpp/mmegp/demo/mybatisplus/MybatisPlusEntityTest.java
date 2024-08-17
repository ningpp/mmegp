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
package me.ningpp.mmegp.demo.mybatisplus;

import me.ningpp.mmegp.demo.mybatisplusmapper.MybatisPlusEntityDynamicSqlSupport;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.ningpp.mmegp.demo.mybatisplusmapper.MybatisPlusEntityDynamicSqlSupport.content;
import static me.ningpp.mmegp.demo.mybatisplusmapper.MybatisPlusEntityDynamicSqlSupport.id;
import static me.ningpp.mmegp.demo.mybatisplusmapper.MybatisPlusEntityDynamicSqlSupport.mybatisPlusEntity;
import static me.ningpp.mmegp.demo.mybatisplusmapper.MybatisPlusEntityDynamicSqlSupport.title;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MybatisPlusEntityTest {

    @Test
    void parseTest() {
        assertEquals("id", id.name());
        assertEquals("title", title.name());
        assertEquals("mp_content", content.name());
        assertEquals("mybatis_plus_entity", mybatisPlusEntity.tableNameAtRuntime());
        Field[] fields = MybatisPlusEntityDynamicSqlSupport.MybatisPlusEntity.class.getFields();
        // 需要禁止掉jacoco
        assertEquals("id,title,content",
                Stream.of(fields).map(Field::getName).collect(Collectors.joining(",")));
    }

}
