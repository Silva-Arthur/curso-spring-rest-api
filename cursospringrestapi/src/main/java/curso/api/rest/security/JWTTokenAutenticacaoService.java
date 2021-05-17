package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	/*Tempo de validade do Token 2 dias*/
	private static final long EXPIRATION_TIME = 172800000;
	
	/*Senha única para compor a autenticação, com pr exemplo, uma assinatura
	 * de certificado digital*/
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	/*Prefixo padrão de Token*/
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	
	/*Gerando toke de atutenticação e adicionando ao cabeçalho e resposta HTTP
	 * que vai voltar pro navegador*/
	public void addAuthentication(HttpServletResponse response, String username)
		throws IOException{
		
		/*Montagem do token*/
		String JWT = Jwts.builder() /*Chama o gerador de token*/ 
				.setSubject(username) /*adiciona o usuario*/
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /*Tempo de expiração*/
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Compactação e algoritmo de geração de senha*/
		
		/*Junta o token com o prefixo*/
		String token = TOKEN_PREFIX + " " + JWT; /*Exemplo: Bearer dsaodjsiosdjifos5f41sa74*/
		
		/*Adiciona no cabeçalho http*/
		response.addHeader(HEADER_STRING, token); /*Exemplo: Authorization: Bearer dsajdsiodjsiaodjsi*/
		
		/*Escreve o token como resposta no corpo do http também em forma de JSON*/
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
	}
	
	/*Retorna o usuário validado com token ou caso não seja válido retorna null*/
	public Authentication getAuthentication(HttpServletRequest request) {
		
		/*Pega o token enviado no cabeçalho http*/
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {
			/*Faz a validação do token do usuário na requisição*/
			String user = Jwts.parser()
					.setSigningKey(SECRET) /*Bearer dsajdsiodjsiaodjsi*/
					.parseClaimsJws(token.replace(TOKEN_PREFIX, "")) /*dsajdsiodjsiaodjsi*/
					.getBody().getSubject(); /*tira tudo e Pega o usuario, exemplo: Arthur*/
			
			if (user != null) {
				Usuario usuario = ApplicationContextLoad.getApplicationContext()
						.getBean(UsuarioRepository.class).findUserByLogin(user);
				
				if (usuario != null) {
					/*Retorna o usuário logado*/
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(), 
							usuario.getSenha(), 
							usuario.getAuthorities());
				}
			} 
		} 
		return null; /*Não autorizado*/
	}
	
}
