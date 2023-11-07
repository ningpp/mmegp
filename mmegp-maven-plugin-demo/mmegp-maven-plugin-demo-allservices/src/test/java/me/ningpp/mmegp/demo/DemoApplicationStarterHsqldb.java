package me.ningpp.mmegp.demo;

import me.ningpp.mmegp.demo.service.AllService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

@TestPropertySource(locations= "classpath:application-test-hsqldb.properties")
@SpringBootTest(classes = DemoApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationStarterHsqldb {
    @Autowired
    protected AllService allService;

    protected static String uuid() {
        return UUID.randomUUID().toString();
    }
}
