package me.ningpp.mmegp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

@TestPropertySource(locations= "classpath:application-test.properties")
@SpringBootTest(classes = TestApplication.class, webEnvironment = WebEnvironment.NONE)
public class TestApplicationStarter {

    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;

    protected static String uuid() {
        return UUID.randomUUID().toString();
    }

}
