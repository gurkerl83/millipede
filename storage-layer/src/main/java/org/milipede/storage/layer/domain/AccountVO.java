package org.milipede.storage.layer.domain;

import java.util.ArrayList;
import java.util.List;

public class AccountVO {

	private int providerId;
	private String providerName;
//	public List<String> visualProperties;
	private boolean enabled;
	
	public AccountVO() {
		this.providerId = -1;
		this.providerName = "";
//		this.visualProperties = new ArrayList<String>();
		this.enabled = false;
	}
	
	public int getProviderId() {
		return providerId;
	}
	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}
	
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	
//	public List<List<String>> getVisualProperties() {
//		return visualProperties;
//	}
//
//	public void setVisualProperties(List<List<String>> visualProperties) {
//		this.visualProperties = visualProperties;
//	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
        


}
