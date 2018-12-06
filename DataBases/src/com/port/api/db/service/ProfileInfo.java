package com.port.api.db.service;

public class ProfileInfo {
	
	private int userId;
	private String userName;
	private String imagePwd;
	private String fBId;
	private String fbToken;
	private int fbSync;
	private String fBPhoto;
	private String SubscriberId;
	private int subStatus;
	private int lastViewService;
	private int lastView;
	private String network;
	/**
	 * @return the network
	 */
	public String getNetwork() {
		return network;
	}
	/**
	 * @param network the network to set
	 */
	public void setNetwork(String network) {
		this.network = network;
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
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the imagePwd
	 */
	public String getImagePwd() {
		return imagePwd;
	}
	/**
	 * @param imagePwd the imagePwd to set
	 */
	public void setImagePwd(String imagePwd) {
		this.imagePwd = imagePwd;
	}
	/**
	 * @return the fBId
	 */
	public String getfBId() {
		return fBId;
	}
	/**
	 * @param fBId the fBId to set
	 */
	public void setfBId(String fBId) {
		this.fBId = fBId;
	}
	/**
	 * @return the fbToken
	 */
	public String getFbToken() {
		return fbToken;
	}
	/**
	 * @param fbToken the fbToken to set
	 */
	public void setFbToken(String fbToken) {
		this.fbToken = fbToken;
	}
	/**
	 * @return the fbSync
	 */
	public int getFbSync() {
		return fbSync;
	}
	/**
	 * @param fbSync the fbSync to set
	 */
	public void setFbSync(int fbSync) {
		this.fbSync = fbSync;
	}
	/**
	 * @return the fBPhoto
	 */
	public String getfBPhoto() {
		return fBPhoto;
	}
	/**
	 * @param fBPhoto the fBPhoto to set
	 */
	public void setfBPhoto(String fBPhoto) {
		this.fBPhoto = fBPhoto;
	}
	/**
	 * @return the subscriberId
	 */
	public String getSubscriberId() {
		return SubscriberId;
	}
	/**
	 * @param subscriberId the subscriberId to set
	 */
	public void setSubscriberId(String subscriberId) {
		SubscriberId = subscriberId;
	}
	/**
	 * @return the subStatus
	 */
	public int getSubStatus() {
		return subStatus;
	}
	/**
	 * @param subStatus the subStatus to set
	 */
	public void setSubStatus(int subStatus) {
		this.subStatus = subStatus;
	}
	/**
	 * @return the lastViewService
	 */
	public int getLastViewService() {
		return lastViewService;
	}
	/**
	 * @param lastViewService the lastViewService to set
	 */
	public void setLastViewService(int lastViewService) {
		this.lastViewService = lastViewService;
	}
	/**
	 * @return the lastView
	 */
	public int getLastView() {
		return lastView;
	}
	/**
	 * @param lastView the lastView to set
	 */
	public void setLastView(int lastView) {
		this.lastView = lastView;
	}
}
