package br.com.alura.idilom.literalura;


import br.com.alura.idilom.literalura.main.Menu;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LiterAluraApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(LiterAluraApplication.class, args);

		Menu menu = context.getBean(Menu.class);

		menu.exibir();
	}
}
