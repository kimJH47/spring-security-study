package com.springsecuritystudy.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilterTest {
    @LocalServerPort
    int port;

    RestTemplate client = new RestTemplate();

    private String greetingUrl(){
        return "http://localhost:"+port+"/greeting";
    }

    @DisplayName("3 testRestTemplate 활용")
    @Test
    void useTestRestTemplate(){
        //테스트용 restTemplate
        //파라미터로 username 과 password를 넘기면
        //알아서 Authorization 헤더를 만들어준다
        TestRestTemplate template = new TestRestTemplate("user1","1111" );
        String resp = template.getForObject(greetingUrl(), String.class);
        assertEquals(resp , "hello");
    }
}
