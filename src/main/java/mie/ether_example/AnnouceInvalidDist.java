package mie.ether_example;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnouceInvalidDist implements JavaDelegate {


    @Override
    public void execute(DelegateExecution execution) {
        // Assuming the process variables are named 'itemType' and 'quantity'
        String itemType = (String) execution.getVariable("itemType");
        

        // print the itemType and quantity
        System.out.println("-------------------------------------");
       System.out.println( "items "+itemType+" are Invalid");

       
    }
}