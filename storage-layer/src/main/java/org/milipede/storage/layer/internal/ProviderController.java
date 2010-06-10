package org.milipede.storage.layer.internal;

import java.util.List;

import org.millipede.router.vo.ProviderVO;

import java.util.ArrayList;

public class ProviderController {

	private static ProviderController instance;

	private List<ProviderVO> storage = new ArrayList<ProviderVO>();
	private List<ProviderVO> mail = new ArrayList<ProviderVO>();
	private List<ProviderVO> hoster = new ArrayList<ProviderVO>();
	
//	private List<Resource> storage;
//	private List<Resource> mail; 
//	private List<Resource> hoster;
//	
//	public List<Resource> getStorage() {
//		return storage;
//	}
//
//	public void setStorage(List<Resource> storage) {
//		this.storage = storage;
//	}
//
//	public List<Resource> getMail() {
//		return mail;
//	}
//
//	public void setMail(List<Resource> mail) {
//		this.mail = mail;
//	}
//
//	public List<Resource> getHoster() {
//		return hoster;
//	}
//
//	public void setHoster(List<Resource> hoster) {
//		this.hoster = hoster;
//	}

	public List<ProviderVO> getStorage() {
		return storage;
	}

	public void setStorage(List<ProviderVO> storage) {
		this.storage = storage;
	}

	public List<ProviderVO> getMail() {
		return mail;
	}

	public void setMail(List<ProviderVO> mail) {
		this.mail = mail;
	}

	public List<ProviderVO> getHoster() {
		return hoster;
	}

	public void setHoster(List<ProviderVO> hoster) {
		this.hoster = hoster;
	}

	private ProviderController() {
//		super();
	}

	public static ProviderController getInstance() {
		if(instance != null)
			return instance;
		else 
			instance = new ProviderController();
		
		return instance;
	}

	
}
