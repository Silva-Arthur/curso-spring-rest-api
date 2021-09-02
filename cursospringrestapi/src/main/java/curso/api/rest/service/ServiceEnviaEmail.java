package curso.api.rest.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class ServiceEnviaEmail {
	
	private String userName = "pereiras.oarthur@gmail.com";
	private String senha = "pantokrator";
	
	public void enviarEmail(String assunto, String emailDestino, String mensagem) throws Exception {
		
		Properties properties = new Properties();
		
		properties.put("mail.smtp.auth", "true"); /*Autorização*/
		properties.put("mail.smtp.starttls", "true"); /*Autenticação*/
		properties.put("mail.smtp.host", "smt.gmail.com"); /*Servidor do Google*/
		properties.put("mail.smtp.port", "465"); /*Porta do servidor*/
		properties.put("mail.smtp.socketFactory.port", "465"); /*Especifica a porta socket*/
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); /*Classe de conexão socket*/
		
		/*Autentica o usuario do email remetente*/
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, senha);
			}
		});
		
		/*Array dos Destinarios*/
		Address[] toUser = InternetAddress.parse(emailDestino);
		
		Message message = new MimeMessage(session);
		
		/*Remetente*/
		message.setFrom(new InternetAddress(userName));
		
		/*Destinario*/
		message.setRecipients(Message.RecipientType.TO, toUser);
		
		/*Assunto*/
		message.setSubject(assunto);
		
		/*Texto do email*/
		message.setText(mensagem);
		
		/*Envio do e-mail*/
		Transport.send(message);
	}

}
