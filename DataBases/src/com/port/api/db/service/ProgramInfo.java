package com.port.api.db.service;

public class ProgramInfo {
	private int eventId;
	private String eventSrc;
	private String channelType;
	private String genre;
	private float price;
	private String priceModel;
	private String expiryDate;
	private String dateAdded;
	private String Description;
	private String maturity;
	private String image;
	private int ranking; 
	private String actors;
	private String director;
	private String musicDirector;
	private String productionHouse;
	private String startTime;
	private String duration;
	private String rating;
	private String language;
	private String eventName;
	private String eventCategory;
	private int uniqueId;
	private long timeStamp;
	private int channelServiceId;
	private String Date;
	private String summary;
	private int bouquetId;
	private String channelName;
	private int collectionId;
	private String collectionName;
	
	//DVB middleware
	private int runningStatus;
	private int freeCAmode;
	private String startDate;

	public ProgramInfo(){
		
	}

	public ProgramInfo(int eventId, String eventSrc,String channeltype,int chlUniqueId,String genre,float price,String priceModel,String expiryDate,
			String dateAdded,String Description, String maturity, String image, int ranking, String actors,String director,String musicDirector,
				String productionHouse, String startTime, String duration, String rating, String language, String name,String category,int unique_id,
				String summary,int bouquet_id,String channelName,int collectionid,String collectionName){
			this.eventId = eventId;
			this.eventSrc = eventSrc;
			this.channelType = channeltype;
			this.channelServiceId = chlUniqueId;
			this.genre = genre;
			this.price = price;
			this.priceModel = priceModel;
			this.expiryDate = expiryDate;
			this.dateAdded = dateAdded;
			this.Description = Description;
			this.maturity = maturity;
			this.image = image;
			this.ranking = ranking;
			this.actors = actors;
			this.director = director;
			this.musicDirector = musicDirector;
			this.productionHouse = productionHouse;
			this.startTime = startTime;
			this.duration = duration;
			this.rating = rating;
			this.language = language;
			this.eventName = name;
			this.eventCategory = category;
			this.uniqueId = unique_id;
			this.summary = summary;
			this.bouquetId = bouquet_id;
			this.channelName = channelName;
			this.collectionId = collectionid;
			this.collectionName = collectionName;
		}
	
	//DVB middleware
	public ProgramInfo(int eventId, String eventSrc,String channeltype,int chlUniqueId,String genre,float price,String priceModel,String expiryDate,
			String dateAdded,String Description, String maturity, String image, int ranking, String actors,String director,String musicDirector,
				String productionHouse, String startTime, String duration, String rating, String language, String name,String category,int unique_id,
				String summary,int bouquet_id,String channelName,int collectionid,String collectionName,int runningStatus,int freeCAmode,String startDate){
			this.eventId = eventId;
			this.eventSrc = eventSrc;
			this.channelType = channeltype;
			this.channelServiceId = chlUniqueId;
			this.genre = genre;
			this.price = price;
			this.priceModel = priceModel;
			this.expiryDate = expiryDate;
			this.dateAdded = dateAdded;
			this.Description = Description;
			this.maturity = maturity;
			this.image = image;
			this.ranking = ranking;
			this.actors = actors;
			this.director = director;
			this.musicDirector = musicDirector;
			this.productionHouse = productionHouse;
			this.startTime = startTime;
			this.duration = duration;
			this.rating = rating;
			this.language = language;
			this.eventName = name;
			this.eventCategory = category;
			this.uniqueId = unique_id;
			this.summary = summary;
			this.bouquetId = bouquet_id;
			this.channelName = channelName;
			this.collectionId = collectionid;
			this.collectionName = collectionName;
			this.runningStatus = runningStatus;
			this.freeCAmode = freeCAmode;
			this.startDate = startDate;
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
	 * @return the uniqueId
	 */
	public int getProgramId() {
		return uniqueId;
	}

	/**
	 * @param uniqueId the uniqueId to set
	 */
	public void setProgramId(int uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}
	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
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
	/**
	 * @return the eventlSrc
	 */
	public String getEventSrc() {
		return eventSrc;
	}
	/**
	 * @param eventlSrc the eventlSrc to set
	 */
	public void setEventSrc(String eventSrc) {
		this.eventSrc = eventSrc;
	}
	/**
	 * @return the channelId
	 */
	public String getChannelType() {
		return channelType;
	}
	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelType(String channeltype) {
		this.channelType = channeltype;
	}
	
	/**
	 * @return the channelServiceId
	 */
	public int getChannelServiceId() {
		return channelServiceId;
	}

	/**
	 * @param channelServiceId the channelServiceId to set
	 */
	public void setChannelServiceId(int channelServiceId) {
		this.channelServiceId = channelServiceId;
	}
	
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
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
	 * @return the dateAdded
	 */
	public String getDateAdded() {
		return dateAdded;
	}
	/**
	 * @param dateAdded the dateAdded to set
	 */
	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
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
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * @return the ranking
	 */
	public int getRanking() {
		return ranking;
	}
	/**
	 * @param ranking the ranking to set
	 */
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	/**
	 * @return the actors
	 */
	public String getActors() {
		return actors;
	}
	/**
	 * @param actors the actors to set
	 */
	public void setActors(String actors) {
		this.actors = actors;
	}
	/**
	 * @return the director
	 */
	public String getDirector() {
		return director;
	}
	/**
	 * @param director the director to set
	 */
	public void setDirector(String director) {
		this.director = director;
	}
	/**
	 * @return the musicDirector
	 */
	public String getMusicDirector() {
		return musicDirector;
	}
	/**
	 * @param musicDirector the musicDirector to set
	 */
	public void setMusicDirector(String musicDirector) {
		this.musicDirector = musicDirector;
	}
	/**
	 * @return the productionHouse
	 */
	public String getProductionHouse() {
		return productionHouse;
	}
	/**
	 * @param productionHouse the productionHouse to set
	 */
	public void setProductionHouse(String productionHouse) {
		this.productionHouse = productionHouse;
	}
	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}
	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}
	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public int getBouquetId() {
		return bouquetId;
	}

	public void setBouquetId(int bouquetId) {
		this.bouquetId = bouquetId;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	
	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public int getRunningStatus() {
		return runningStatus;
	}

	public void setRunningStatus(int runningStatus) {
		this.runningStatus = runningStatus;
	}

	public int getFreeCAmode() {
		return freeCAmode;
	}

	public void setFreeCAmode(int freeCAmode) {
		this.freeCAmode = freeCAmode;
	}
	
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
}
