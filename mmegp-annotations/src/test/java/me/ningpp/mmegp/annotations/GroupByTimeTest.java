package me.ningpp.mmegp.annotations;

import me.ningpp.mmegp.enums.TimeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@GroupByTimes({
        @GroupByTime(
                timeColumn = "create_time",
                types = TimeType.YEAR
        ),
        @GroupByTime(
                timeColumn = "create_time",
                types = {TimeType.YEAR, TimeType.YEAR_MONTH},
                otherGroupColumns = "city_id"
        )
})
class GroupByTimeTest {

    @Test
    void test() {
        GroupByTime[] values = GroupByTimeTest.class
                .getAnnotation(GroupByTimes.class).value();
        assertEquals(2, values.length);
    }

}
