package com.port.api.epg.service;

public class PackageInfos {
	String packageId;
	String packageName;
	String packageDescription;
	String packageImage;
	String packagePrice;
	String packageCurrency;
	String packagePrograms; 
	String packageChannels;
	String packagePeriod;
	String packagePeriodUnit;
	String packageThumbnail;
	
	public PackageInfos(String packageId, String packageName, String packageDescription, String packageImage, String packagePrice, String packageCurrency, String packagePrograms, String packageChannels, String packagePeriod, String packagePeriodUnit,String thumbnail) {
		this.packageId = packageId;
		this.packageName = packageName;
		this.packageDescription = packageDescription;
		this.packageImage = packageImage;
		this.packagePrice = packagePrice;
		this.packagePrograms = packagePrograms;
		this.packageChannels = packageChannels;
		this.packageCurrency = packageCurrency;
		this.packagePeriod = packagePeriod;
		this.packagePeriodUnit = packagePeriodUnit;
		this.packageThumbnail = thumbnail;
	}

	
	/**
	 * @return the packageId
	 */
	public String getPackageId() {
		return packageId;
	}
	/**
	 * @param packageId the packageId to set
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}
	/**
	 * @param packageName the packageName to set
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	/**
	 * @return the packageDescription
	 */
	public String getPackageDescription() {
		return packageDescription;
	}
	/**
	 * @param packageDescription the packageDescription to set
	 */
	public void setPackageDescription(String packageDescription) {
		this.packageDescription = packageDescription;
	}
	/**
	 * @return the packageImage
	 */
	public String getPackageImage() {
		return packageImage;
	}
	/**
	 * @param packageImage the packageImage to set
	 */
	public void setPackageImage(String packageImage) {
		this.packageImage = packageImage;
	}
	/**
	 * @return the packagePrice
	 */
	public String getPackagePrice() {
		return packagePrice;
	}
	/**
	 * @param packagePrice the packagePrice to set
	 */
	public void setPackagePrice(String packagePrice) {
		this.packagePrice = packagePrice;
	}
	/**
	 * @return the packageCurrency
	 */
	public String getPackageCurrency() {
		return packageCurrency;
	}
	/**
	 * @param packageCurrency the packageCurrency to set
	 */
	public void setPackageCurrency(String packageCurrency) {
		this.packageCurrency = packageCurrency;
	}
	/**
	 * @return the packagePrograms
	 */
	public String getPackagePrograms() {
		return packagePrograms;
	}
	/**
	 * @param packagePrograms the packagePrograms to set
	 */
	public void setPackagePrograms(String packagePrograms) {
		this.packagePrograms = packagePrograms;
	}
	/**
	 * @return the packageChannels
	 */
	public String getPackageChannels() {
		return packageChannels;
	}
	/**
	 * @param packageChannels the packageChannels to set
	 */
	public void setPackageChannels(String packageChannels) {
		this.packageChannels = packageChannels;
	}
	/**
	 * @return the packagePeriod
	 */
	public String getPackagePeriod() {
		return packagePeriod;
	}
	/**
	 * @param packagePeriod the packagePeriod to set
	 */
	public void setPackagePeriod(String packagePeriod) {
		this.packagePeriod = packagePeriod;
	}
	/**
	 * @return the packagePeriodUnit
	 */
	public String getPackagePeriodUnit() {
		return packagePeriodUnit;
	}
	/**
	 * @param packagePeriodUnit the packagePeriodUnit to set
	 */
	public void setPackagePeriodUnit(String packagePeriodUnit) {
		this.packagePeriodUnit = packagePeriodUnit;
	}
	
	/**
	 * @return the packageThumbnail
	 */
	public String getPackageThumbnail() {
		return packageThumbnail;
	}


	/**
	 * @param packageThumbnail the packageThumbnail to set
	 */
	public void setPackageThumbnail(String packageThumbnail) {
		this.packageThumbnail = packageThumbnail;
	}

}
