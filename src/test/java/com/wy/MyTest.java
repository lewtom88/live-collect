package com.wy;

import com.alibaba.fastjson.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyTest {

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    //@Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        JSONArray array = new JSONArray();
        JSONArray c1 = new JSONArray();
        c1.add("name1");
        c1.add("abc");
        array.add(array);

        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "",
                String.class, "1", array.toJSONString())).contains("NO SERVICE!");
    }
}
