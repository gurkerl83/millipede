package org.milipede.storage.layer;

public interface CredentialsService {

	public void setSession(String key, String value);
	public String getSession(String key);
	
}
