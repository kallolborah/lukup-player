package com.port.api.db.service;

public class BouquetInfo {
    
    private int bouquetId;
    private String category;
	private String bouquetName;
	private long timeStamp;
	private int userId;	
	private String Date;
	//DVB middleware
	private int TSId;	
	private int LCN;	
	private int ServiceId;

	public BouquetInfo(){
		
	}
	
	public BouquetInfo(int bouquetid, String bouquetName,String category){
		this.bouquetId = bouquetid;
		this.bouquetName = bouquetName;
		this.category = category;
	}
	
	//DVB middleware
	public BouquetInfo(int bouquetid, String bouquetName,String category,int serviceId,int tsid,int lcn){
		this.bouquetId = bouquetid;
		this.bouquetName = bouquetName;
		this.category = category;
		
		this.ServiceId = serviceId;
		this.TSId = tsid;
		this.LCN = lcn;
	}
	
	public int getTSId() {
		return TSId;
	}

	public void setTSId(int tSId) {
		TSId = tSId;
	}

	public int getLCN() {
		return LCN;
	}

	public void setLCN(int lCN) {
		LCN = lCN;
	}

	public int getServiceId() {
		return ServiceId;
	}

	public void setServiceId(int serviceId) {
		ServiceId = serviceId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}
	
	/**
	 * @return the bouquetId
	 */
	public int getBouquetId() {
		return bouquetId;
	}
	/**
	 * @param bouquetId the bouquetId to set
	 */
	public void setBouquetId(int bouquetId) {
		this.bouquetId = bouquetId;
	}
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the bouquetName
	 */
	public String getBouquetName() {
		return bouquetName;
	}
	/**
	 * @param bouquetName the bouquetName to set
	 */
	public void setBouquetName(String bouquetName) {
		this.bouquetName = bouquetName;
	}
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
}
