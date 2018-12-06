package com.port.api.network.bonjour;

	public class ConnectedDevice {
	private String mName = null;
	private String mIp =  null;
	private int mPort = -1;
	private String mDeviceInfo = null;
	private String mDeviceState = null;
	private String mDeviceType = null;
	public String getmDeviceType() {
		return mDeviceType;
	}
	public void setmDeviceType(String mDeviceType) {
		this.mDeviceType = mDeviceType;
	}
	public String getmName() {
		return mName;
	}
	public void setmName(String mName) {
		this.mName = mName;
	}
	public String getmIp() {
		return mIp;
	}
	public void setmIp(String mIp) {
		this.mIp = mIp;
	}
	public int getmPort() {
		return mPort;
	}
	public void setmPort(int mPort) {
		this.mPort = mPort;
	}
	public String getmDeviceInfo() {
		return mDeviceInfo;
	}
	public void setmDeviceInfo(String mDeviceInfo) {
		this.mDeviceInfo = mDeviceInfo;
	}
	public String getmDeviceState() {
		return mDeviceState;
	}
	public void setmDeviceState(String mDeviceState) {
		this.mDeviceState = mDeviceState;
	}
	
}
