package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDatailsService;

/*Mapeando como um controller REST*/
@RestController
/*mapeando minha classe com a URI*/
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ImplementacaoUserDatailsService implementacaoUserDatailsService;
	
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
	public ResponseEntity<Usuario> initV1(
			@PathVariable(value = "id") Long id,
			@RequestParam(value = "nome", defaultValue = "Nome não informado", required = false) String nome,
			@RequestParam(value = "salario", required = false) Long salario){
		System.out.println("Versão 01");
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/* 'value = /' quer dizer que estamos mapeando direto na raiz*/
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2")
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
	public ResponseEntity<Page<Usuario>> usuarios() throws InterruptedException{
		
		/*Primeira página trás 5 registros*/
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		
		Page<Usuario> lista = usuarioRepository.findAll(page);
		
		return new ResponseEntity<Page<Usuario>>(lista, HttpStatus.OK);
	}
	
	/*Grava no banco de dados e retorna o objeto que foi salvo no banco*/
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {		
		for (Telefone telefone : usuario.getTelefones()) {
			telefone.setUsuario(usuario);
		}
		
		/*Consumindo API pública externa - INICIO*/
		if (usuario.getCep() != null && !usuario.getCep().isEmpty()) {
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
		}
		
		/*Consumindo API pública externa - FIM*/
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		implementacaoUserDatailsService.insereAcessoPadrao(usuarioSalvo.getId());
		
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
		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();
		
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
	
	@SuppressWarnings({"rawtypes"})
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CacheEvict(value="cacheusuarios", allEntries = true) // nao permite que o chache fique muito tempo setado e tras novas atualizações ele tras
	@CachePut("cacheusuarios") // identifica que tem novas atualizaç~eos e tras
	public ResponseEntity pesquisarByNome(@PathVariable(value = "nome") String nome) {
		
		PageRequest page = null;
		Page<Usuario> usuarios = null; 
		
		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			page = PageRequest.of(0, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findAll(page);
		} else {
			page = PageRequest.of(0, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findUserByNome(nome, page);
		}
		
		
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		
		telefoneRepository.deleteById(id);
		
		return "ok";
	}
	
	/*Vamos supor que o carregamento de usuário seja um processo lento e
	 * queremos controlar ele com cache para agilizar o processo*/
	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@CacheEvict(value="cacheusuarios", allEntries = true) // nao permite que o chache fique muito tempo setado e tras novas atualizações ele tras
	@CachePut("cacheusuarios") // identifica que tem novas atualizaç~eos e tras
	public ResponseEntity<Page<Usuario>> usuariosPagina(@PathVariable(value = "pagina") int pagina) throws InterruptedException{
		
		/*Primeira página trás 5 registros*/
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		Page<Usuario> lista = usuarioRepository.findAll(page);
		
		return new ResponseEntity<Page<Usuario>>(lista, HttpStatus.OK);
	}
	
	@SuppressWarnings({"rawtypes"})
	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CacheEvict(value="cacheusuarios", allEntries = true) // nao permite que o chache fique muito tempo setado e tras novas atualizações ele tras
	@CachePut("cacheusuarios") // identifica que tem novas atualizaç~eos e tras
	public ResponseEntity pesquisarByNomePage(
			@PathVariable(value = "nome") String nome,
			@PathVariable(value = "page") int page) {
		
		PageRequest pageRequest = null;
		Page<Usuario> usuarios = null; 
		
		if (nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			usuarios = usuarioRepository.findUserByNome(nome, pageRequest);
		}
		return new ResponseEntity<Page<Usuario>>(usuarios, HttpStatus.OK);
	}
}
