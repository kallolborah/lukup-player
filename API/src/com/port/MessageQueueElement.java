package com.port;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;


public class MessageQueueElement {

	private String consumer;
	private String consumerNetwork;
	private String method;
	private int priority;
	private String macId;
	private String producer;
//	private String[][] params;
	private String jsonParams;
	private JSONObject json;
	/**
	 * @return the json
	 */
	public JSONObject getJson() {
		return json;
	}
	/**
	 * @param json the json to set
	 */
	public void setJson(JSONObject json) {
		this.json = json;
	}

	private String called;
	private String caller;
	
	/**
	 * @return the jsonParams
	 */
	public String getJsonParams() {
		return jsonParams;
	}
	/**
	 * @param jsonParams the jsonParams to set
	 */
	public void setJsonParams(String jsonParams) {
		this.jsonParams = jsonParams;
	}
	/**
	 * @return the macId
	 */
	public String getMacId() {
		return macId;
	}
	/**
	 * @param macId the macId to set
	 */
	public void setMacId(String macId) {
		this.macId = macId;
	}

	/**
	 * @return the producer
	 */
	public String getProducer() {
		return producer;
	}
	/**
	 * @param producer the producer to set
	 */
	public void setProducer(String producer) {
		this.producer = producer;
	}
	/**
	 * @return the consumer
	 */
	public String getConsumer() {
		return consumer;
	}
	/**
	 * @param consumer the consumer to set
	 */
	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void setNetwork(String cNetwork){
		this.consumerNetwork = cNetwork;
	}

	public String getNetwork(){
		return consumerNetwork;
	}
	
	public void setCaller(String caller){
		this.caller = caller;
	}
	
	public String getCaller(){
		return caller;
	}
	
	public void setCalled(String called){
		this.called = called;
	}

	public String getCalled(){
		return called;
	}
}
