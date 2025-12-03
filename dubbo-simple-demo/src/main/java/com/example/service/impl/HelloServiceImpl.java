package com.example.service.impl;

import com.example.service.HelloService;
import org.springframework.stereotype.Service;

@Service("helloService")  // Spring Bean名称
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "! (from Dubbo Container)";
    }
}