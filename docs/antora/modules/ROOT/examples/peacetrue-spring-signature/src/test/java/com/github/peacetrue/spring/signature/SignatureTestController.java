package com.github.peacetrue.spring.signature;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author peace
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
class SignatureTestController {

    @ResponseBody
    @RequestMapping(method = {GET, DELETE}, value = "/echo")
    public String echoForURL(String input) {
        return input;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestBean {
        private String input;
    }

    @ResponseBody
    @RequestMapping(value = "/echo", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String echoForForm(TestBean testBean) {
        return testBean.getInput();
    }

    @ResponseBody
    @RequestMapping(value = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String echoForJSON(@RequestBody TestBean testBean) {
        return testBean.getInput();
    }

}
