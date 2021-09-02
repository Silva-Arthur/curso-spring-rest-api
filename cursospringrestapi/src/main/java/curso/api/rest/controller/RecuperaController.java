package curso.api.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.ObjetoErro;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) {
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		
		if (user == null) {
			objetoErro.setCode("404"); /*Não encontrado*/
			objetoErro.setError("Usuário não encontrado");
		} else {
			/*Rotina de envio de e-mail*/
			objetoErro.setCode("200"); /*Não encontrado*/
			objetoErro.setError("Acesso enviado para seu e-mail");
		}
		
		return new ResponseEntity<ObjetoErro>(objetoErro, HttpStatus.OK);
	}
	
	
}
