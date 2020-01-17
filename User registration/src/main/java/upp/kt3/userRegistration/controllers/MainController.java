package upp.kt3.userRegistration.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import upp.kt3.userRegistration.model.FormFieldsDto;
import upp.kt3.userRegistration.model.FormSubmissionDto;
import upp.kt3.userRegistration.model.TaskDto;

@Controller
public class MainController {
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private RepositoryService repositoryService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private FormService formService;
	
	@GetMapping(path = "/startProcess", produces = "application/json")
    public @ResponseBody FormFieldsDto get() {
		//provera da li korisnik sa id-jem pera postoji
		//List<User> users = identityService.createUserQuery().userId("pera").list();
		System.out.println("Retrieving personal fields");
		
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("userRegistration");
		
		Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).list().get(0);

		TaskFormData tfd = formService.getTaskFormData(task.getId());
		List<FormField> properties = tfd.getFormFields();
		
        return new FormFieldsDto(task.getId(), pi.getId(), properties);
    }
	
	@GetMapping(path = "/scientificData/{pid}", produces = "application/json")
    public @ResponseBody FormFieldsDto scientificData(@PathVariable String pid) {
		
		System.out.println("Retrieveing scientific fields");

		Task task = taskService.createTaskQuery().processInstanceId(pid).list().get(0);
		
		TaskFormData tfd = formService.getTaskFormData(task.getId());
		List<FormField> properties = tfd.getFormFields();
		
        return new FormFieldsDto(task.getId(), pid, properties);
    }
	
	@GetMapping(path = "/activate/{username}/{pid}", produces = "application/json")
    public @ResponseBody ResponseEntity<Object> activate(@PathVariable String username, @PathVariable String pid) {
		
		System.out.println("Activating " + username);

		Task task = taskService.createTaskQuery().processInstanceId(pid).list().get(0);
		taskService.complete(task.getId());
		
        return new ResponseEntity<>(HttpStatus.OK);
    }
	
	@PostMapping(path = "/post/{taskId}", produces = "application/json", consumes = "application/json")
    public @ResponseBody ResponseEntity<Object> post(@RequestBody List<FormSubmissionDto> dto, @PathVariable String taskId) {
		System.out.println("Submitting data");
		
		HashMap<String, Object> map = this.mapListToDto(dto);
		
		    // list all running/unsuspended instances of the process
//		    ProcessInstance processInstance =
//		        runtimeService.createProcessInstanceQuery()
//		            .processDefinitionKey("Process_1")
//		            .active() // we only want the unsuspended process instances
//		            .list().get(0);
		
//			Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list().get(0);
		
		
		
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		
		for(FormSubmissionDto temp : dto) {
			if(temp.getFieldId().equals("scientificField")) {
				List<String> scientificFields = (List<String>) runtimeService.getVariable(processInstanceId, "scientificFields");
				if(scientificFields == null) {
					scientificFields = new ArrayList<String>();
				}
				scientificFields.add(temp.getFieldValue());
				runtimeService.setVariable(processInstanceId, "scientificFields", scientificFields);
			}
			else {
				if(temp.getFieldId().equals("username")) {
					List<User> users = identityService.createUserQuery().userIdIn(temp.getFieldValue()).list();
					if(!users.isEmpty()) {
						return new ResponseEntity<>("User with given username alredy exists", HttpStatus.BAD_REQUEST);
					}
				}
				runtimeService.setVariable(processInstanceId, temp.getFieldId(), temp.getFieldValue());
			}
		}
		//runtimeService.setVariable(processInstanceId, "registration", map);
		formService.submitTaskForm(taskId, map);
        return new ResponseEntity<>(HttpStatus.OK);
    }
	
	@GetMapping(path = "/tasks/claim/{taskId}", produces = "application/json")
    public @ResponseBody ResponseEntity<Object> claim(@PathVariable String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		String user = (String) runtimeService.getVariable(processInstanceId, "username");
		taskService.claim(taskId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
	
	@GetMapping(path = "/tasks/complete/{taskId}", produces = "application/json")
    public @ResponseBody ResponseEntity<List<TaskDto>> complete(@PathVariable String taskId) {
		Task taskTemp = taskService.createTaskQuery().taskId(taskId).singleResult();
		taskService.complete(taskId);
		List<Task> tasks = taskService.createTaskQuery().processInstanceId(taskTemp.getProcessInstanceId()).list();
		List<TaskDto> dtos = new ArrayList<TaskDto>();
		for (Task task : tasks) {
			TaskDto t = new TaskDto(task.getId(), task.getName(), task.getAssignee());
			dtos.add(t);
		}
        return new ResponseEntity<List<TaskDto>>(dtos, HttpStatus.OK);
    }
	
	private HashMap<String, Object> mapListToDto(List<FormSubmissionDto> list)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(FormSubmissionDto temp : list){
			map.put(temp.getFieldId(), temp.getFieldValue());
		}
		
		return map;
	}
}
