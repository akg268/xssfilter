package com.example.sanitizeinputdemo.restresources;


import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting")
public class Greetings {

    private static final String[] INIT_GREETINS = new String[] {"hi","hello"} ;
    private static final Set<String> GREETINGS= new HashSet<String>(Arrays.asList(INIT_GREETINS));
    @GetMapping
    public String sayHello(){
        SecureRandom ran = new SecureRandom();
        int randomNumber = ran.nextInt(GREETINGS.size());
        int index=0;
        for(String greeting : GREETINGS){
            System.out.println("Greeting:"+ greeting);
            System.out.println("index:"+ index);
            System.out.println("randomNumber:"+ randomNumber);
          if(randomNumber == index){
            return greeting;
          }
          index++;
        }
        return "Default Greeting";
    }
    @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
    public void saveGreeting(@RequestBody String greeting){
       System.out.println(greeting);
       GREETINGS.add(greeting);
    }
    
}
