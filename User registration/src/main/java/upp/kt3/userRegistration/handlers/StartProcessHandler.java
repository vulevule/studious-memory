package upp.kt3.userRegistration.handlers;

import java.util.List;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StartProcessHandler implements ExecutionListener{
	@Autowired
	IdentityService identityService;
	
	@Override
	public void notify(DelegateExecution execution) throws Exception {

		System.out.println("Process started. Setting users.");
		List<User> users = identityService.createUserQuery().userIdIn("pera", "mika", "zika").list();
		if(users.isEmpty() ) {
			
			User user1 = identityService.newUser("pera");
			user1.setEmail("vjsrbija96@gmail.com");
			user1.setFirstName("Pera");
			user1.setLastName("Peric");
			user1.setPassword("pass");
			identityService.saveUser(user1);
			users.add(user1);
			
			User user2 = identityService.newUser("mika");
			user2.setEmail("vjsrbija96@gmail.com");
			user2.setFirstName("Mika");
			user2.setLastName("Mikic");
			user2.setPassword("pass");
			identityService.saveUser(user2);
			users.add(user2);
			
			User user3 = identityService.newUser("zika");
			user3.setEmail("vjsrbija96@gmail.com");
			user3.setFirstName("Zika");
			user3.setLastName("Zikic");
			user3.setPassword("pass");
			identityService.saveUser(user3);
			users.add(user3);
		}
		
		execution.setVariable("users", users);
		
	}
}
