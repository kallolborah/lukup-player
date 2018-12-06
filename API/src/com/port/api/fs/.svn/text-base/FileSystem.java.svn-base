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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.util.Log;


/**
 * This class provides the functionality to set and get the something.
 * @author Jeetendra
 *
 */
public class FileSystem implements IFileSystem {

	private static final String TAG = "FileSystem";
	ArrayList<String> foldersArray = null;
	ArrayList<String> filesArray = null;
	String path = null;
	HashMap<String, ArrayList<String>> pathwithFileMap = null;

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#getFile(java.lang.String)
	 */
	@Override
	public Object getFile(String path) {
		return getFilesFromDir(path);
	}

	private Object getFilesFromDir(String dirPath){
		pathwithFileMap = new HashMap<String, ArrayList<String>>();
		try{
			if(Constant.DEBUG)  Log.d(TAG,"FileLocation: " + dirPath);
			
			foldersArray = new ArrayList<String>();
			filesArray = new ArrayList<String>();
			File f = new File(dirPath);
			File[] files = f.listFiles();
			for(int i=0; i < files.length; i++)
			{
				File file = files[i];
				if(!file.isHidden() && file.canRead()){
					if(Constant.DEBUG) System.out.println("All File name "+file.getName());
					if(file.isDirectory()){
						if(Constant.DEBUG) System.out.println("Folder name "+file.getName());
						foldersArray.add(file.getName());
					}else{
						if(Constant.DEBUG) System.out.println("File name "+file.getName());
						filesArray.add(file.getName());
					}
				}	
			}
			pathwithFileMap.put("folder", foldersArray);
			pathwithFileMap.put("files", filesArray);
			return pathwithFileMap;
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_STORAGE, errors.toString(), e.getMessage());
		}
		return pathwithFileMap;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#getFileSize(java.lang.String)
	 */
	@Override
	public int getFileSize(String filepath) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#getUsedSpace()
	 */
	@Override
	public String getUsedSpace() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#getFreeSpace()
	 */
	@Override
	public String getFreeSpace() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#lastModifiedDate(java.lang.String)
	 */
	@Override
	public Date lastModifiedDate(String filepath) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#copy(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean copy(String sourcepath, String destinationpath) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#move(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean move(String sourcepath, String destinationpath) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#createDirectory(java.lang.String)
	 */
	@Override
	public boolean createDirectory(String dirpath) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#deleteDirectory(java.lang.String)
	 */
	@Override
	public boolean deleteDirectory(String dirpath) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#listDirectories()
	 */
	@Override
	public String[] listDirectories() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#listDirectory(java.lang.String)
	 */
	@Override
	public String[] listDirectory(String dirpath) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#deleteFile(java.lang.String)
	 */
	@Override
	public boolean deleteFile(String filepath) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#readFile(java.lang.String, java.lang.String, int)
	 */
	@Override
	public Object readFile(String filepath, String offset, int bytestoread) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#writeFile(java.lang.String, boolean, java.lang.String)
	 */
	@Override
	public boolean writeFile(String filepath, boolean append, String ascii) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#download(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean download(String filepath, String landingpath) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#list()
	 */
	@Override
	public String[] list() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#suspend(java.lang.String, int)
	 */
	@Override
	public boolean suspend(String landingpath, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#resume(java.lang.String, int)
	 */
	@Override
	public boolean resume(String landingpath, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#setLanding(java.lang.String, int)
	 */

	@Override
	public boolean setLanding(String path, int size) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#getLandingSpace(java.lang.String)
	 */

	@Override
	public int getLandingSpace(String landingpath) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.lukup.fs.IFileSystem#removeLanding(java.lang.String)
	 */

	@Override
	public boolean removeLanding(String landingpath) {
		// TODO Auto-generated method stub
		return false;
	}

}
