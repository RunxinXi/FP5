package edu.toronto.dbservice.types;

import java.io.Serializable;

public class ClientRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer clientNum;
	private String item;
	private Integer quantity;
	
	public ClientRequest(Integer pClientNum, String pItem, Integer Quantity) {
		clientNum = pClientNum;
		item = pItem;
		quantity=Quantity;
		
	}
	
	public Integer getClientNum() {
		return clientNum;
	}
	
	public String getItem() {
		return item;
	}
	public Integer getQuantity() {
		return quantity;
	}

}