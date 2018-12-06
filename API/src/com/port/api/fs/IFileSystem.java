/*
* Copyright (c) Lukup Media Pvt Limited, India.
* All rights reserved.
*
* This software is the confidential and proprietary information of Lukup Media Pvt Limited ("Confidential Information").
* You shall not disclose such Confidential Information and shall use it only in accordance with the terms
* of the licence agreement you entered into with Lukup Media Pvt Limited.
*
*/
package com.port.api.fs;

import java.util.Date;

/**
 * This interface that needs to be implemented by the class that wants to perform some operation related to file system.
 * @author Jeetendra
 *
 */
public interface IFileSystem {

	/**
	 * @return - returns file object.
	 */
	public Object getFile(String path);
	
	/**
	 * @return - returns file size.
	 */
	public int getFileSize(String filepath);
	
	/**
	 * @return - returns used space.
	 */
	public String getUsedSpace();
	
	/**
	 * @return - returns free space.
	 */
	public String getFreeSpace();
	
	/**
	 * @return - returns last modified date.
	 */
	public Date lastModifiedDate(String filepath);
	
	/**
	 * @return - returns true, if successfully copied.
	 */
	public boolean copy(String sourcepath, String destinationpath);
	
	/**
	 * @return - returns true, if successfully moved.
	 */
	public boolean move(String sourcepath, String destinationpath);
	
	/**
	 * @return - returns true, if successfully directory is created.
	 */
	public boolean createDirectory(String dirpath);
	
	/**
	 * @return - returns true, if successfully directory is deleted.
	 */
	public boolean deleteDirectory(String dirpath);
	
	/**
	 * @return - returns list of directories.
	 */
	public String[] listDirectories();
	
	/**
	 * @return - returns sublist of specified directory.
	 */
	public String[] listDirectory(String dirpath);
	
	/**
	 * @return - returns true, if successfully file is deleted.
	 */
	public boolean deleteFile(String filepath);
	
	/**
	 * @return - returns file object.
	 */
	public Object readFile(String filepath, String offset, int bytestoread);
	
	/**
	 * @return - returns true, if successfully written in file.
	 */
	public boolean writeFile(String filepath, boolean append, String ascii);
	
	/**
	 * @return - returns true, if successfully file is downloaded.
	 */
	public boolean download(String filepath, String landingpath);
	
	/**
	 * @return - returns list of files.
	 */
	public String[] list();
	
	/**
	 * @return - returns true, if successfully suspend operation is performed.
	 */
	public boolean suspend(String landingpath, int index);
	
	/**
	 * @return - returns true, if successfully resume operation is performed.
	 */
	public boolean resume(String landingpath, int index);
	
	/**
	 * @return - returns true, if successfully landing is set.
	 */
	public boolean setLanding(String path, int size);
	
	/**
	 * @return - returns landing space.
	 */
	public int getLandingSpace(String landingpath);
	
	/**
	 * @return - returns true, if successfully landing is removed.
	 */
	public boolean removeLanding(String landingpath);
}
