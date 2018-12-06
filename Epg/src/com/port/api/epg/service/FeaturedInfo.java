package com.port.api.epg.service;


public class FeaturedInfo {

	
	int featuredId;
	int userId;
	int externalEventId;
	String featuredType;
	String featuredImage;
	String featuredTitle;
	String featuredDesc;
	String featuredSubscribe;
	String price;
	String featuredThumbnail;
	
	public FeaturedInfo(int featuredId,int userId,int externalEventId,String featuredType,String image,String desc,String name,String thumbnail) {
		this.featuredId = featuredId;
		this.userId = userId;
		this.externalEventId = externalEventId;
		this.featuredType = featuredType;
		this.featuredImage = image;
		this.featuredDesc = desc;
		this.featuredTitle = name;
		this.featuredThumbnail = thumbnail;
	}
	
	
//	public FeaturedInfo(int externalEventId,String featuredType,String image,String title,String desc,String subscribe,
//			String price) {
//		this.externalEventId = externalEventId;
//		this.featuredType = featuredType;
//		this.featuredImage = image;
//		this.featuredTitle = title;
//		this.featuredDesc = desc;
//		this.featuredSubscribe = subscribe;
//		this.price = price;
//	}
	
	/**
	 * @return the featuredImage
	 */
	public String getFeaturedImage() {
		return featuredImage;
	}
	/**
	 * @param featuredImage the featuredImage to set
	 */
	public void setFeaturedImage(String featuredImage) {
		this.featuredImage = featuredImage;
	}
	/**
	 * @return the featuredId
	 */
	public int getFeaturedId() {
		return featuredId;
	}
	/**
	 * @param featuredId the featuredId to set
	 */
	public void setFeaturedId(int featuredId) {
		this.featuredId = featuredId;
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
	 * @return the externalEventId
	 */
	public int getExternalEventId() {
		return externalEventId;
	}
	/**
	 * @param externalEventId the externalEventId to set
	 */
	public void setExternalEventId(int externalEventId) {
		this.externalEventId = externalEventId;
	}
	/**
	 * @return the featuredType
	 */
	public String getFeaturedType() {
		return featuredType;
	}
	/**
	 * @param featuredType the featuredType to set
	 */
	public void setFeaturedType(String featuredType) {
		this.featuredType = featuredType;
	}
	
	/**
	 * @return the featuredTitle
	 */
	public String getFeaturedTitle() {
		return featuredTitle;
	}


	/**
	 * @param featuredTitle the featuredTitle to set
	 */
	public void setFeaturedTitle(String featuredTitle) {
		this.featuredTitle = featuredTitle;
	}


	/**
	 * @return the featuredDesc
	 */
	public String getFeaturedDesc() {
		return featuredDesc;
	}


	/**
	 * @param featuredDesc the featuredDesc to set
	 */
	public void setFeaturedDesc(String featuredDesc) {
		this.featuredDesc = featuredDesc;
	}


	/**
	 * @return the featuredSubscribe
	 */
	public String getFeaturedSubscribe() {
		return featuredSubscribe;
	}


	/**
	 * @param featuredSubscribe the featuredSubscribe to set
	 */
	public void setFeaturedSubscribe(String featuredSubscribe) {
		this.featuredSubscribe = featuredSubscribe;
	}
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	/**
	 * @return the featuredThumbnail
	 */
	public String getFeaturedThumbnail() {
		return featuredThumbnail;
	}


	/**
	 * @param featuredThumbnail the featuredThumbnail to set
	 */
	public void setFeaturedThumbnail(String featuredThumbnail) {
		this.featuredThumbnail = featuredThumbnail;
	}
}
