package org.deepstudio.api;

import org.deepstudio.Bootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApi {

    public static void run(){
        //创建web接口
        SpringApplication.run(WebApi.class);
    }

}
