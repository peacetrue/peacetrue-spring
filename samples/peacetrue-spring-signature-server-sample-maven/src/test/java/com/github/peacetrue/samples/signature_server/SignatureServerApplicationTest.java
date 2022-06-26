package com.github.peacetrue.samples.signature_server;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author peace
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SignatureServerApplicationTest {

    @Autowired
    private List<RestTemplateCustomizer> restTemplateCustomizers;

    @Test
    public void basic() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplateCustomizers.forEach(item -> item.customize(restTemplate));
        String input = "1";
        String response = restTemplate.getForObject("http://localhost:8080/echo?input={0}", String.class, input);
        Assert.assertEquals(response, input);
    }
}
