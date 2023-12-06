package mie.ether_example;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.ClientRequest;
import edu.toronto.dbservice.types.EtherAccount;


public class CheckIsClient implements JavaDelegate {
	Connection conn = null;
	
	public CheckIsClient() {
		conn = MIE354DBHelper.getDBConnection();
	}
    @Override
    public void execute(DelegateExecution execution)  {
        // Assuming 'currentAccountId' is a process variable holding the account ID
    	 ClientRequest requestId = (ClientRequest) execution.getVariable("currentClientRequest");
    	 int currentAccountId=requestId.getClientNum();
        try (Connection connection =  MIE354DBHelper.getDBConnection();) {
            String query = "SELECT isClient FROM AccountDetails WHERE accountId = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setInt(1, currentAccountId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        boolean isClient = resultSet.getBoolean("isClient");
                        execution.setVariable("isClient", isClient);
                    } else {
                        // Handle the case where the account is not found
                        execution.setVariable("isClient", false);
                    }
                }
            }catch (SQLException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        } // Handle exceptions as necessary
 catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}
