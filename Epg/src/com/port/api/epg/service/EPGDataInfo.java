package com.port.api.epg.service;

public class EPGDataInfo {

	private String program1;
	private String time1;
	private String image1;
	private String serviceId;
	private String channelName;
	private String channelType;
	
	public EPGDataInfo(String program1,String time1,String image1,String channelId){
		this.program1 = program1;
		this.time1 = time1;
		this.image1 = image1;
		this.serviceId = channelId;
	}
	
	public EPGDataInfo(String program1,String time1,String image1,String channelId,String channelName,String channelType){
		this.program1 = program1;
		this.time1 = time1;
		this.image1 = image1;
		this.serviceId = channelId;
		this.channelName = channelName;
		this.channelType = channelType;
	}
	
	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	/**
	 * @return the program1
	 */
	public String getProgram1() {
		return program1;
	}

	/**
	 * @param program1 the program1 to set
	 */
	public void setProgram1(String program1) {
		this.program1 = program1;
	}

	/**
	 * @return the time1
	 */
	public String getTime1() {
		return time1;
	}

	/**
	 * @param time1 the time1 to set
	 */
	public void setTime1(String time1) {
		this.time1 = time1;
	}

	/**
	 * @return the image1
	 */
	public String getImage1() {
		return image1;
	}

	/**
	 * @param image1 the image1 to set
	 */
	public void setImage1(String image1) {
		this.image1 = image1;
	}

	/**
	 * @return the channelId
	 */
	public String setServiceId() {
		return serviceId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setServiceId(String channelId) {
		this.serviceId = channelId;
	}
	
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
}
