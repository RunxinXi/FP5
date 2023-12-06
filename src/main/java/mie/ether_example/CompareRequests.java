package mie.ether_example;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import edu.toronto.dbservice.types.ClientRequest;

public class CompareRequests implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Assuming currentRequest is a map with keys 'itemType' and 'quantity'
    	ClientRequest requestId = (ClientRequest) execution.getVariable("currentClientRequest");

        // Retrieve the item type and requested quantity from the current request
        String itemType = (String) requestId.getItem();
        int requestedQuantity = (Integer) requestId.getQuantity();

        // Retrieve inventory quantities
        String quantityA = (String)execution.getVariable("QuantityA");
        String quantityB = (String) execution.getVariable("QuantityB");
        String quantityC = (String) execution.getVariable("QuantityC");
        
        String item= (String)execution.getVariable("currentitem");

        int intQuantityA = Integer.parseInt(quantityA);
        int intQuantityB = Integer.parseInt(quantityB);
        int intQuantityC = Integer.parseInt(quantityC);
        // Check if the requested quantity is smaller than the inventory
        boolean canFulfill = false;
        
        switch (item) {
            case "A":
                canFulfill = requestedQuantity <= intQuantityA;
                break;
            case "B":
                canFulfill = requestedQuantity <= intQuantityB;
                break;
            case "C":
                canFulfill = requestedQuantity <= intQuantityC;
                break;
        }
        System.out.println("---------------------------------------------");
        System.out.println("itemType"+item);
        System.out.println("Requested quantity"+requestedQuantity);
        System.out.println("Inventory quantityA"+intQuantityA);
        System.out.println("Inventory quantityB"+intQuantityB);
        System.out.println("Inventory quantityC"+intQuantityC);
        // Set a process variable indicating whether the request can be fulfilled
        execution.setVariable("canFulfillRequest", canFulfill);
        execution.setVariable("itemType", item);
    }
}
