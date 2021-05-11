package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

/*Mapeando como um controller REST*/
@RestController
/*mapeando minha classe com a URI*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	/* 'value = /' quer dizer que estamos mapeando direto na raiz*/
	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/json")
	public ResponseEntity<Usuario> relatorio(
			@PathVariable(value = "id") Long id,
			@PathVariable(value = "venda") Long venda){
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		/*retorno seria um relatorios*/
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/* 'value = /' quer dizer que estamos mapeando direto na raiz*/
	@GetMapping(value = "/{id}/relatoriopdf", produces = "application/pdf")
	public ResponseEntity<Usuario> relatorio(
			@PathVariable(value = "id") Long id){
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		/*retorno seria um relatorios*/
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	/* 'value = /' quer dizer que estamos mapeando direto na raiz*/
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario> init(
			@PathVariable(value = "id") Long id,
			@RequestParam(value = "nome", defaultValue = "Nome não informado", required = false) String nome,
			@RequestParam(value = "salario", required = false) Long salario){
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarios(){
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
}
