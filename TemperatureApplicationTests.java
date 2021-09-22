package com.city.temperature;

import com.city.temperature.service.TemperatureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class TemperatureApplicationTests {

    @Autowired
    TemperatureService temperatureService;

    @Test
    void contextLoads() {
        for(int i= 0;i<1;i++){
            new Thread(()->{
                Optional<Integer> optional = temperatureService.getTemperature("10119","04","01");
                System.out.println("111111");
            }).start();
        }

    }

}
