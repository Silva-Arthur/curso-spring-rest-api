package curso.api.rest;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

@SpringBootTest
class CursospringrestapiApplicationTests {

	@Autowired
	UsuarioRepository usuario;
	
	@Test
	void contextLoads() {
		List<Usuario> usuarios = usuario.findAll();
		for (Usuario usuario : usuarios) {
			System.out.println(usuario);
		}
	}

}
