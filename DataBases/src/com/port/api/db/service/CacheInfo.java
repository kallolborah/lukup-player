package com.port.api.db.service;

public class CacheInfo {
	private int Id;
	private String Ssid;
	private String Bssid;
	private String pwd;
	private String Security;
	private String subscriber;
	private String distributor;
	private String profile;
	private String hname;
	private String hpwd;
	
	public CacheInfo(){
		
	}

	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getSsid() {
		return Ssid;
	}
	public void setSsid(String ssid) {
		Ssid = ssid;
	}
	public String getBssid() {
		return Bssid;
	}
	public void setBssid(String bssid) {
		Bssid = bssid;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSecurity() {
		return Security;
	}
	public void setSecurity(String security) {
		Security = security;
	}
	public String getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(String Subscriber) {
		subscriber = Subscriber;
	}
	public String getDistributor() {
		return distributor;
	}
	public void setDistributor(String Distributor) {
		distributor = Distributor;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String Profile) {
		profile = Profile;
	}
	public String getHotspotName() {
		return hname;
	}
	public void setHotspotName(String hotspotname) {
		hname = hotspotname;
	}
	public String getHotspotPwd() {
		return hpwd;
	}
	public void setHotspotPwd(String hotspotpwd) {
		hpwd = hotspotpwd;
	}
}
