package com.rbkmoney.walker.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public class EventListener {

    public static class RestService {

            @RequestMapping(path = "/")
            @ResponseBody
            String home() {
                return "Hello, I am Approver!";
            }

            @RequestMapping(method = RequestMethod.POST)
            @ResponseBody
            String test( @RequestBody String input) {
                System.out.println(input);
                return "Hello World! 2";
            }
    }
}
