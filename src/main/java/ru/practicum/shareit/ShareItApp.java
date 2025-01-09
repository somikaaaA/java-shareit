package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//надо будет удалить DataSourceAutoConfiguration, когда будет подключена база
//иначе не проходят тесты
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@SpringBootApplication
public class ShareItApp {
	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}
}
