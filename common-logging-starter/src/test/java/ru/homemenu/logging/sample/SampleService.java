package ru.homemenu.logging.sample;

import org.springframework.stereotype.Service;

@Service
public class SampleService {

    public String greet(String name) {
        return "Hello, " + name;
    }

    public void boom() {
        throw new IllegalStateException("kaboom");
    }

}
