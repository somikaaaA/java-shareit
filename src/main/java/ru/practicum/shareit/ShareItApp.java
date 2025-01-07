package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//надо будет удалить DataSourceAutoConfiguration, когда будет подключена база
//иначе не проходят тесты
@SpringBootApplication//(exclude = {DataSourceAutoConfiguration.class })

public class ShareItApp {

	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

}
