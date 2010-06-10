package org.milipede.storage.layer.internal;

import java.util.ArrayList;
import java.util.List;

import org.merapi.internal.Bridge;
import org.milipede.storage.layer.domain.AccountVO;
//import org.milipede.storage.layer.merapi.handlers.ProviderHandler;
//import org.milipede.storage.layer.merapi.messages.ProviderMessage;

/**
 * Performante und thread-safe Implementierung des Singleton-Patterns
 */
public class Assistant {
	private static Assistant instance = new Assistant();

	/**
	 * Default-Konstruktor, der nicht au�erhalb dieser Klasse
	 * aufgerufen werden kann
	 */
	private Assistant() {
		
		//dummy data objects of type ProviderVO
		AccountVO ao1 = new AccountVO();
		ao1.setProviderId(0001);
		ao1.setProviderName("test1");
//		ao1.visualProperties.add("flash");
		AccountVO ao2 = new AccountVO();
		ao2.setProviderId(0002);
		ao2.setProviderName("test2");
//		ao2.visualProperties.add("flex");
//		addAll(new ArrayList(["flash", "flex"]));
		providerList.add(ao1);
		providerList.add(ao2);
		
//		Bridge.open();
//		System.out.println("Main start");
//		Bridge.getInstance().registerMessageHandler(ProviderMessage.PROVIDER_MESSAGE, new ProviderHandler());
	}

	/**
	 * Statische Methode, liefert die einzige Instanz dieser
	 * Klasse zur�ck
	 */
	public static Assistant getInstance() {
		return instance;
	}
	
//	/** list view of email accounts */
//	private List<Account> accountsList = new ArrayList<Account>();
//
//	public List<Account> getAccountsList() {
//		return accountsList;
//	}
//
//	public void setAccountsList(List<Account> accountsList) {
//		this.accountsList = accountsList;
//	}

	/** list of available providers  */
	private List<AccountVO> providerList = new ArrayList<AccountVO>();

	public List<AccountVO> getProviderList() {
		return providerList;
	}

	public void setProviderList(List<AccountVO> providerList) {
		this.providerList = providerList;
	}
	
}