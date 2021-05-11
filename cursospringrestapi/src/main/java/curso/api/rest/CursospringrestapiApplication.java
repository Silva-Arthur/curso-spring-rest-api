package curso.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/*Faz com que o spring dê o start no servidor*/
@SpringBootApplication

/*Lê todas as classes desse pacote e cria as tabelas no banco*/
@EntityScan(basePackages = {"curso.api.rest.model"})

/*Permite que o spring controle todos os objetos, fazendo por exemplo, injeção de dependências*/
@ComponentScan(basePackages = {"curso.*"})

/*Habilitando a parte de persistência */
@EnableJpaRepositories(basePackages = {"curso.api.rest.repository*"})

/*Gerência de transações*/
@EnableTransactionManagement

/*Habilita também o MVC*/
@EnableWebMvc

/*Habilitar o rest, para os controllers retornarem JSON*/
@RestController

/*Spring configurará o projeto para nós*/
@EnableAutoConfiguration

public class CursospringrestapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CursospringrestapiApplication.class, args);
	}

}
