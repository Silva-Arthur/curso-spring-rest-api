package curso.api.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*Mapeando como um controller REST*/
@RestController
/*mapeando minha classe com a URI*/
@RequestMapping(value = "/usuario")
public class IndexController {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	/* 'value = /' quer dizer que estamos mapeando direto na raiz*/
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity init(
			@RequestParam(value = "nome", defaultValue = "Nome não informado", required = false) String nome,
			@RequestParam(value = "salario") Long salario){
		
		System.out.println("Parametro recebido: " + nome);
		
		return new ResponseEntity("Olá usuário REST Spring Boot, seu nome é: " + nome + 
				" e o seu salário é: " + salario, HttpStatus.OK);
	}
}
