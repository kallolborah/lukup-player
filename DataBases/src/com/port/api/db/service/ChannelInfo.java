package com.port.api.db.service;

public class ChannelInfo {
	private int serviceId;
	private String networkId;
	private int tsId;
	private String type;
	private String channelName;
	private int bouquetId;
	private int userId;
	private String Desc;
	private String maturity;
	private float price;
	private String priceModel;
	private String expiryDate;
	private int caScrambled;
	private int runningStatus;
	private String serviceCategory;
	private String channelLogo;
	private long timeStamp;
	private String Date;
	//DVB middleware
	private int eitSchedule;
	private int eitPresent;
	private int networkid;
	private String channelurl;
	private int lcn;
	private int dvbType;
	
	public ChannelInfo(){
		
	}

	public ChannelInfo(int serviceId,int tsId, String networkId,String type, String channelName,int bouquetId, int userId,
			String Desc, String maturity, float price, String priceModel, String expiryDate, int caScrambled, int runningStatus,
			String serviceCategory,String logo,String channelurl){
		this.serviceId = serviceId;
		this.tsId = tsId;
		this.networkId = networkId;
		this.type = type;
		this.channelName = channelName;
		this.bouquetId = bouquetId;
		this.userId = userId;
		this.Desc = Desc;
		this.maturity = maturity;
		this.price = price;
		this.priceModel = priceModel;
		this.expiryDate = expiryDate;
		this.caScrambled = caScrambled;
		this.runningStatus = runningStatus;
		this.serviceCategory = serviceCategory;
		this.channelLogo = logo;
		this.channelurl= channelurl;
	}
	
	//DVB middleware
	public ChannelInfo(int serviceId,int lcn,int dvbtype,int tsId, int networkId,String type, String channelName,int bouquetId, int userId,
			String Desc, String maturity, float price, String priceModel, String expiryDate, int caScrambled, int runningStatus,
			String serviceCategory,String logo,int eschedule,int epresent){
		this.serviceId = serviceId;
		this.lcn = lcn;
		this.dvbType = dvbtype;
		this.tsId = tsId;
		this.networkid = networkId;
		this.type = type;
		this.channelName = channelName;
		this.bouquetId = bouquetId;
		this.userId = userId;
		this.Desc = Desc;
		this.maturity = maturity;
		this.price = price;
		this.priceModel = priceModel;
		this.expiryDate = expiryDate;
		this.caScrambled = caScrambled;
		this.runningStatus = runningStatus;
		this.serviceCategory = serviceCategory;
		this.channelLogo = logo;
		this.eitSchedule = eschedule;
		this.eitPresent = epresent;
	}
	
	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * @return the channelLogo
	 */
	public String getChannelLogo() {
		return channelLogo;
	}

	/**
	 * @param channelLogo the channelLogo to set
	 */
	public void setChannelLogo(String channelLogo) {
		this.channelLogo = channelLogo;
	}

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}
	/**
	 * @return the serviceId
	 */
	public int getServiceId() {
		return serviceId;
	}
	/**
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	/**
	 * @return the networkId
	 */
	public String getNetworkId() {
		return networkId;
	}
	/**
	 * @param networkId the networkId to set
	 */
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	/**
	 * @return the tsId
	 */
	public int getTsId() {
		return tsId;
	}
	/**
	 * @param tsId the tsId to set
	 */
	public void setTsId(int tsId) {
		this.tsId = tsId;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}
	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
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
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return Desc;
	}
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		Desc = desc;
	}
	/**
	 * @return the maturity
	 */
	public String getMaturity() {
		return maturity;
	}
	/**
	 * @param maturity the maturity to set
	 */
	public void setMaturity(String maturity) {
		this.maturity = maturity;
	}
	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(float price) {
		this.price = price;
	}
	/**
	 * @return the priceModel
	 */
	public String getPriceModel() {
		return priceModel;
	}
	/**
	 * @param priceModel the priceModel to set
	 */
	public void setPriceModel(String priceModel) {
		this.priceModel = priceModel;
	}
	/**
	 * @return the expiryDate
	 */
	public String getExpiryDate() {
		return expiryDate;
	}
	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	/**
	 * @return the caScrambled
	 */
	public int getCaScrambled() {
		return caScrambled;
	}
	/**
	 * @param caScrambled the caScrambled to set
	 */
	public void setCaScrambled(int caScrambled) {
		this.caScrambled = caScrambled;
	}
	/**
	 * @return the runningStatus
	 */
	public int getRunningStatus() {
		return runningStatus;
	}
	/**
	 * @param runningStatus the runningStatus to set
	 */
	public void setRunningStatus(int runningStatus) {
		this.runningStatus = runningStatus;
	}
	
	public int getEitSchedule() {
		return eitSchedule;
	}

	public void setEitSchedule(int eitSchedule) {
		this.eitSchedule = eitSchedule;
	}

	public int getEitPresent() {
		return eitPresent;
	}

	public void setEitPresent(int eitPresent) {
		this.eitPresent = eitPresent;
	}

	public int getNetworkid() {
		return networkid;
	}

	public void setNetworkid(int networkid) {
		this.networkid = networkid;
	}
	
	public int getLCN() {
		return lcn;
	}

	public void setLCN(int lcn) {
		this.lcn = lcn;
	}
	
	public int getDvbType() {
		return dvbType;
	}

	public void setDvbType(int dvbType) {
		this.dvbType = dvbType;
	}

	public String getChannelurl() {
		return channelurl;
	}

	public void setChannelurl(String channelurl) {
		this.channelurl = channelurl;
	}
}
