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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimitOffsetTest {

    @Test
    void test() {
        DefaultLimitOffsetImpl lo = new DefaultLimitOffsetImpl();
        assertNull(lo.limit());
        assertNull(lo.offset());

        LimitOffset l1 = LimitOffset.of(null, null);
        assertNull(l1.limit());
        assertNull(l1.offset());

        LimitOffset l2 = LimitOffset.ofLimit(3L);
        assertEquals(3L, l2.limit());
        assertNull(l2.offset());

        LimitOffset l3 = LimitOffset.ofOffset(12L);
        assertNull(l1.limit());
        assertEquals(12L, l3.offset());

        LimitOffset l4 = LimitOffset.of(3L, 12L);
        assertEquals(3L, l4.limit());
        assertEquals(12L, l4.offset());

        assertTrue(Page.empty().getItems().isEmpty());
        assertEquals(0L, Page.empty().getTotalCount());
    }

}
