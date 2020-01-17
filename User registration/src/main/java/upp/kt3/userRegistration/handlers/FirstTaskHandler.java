package upp.kt3.userRegistration.handlers;

import java.util.List;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;

public class FirstTaskHandler implements TaskListener {
	@Autowired
	IdentityService identityService;
	
	  public void notify(DelegateTask delegateTask) {
		  System.out.println("Kreiran prvi task");
		  List<User> users = identityService.createUserQuery().userIdIn("pera", "mika", "zika").list();
			
		  users = identityService.createUserQuery().list();
		  DelegateExecution execution = delegateTask.getExecution();
		  execution.setVariable("users", users);
		  
	  }

}
