package br.com.infnet.mslogistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ConfigurationPropertiesScan("br.com.infnet.mslogistics.config")
@EnableAsync
public class MsLogisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsLogisticsApplication.class, args);
    }
}
