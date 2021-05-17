package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Telefone;
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
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
	public ResponseEntity<Usuario> initV1(
			@PathVariable(value = "id") Long id,
			@RequestParam(value = "nome", defaultValue = "Nome não informado", required = false) String nome,
			@RequestParam(value = "salario", required = false) Long salario){
		System.out.println("Versão 01");
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/* 'value = /' quer dizer que estamos mapeando direto na raiz*/
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2" )
	public ResponseEntity<Usuario> initV2(
			@PathVariable(value = "id") Long id,
			@RequestParam(value = "nome", defaultValue = "Nome não informado", required = false) String nome,
			@RequestParam(value = "salario", required = false) Long salario){
		System.out.println("Versão 02");
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> usuarios(){
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	/*Grava no banco de dados e retorna o objeto que foi salvo no banco*/
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		
		for (Telefone telefone : usuario.getTelefones()) {
			telefone.setUsuario(usuario);
		}
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = "/{idUser}/idVenda/{idVenda}", produces = "application/json")
	public ResponseEntity<Usuario> cadastrarVenda(
			@PathVariable(value = "idUser") Long idUser,
			@PathVariable(value = "idVenda") Long idVenda) {
		
		/*Aqui seria o processo de venda*/
		
		return new ResponseEntity(
				"idUser: " + idUser  + " idVenda: " + idVenda, HttpStatus.OK);
	}
	
	/*Atualiza no banco de dados e retorna o objeto que foi salvo no banco*/
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		/*Outras rotinas antes de atualizar*/
		for (Telefone telefone : usuario.getTelefones()) {
			telefone.setUsuario(usuario);
		}
		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if (!userTemporario.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public ResponseEntity deletar(@PathVariable(value = "id") Long id){
		
		usuarioRepository.deleteById(id);
		
		return new ResponseEntity("apagou!", HttpStatus.OK);
	}
	
}
