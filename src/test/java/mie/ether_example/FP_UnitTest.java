package mie.ether_example;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.toronto.dbservice.types.EtherAccount;

import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;


/* Run with parameters */
@RunWith(Parameterized.class)
public class FP_UnitTest extends LabBaseUnitTest {
	
	@BeforeClass
	public static void setupFile() {
		filename = "src/main/resources/diagrams/Final_Project.bpmn";
	}
	
	/* START OF PARAMETERIZED CODE */
	private String ItemName;
	private String Quantity;
	private String numtasks1;
	private String numtasks2;
	
	/* Constructor has two string parameters */
	public FP_UnitTest(String itemParam, String ownerParam, String numtasks1, String numtasks2) {
		this.ItemName = itemParam;
		this.Quantity = ownerParam;
		this.numtasks1=numtasks1;
		this.numtasks2=numtasks2;
	}
	
	/* Setup parameters to provide pairs of strings to the constructor */
	@Parameters
	public static Collection<String[]> data() {
		ArrayList<String[]> parameters = new ArrayList<>();
		parameters.add(new String[] {"A", "3","1","2"});
//		parameters.add(new String[] {"b", "3"});
//		parameters.add(new String[] {"A", "3"});
		return parameters;
	}
	/* END OF PARAMETERIZED CODE */
	
	private void startProcess() {	
		RuntimeService runtimeService = flowableContext.getRuntimeService();
		processInstance = runtimeService.startProcessInstanceByKey("process_pool1");
		System.out.println("process_pool1===================");
	}
	
	private void fillInventory(String quantity) {
	    // form fields are filled using a map from field ids to values
	    Map<String, String> formEntries = new HashMap<>();
	    formEntries.put("QuantityA", quantity);
	    formEntries.put("QuantityB", quantity);
	    formEntries.put("QuantityC", quantity);
	    // get the user task "select audited item"
	    TaskService taskService = flowableContext.getTaskService();
	    List<Task> proposalsTasks = taskService.createTaskQuery().taskDefinitionKey("usertask1").list();

	    // Iterate over each task and submit the form data
	    for (Task task : proposalsTasks) {
	        // get the list of fields in the form
	        List<String> bpmnFieldNames = new ArrayList<>();
	        TaskFormData taskFormData = flowableContext.getFormService().getTaskFormData(task.getId());
	        for (FormProperty fp : taskFormData.getFormProperties()) {
	            bpmnFieldNames.add(fp.getId());
	        }

	        // build a list of required fields that must be filled
	        List<String> requiredFields = new ArrayList<>(
	                Arrays.asList("QuantityA"));
	        requiredFields.add("QuantityB");
	        requiredFields.add("QuantityC");
	        // make sure that each of the required fields is in the form
	        for (String requiredFieldName : requiredFields) {
	            assertTrue(bpmnFieldNames.contains(requiredFieldName));
	        }
	        for (String requiredFieldName : requiredFields) {
	            System.out.println(requiredFieldName);
	        }
	        // make sure that each of the required fields was assigned a value
	        for (String requiredFieldName : requiredFields) {
	            assertTrue(formEntries.keySet().contains(requiredFieldName));
	        }

	        // submit the form (will lead to completing the user task)
	        flowableContext.getFormService().submitTaskFormData(task.getId(), formEntries);
	    }
	}

	
	@SuppressWarnings("unchecked")
	private void auditItem(Integer expectedClientId) {
		/* get audited item owner */
		String auditedItemOwner = (String) flowableContext.getRuntimeService().getVariableLocal(processInstance.getId(), "auditedItemOwner");
		
		/* get expected owner address */
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) flowableContext.getRuntimeService().getVariableLocal(processInstance.getId(), "accounts");
		String expectedOwner = accounts.get(expectedClientId).getCredentials().getAddress();
		assertTrue(auditedItemOwner.equals(expectedOwner));
	}
	@Test
	public void testProcessStarted()
	{
		startProcess();
	}
//	Test process started and paused at the first user task
	@Test
	public void checkRegisterAndPaused() {
		/* Check process is paused at usertask1 */
		startProcess();
		assertNotNull(processInstance);
		
		/* get pending tasks */
		List<Task> list = flowableContext.getTaskService().createTaskQuery()
				.list();
		
		/* assert 3 pending tasks */ 
		//because it is a multi-instance sub-process 
		assertTrue(list.size() == 3);
		
		/* assert pending task id */
		assertTrue(list.get(0).getTaskDefinitionKey().equals("usertask1"));
	}
	

//	o Test process ends
	@Test
	public void ProcessEnded() {
		/* Check process is paused at usertask1 */
		startProcess();
		//usertask1
		fillInventory(this.Quantity);
		//second user task that checks number of pending tasks after the first pattern
		int nt1=Integer.parseInt(this.numtasks1);
		int nt2=Integer.parseInt(this.numtasks2);
		CheckandFinishNumTasks(nt1);
		//third user task that check number of pending tasks after inclusive
		CheckandFinishNumTasks(nt2);
		assertPendingTaskSize(0);
		
	}
	
	//checks and finishes current user task(which is going to count the number of pending tasks)
	
	public void CheckandFinishNumTasks(int Num) {
		List<Task> list = flowableContext.getTaskService().createTaskQuery()
				.list();
		System.out.println("list size"+list.size());
		System.out.println("Task name"+list.get(0).getTaskDefinitionKey());
		assertPendingTaskSize(Num);
		for (Task task : list) {
	        // You need to replace "ItemName" with the actual variable or value you want to use for completion
	        flowableContext.getTaskService().complete(task.getId());
	    }
	}
	
//	o Test for a complex type handled correctly
//	o Test for a form filled and submitted correctly
//	o Tests for non-empty lists used in the collections corresponding to the
//	multi-instance activities and sub-processes
//	o Tests that Service tasks access and modify the database correctly
//	o Tests for each of the start-end patterns (as described earlier)
//	o Parameterized tests to test at least 2 different cases for each of the start-
//	end patterns
//	o Tests to verify the contents in the blockchain registry (by calling the smart
//	contract API)
//	
//	@Test
//	public void checkRegisterAndAudit() {
//		startProcess();
//		fillAuditForm(anItemParameter);
//		auditItem(Integer.valueOf(anOwnerParameter));
//		
//		/* assert process ended */
//		HistoryService historyService = flowableContext.getHistoryService();
//		HistoricProcessInstance historicProcessInstance = historyService
//				.createHistoricProcessInstanceQuery()
//				.processInstanceId(processInstance.getId()).singleResult();
//		assertNotNull(historicProcessInstance);
//
//		System.out.println("Process instance end time: "
//				+ historicProcessInstance.getEndTime());
//	}
	
//	@Test
//	public void checkRegisterRecordedInDB() throws SQLException {
//		/* Start process. will pause at usertask1 */
//		startProcess();
//		
//		/* Check records in Registered table match records in the Request table */
//		
//		Statement statement;
//		ResultSet resultSet;
//		
//		/* First, we load all requested items into a list */
//		ArrayList<String> requestedItems = new ArrayList<>();
//		statement = dbCon.createStatement();
//		resultSet = statement.executeQuery("SELECT * FROM Request");
//		while (resultSet.next()) {
//			String item = resultSet.getString("item");
//			requestedItems.add(item);
//		}
//		
//		/* Then, we load all registered items into a list */
//		ArrayList<String> registeredItems = new ArrayList<>();
//		statement = dbCon.createStatement();
//		resultSet = statement.executeQuery("SELECT * FROM Registered");
//		while (resultSet.next()) {
//			String item = resultSet.getString("item");
//			registeredItems.add(item);
//		}
//		
//		/* Finally, we make sure that each requested item is registered, and no extra item was registered */
//		for (String item : registeredItems) {
//			assertTrue(registeredItems.contains(item));
//		}
//		assertTrue(registeredItems.size() == requestedItems.size());
//	}
//	@Test
//	public void checkProcessEnded() {
//		startProcess();
//		//completeAllPendingTasks();
//		assertPendingTaskSize(0);
//	}
//	@Test
//	public void checkTask1() {
//		assertPendingTaskSize(1);
//	}
	private void assertPendingTaskSize(int num) {
		List<Task> list3 = flowableContext.getTaskService().createTaskQuery().list();
		assertTrue(list3.size() == num);
	}
////	private boolean checkWinner() {
////		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) flowableContext.getHistoryService()
////				.createHistoricVariableInstanceQuery().variableName("accounts").singleResult().getValue();
////
////		Web3j web3 = Web3j.build(new HttpService());
////		String contractAddress = (String) flowableContext.getHistoryService().createHistoricVariableInstanceQuery()
////				.variableName("contractAddress").singleResult().getValue();
////		Ballot ballot = Ballot.load(contractAddress, web3, accounts.get(0).getCredentials(), EtherUtils.GAS_PRICE,
////				EtherUtils.GAS_LIMIT_CONTRACT_TX);
////		
////		int winner = 0;
////		try {
////			winner = ballot.winningProposal().get().getValue().intValue();
////		} catch (InterruptedException | ExecutionException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		
////		return expectedWinner == winner;
////	}

	
}
