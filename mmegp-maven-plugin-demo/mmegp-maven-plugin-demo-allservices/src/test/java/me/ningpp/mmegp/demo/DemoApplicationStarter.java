package me.ningpp.mmegp.demo;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

import me.ningpp.mmegp.demo.service.AllService;

@TestPropertySource(locations= "classpath:application-test.properties")
@SpringBootTest(classes = DemoApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationStarter {
    @Autowired
    protected AllService allService;

    protected static String uuid() {
        return UUID.randomUUID().toString();
    }
}
