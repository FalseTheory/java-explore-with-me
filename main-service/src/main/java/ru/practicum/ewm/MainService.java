package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.dto.ParamHitDto;

@SpringBootApplication
public class MainService {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MainService.class, args);
        StatClient statClient = context.getBean(StatClient.class);

        ParamHitDto paramHitTest = new ParamHitDto();
        paramHitTest.setIp("192.163.0.1");
        paramHitTest.setUri("/events/1");
        paramHitTest.setApp("ewm-main-service");
        //проверки
        statClient.hit(paramHitTest);
        statClient.getStat("2022-09-06 11:00:23", "2025-09-06 11:00:23", null, null);
    }
}
