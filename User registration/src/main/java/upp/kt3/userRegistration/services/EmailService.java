package upp.kt3.userRegistration.services;

import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements JavaDelegate {

	@Autowired
	IdentityService identityService;
	
	@Autowired
	TaskService taskService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		String username = (String) execution.getVariable("username");
		String email = (String) execution.getVariable("email");
		String firstName = (String) execution.getVariable("firstname");
		String lastName = (String) execution.getVariable("lastname");
		
		Task task = taskService.createTaskQuery().processInstanceId(execution.getProcessInstanceId()).list().get(0);
		
		// Recipient's email ID needs to be mentioned.
	    String to = email;

	      // Sender's email ID needs to be mentioned
	     String from = "web@gmail.com";

	      // Assuming you are sending email from localhost
	     String host = "localhost";

	      // Get system properties
	     Properties properties = System.getProperties();

	      // Setup mail server
	     properties.setProperty("mail.smtp.host", "smtp.gmail.com");    
	     properties.setProperty("mail.smtp.socketFactory.port", "465");    
	     properties.setProperty("mail.smtp.socketFactory.class",    
                   "javax.net.ssl.SSLSocketFactory");    
	     properties.setProperty("mail.smtp.auth", "true");    
	     properties.setProperty("mail.smtp.port", "465");
	     
	     properties.setProperty("mail.user", "");
	     properties.setProperty("mail.password", "");
	     
	      // Get the default Session object.
	     Session session = Session.getDefaultInstance(properties);

	     try {
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(session);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(from));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("Account activation for " + username);
	         
	         // Now set the actual message
	         String bodyText = "Hello " + firstName + " " + lastName;
	         bodyText += "\nActivate your account on the link: ";
	         bodyText += "http://localhost:8080/activate/" + username + "/" + task.getId();
	         
	         message.setText(bodyText);

	         // Send message
	         Transport.send(message);
	         System.out.println("Mail sent successfully");
	     } catch (MessagingException mex) {
	         mex.printStackTrace();
	     }
		
	}
}
