package curso.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.ObjetoErro;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviaEmail serviceEnviaEmail;
	
	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoErro> recuperar(@RequestBody Usuario login) throws Exception {
		ObjetoErro objetoErro = new ObjetoErro();
		
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		
		if (user == null) {
			objetoErro.setCode("404"); /*Não encontrado*/
			objetoErro.setError("Usuário não encontrado");
		} else {
			/*Gerando senha para o usuario*/
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova = dateFormat.format(Calendar.getInstance().getTime());

			/*Criptografando a senha*/
			String senhaCriptografada = new BCryptPasswordEncoder().encode(senhaNova);
			
			/*Atualizando no banco de dados*/
			usuarioRepository.updateSenha(senhaCriptografada, user.getId());
			
			/*Rotina de envio de e-mail*/
			serviceEnviaEmail.enviarEmail("Recuperação de Senha", user.getLogin(), "Sua nova senha é = " + senhaNova);
			
			objetoErro.setCode("200"); /*Encontrado*/
			objetoErro.setError("Acesso enviado para seu e-mail");
		}
		
		return new ResponseEntity<ObjetoErro>(objetoErro, HttpStatus.OK);
	}
	
	
}
