package com.port.api.db.service;

public class StatusInfo {
	private int userId;
	private int serviceId;
	private int eventId;
	private int uniqueId;
	private int status;
	private int statusFreq;
	private long timeStamp;
	private String Date;
	private int lastViewed;
	
	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
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
	
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * @return the channelId
	 */
	public int getServiceId() {
		return serviceId;
	}
	/**
	 * @param channelId the channelId to set
	 */
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	/**
	 * @return the eventId
	 */
	public int getEventId() {
		return eventId;
	}
	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	
	public int getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the statusFreq
	 */
	public int getStatusFreq() {
		return statusFreq;
	}
	/**
	 * @param statusFreq the statusFreq to set
	 */
	public void setStatusFreq(int statusFreq) {
		this.statusFreq = statusFreq;
	}
	/**
	 * @return the lastViewed
	 */
	public int getLastViewed() {
		return lastViewed;
	}
	/**
	 * @param lastViewed the lastViewed to set
	 */
	public void setLastViewed(int lastViewed) {
		this.lastViewed = lastViewed;
	}
	
}
