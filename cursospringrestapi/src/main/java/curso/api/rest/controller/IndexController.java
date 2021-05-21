package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
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

import com.google.gson.Gson;

import curso.api.rest.model.Telefone;
import curso.api.rest.model.Usuario;
import curso.api.rest.model.UsuarioDTO;
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
	public ResponseEntity<UsuarioDTO> initV2(
			@PathVariable(value = "id") Long id,
			@RequestParam(value = "nome", defaultValue = "Nome não informado", required = false) String nome,
			@RequestParam(value = "salario", required = false) Long salario){
		System.out.println("Versão 02");
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	/*Vamos supor que o carregamento de usuário seja um processo lento e
	 * queremos controlar ele com cache para agilizar o processo*/
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value="cacheusuarios", allEntries = true) // nao permite que o chache fique muito tempo setado e tras novas atualizações ele tras
	@CachePut("cacheusuarios") // identifica que tem novas atualizaç~eos e tras
	public ResponseEntity<List<Usuario>> usuarios() throws InterruptedException{
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	/*Grava no banco de dados e retorna o objeto que foi salvo no banco*/
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {		
		for (Telefone telefone : usuario.getTelefones()) {
			telefone.setUsuario(usuario);
		}
		
		/*Consumindo API pública externa - INICIO*/
		URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		while ((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		System.out.println(jsonCep.toString());
		
		/*converte o texto em um json e do json para o objeto usuario*/
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		
		/*Consumindo API pública externa - FIM*/
		
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
