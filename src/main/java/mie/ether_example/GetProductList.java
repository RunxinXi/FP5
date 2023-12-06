
package mie.ether_example;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.ClientRequest;

public class GetProductList implements JavaDelegate {
	public GetProductList() {
	}
	@Override
	public void execute(DelegateExecution execution) {
		
		
		// Selecting the registry request list from the data table
		List<String> itemList= new ArrayList<>();
		itemList.add("A");
		itemList.add("B");
		itemList.add("C");
		execution.setVariable("itemList", itemList);
	}
}
