package org.deepstudio.api;

import org.deepstudio.Bootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class WebApi {

    public static void run(){
        //创建web接口
//        SpringApplication.run(WebApi.class);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(WebApi.class);
        //不明原因，直接啟動會和java.awt衝突。本人使用swing與awt製作懸浮窗。
        builder.headless(false).run();
    }

}
