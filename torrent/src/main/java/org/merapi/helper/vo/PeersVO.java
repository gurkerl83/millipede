package org.merapi.helper.vo;

public class PeersVO {

	//updateDetailData - peers --> only dynamic
	private String peersIP = null;
	private String peersProgramm = null;
	private String peersFlags = null;
	private int peersProzent = 0;
	private int peersDLRate = 0;
	private int peersULRate = 0;
	private int peersAnfragen = 0;
	private int peersUpgeloaded = 0;
	private int peersGeladen = 0;
	private int peersPeerDL = 0;
	
	public PeersVO(String peersIP, String peersProgramm, String peersFlags,
			int peersProzent, int peersDLRate, int peersULRate, int peersAnfragen,
			int peersUpgeloaded, int peersGeladen, int peersPeerDL) {
		
		this.peersIP = peersIP;
		this.peersProgramm = peersProgramm;
		this.peersFlags = peersFlags;
		this.peersProzent = peersProzent;
		this.peersDLRate = peersDLRate;
		this.peersULRate = peersULRate;
		this.peersAnfragen = peersAnfragen;
		this.peersUpgeloaded = peersUpgeloaded;
		this.peersGeladen = peersGeladen;
		this.peersPeerDL = peersPeerDL;
	}
	
	public String getPeersIP() {
		return peersIP;
	}
	public void setPeersIP(String peersIP) {
		this.peersIP = peersIP;
	}
	public String getPeersProgramm() {
		return peersProgramm;
	}
	public void setPeersProgramm(String peersProgramm) {
		this.peersProgramm = peersProgramm;
	}
	public String getPeersFlags() {
		return peersFlags;
	}
	public void setPeersFlags(String peersFlags) {
		this.peersFlags = peersFlags;
	}
	public int getPeersProzent() {
		return peersProzent;
	}
	public void setPeersProzent(int peersProzent) {
		this.peersProzent = peersProzent;
	}
	public int getPeersDLRate() {
		return peersDLRate;
	}
	public void setPeersDLRate(int peersDLRate) {
		this.peersDLRate = peersDLRate;
	}
	public int getPeersULRate() {
		return peersULRate;
	}
	public void setPeersULRate(int peersULRate) {
		this.peersULRate = peersULRate;
	}
	public int getPeersAnfragen() {
		return peersAnfragen;
	}
	public void setPeersAnfragen(int peersAnfragen) {
		this.peersAnfragen = peersAnfragen;
	}
	public int getPeersUpgeloaded() {
		return peersUpgeloaded;
	}
	public void setPeersUpgeloaded(int peersUpgeloaded) {
		this.peersUpgeloaded = peersUpgeloaded;
	}
	public int getPeersGeladen() {
		return peersGeladen;
	}
	public void setPeersGeladen(int peersGeladen) {
		this.peersGeladen = peersGeladen;
	}
	public int getPeersPeerDL() {
		return peersPeerDL;
	}
	public void setPeersPeerDL(int peersPeerDL) {
		this.peersPeerDL = peersPeerDL;
	}
	
	
}
