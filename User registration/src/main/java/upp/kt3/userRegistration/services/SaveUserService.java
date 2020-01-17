package upp.kt3.userRegistration.services;

import java.util.List;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import upp.kt3.userRegistration.model.FormSubmissionDto;

@Service
public class SaveUserService implements JavaDelegate {
	@Autowired
	IdentityService identityService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
	  
		System.out.println("Saving user");
		
		String username = (String) execution.getVariable("username");
		String email = (String) execution.getVariable("email");
		String firstName = (String) execution.getVariable("firstname");
		String lastName = (String) execution.getVariable("lastname");
		String password = (String) execution.getVariable("password");

		User user = identityService.newUser("");
		user.setId(username);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setPassword(password);
		
		identityService.saveUser(user);
	}
}
