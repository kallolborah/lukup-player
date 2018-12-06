package com.port.apps.search;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.port.Channel;
import com.port.Consumer;
import com.port.MessageQueue;
import com.port.Port;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.service.StatusInfo;
import com.port.api.db.util.CacheData;
import com.port.api.db.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.search.webservices.SearchData;

//consumer : player, network : bluetooth
public class Search extends IntentService {

	private String TAG = "Search";
	String method = "com.port.apps.search.Search.";

	// Receiver
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	Channel returner;
	String dockID;

	public Search() {
		super("Search");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String category = "";
		String keywords = "";
		String id = "";
		String type = "";
		String channeltype = "";
		String starttime = "";
		String name = "";
		Port.c = getApplicationContext();
		if (extras != null) {

			if (extras.containsKey("ProducerNetwork")) {
				pnetwork = extras.getString("ProducerNetwork"); // to be used to
																// return back
																// response
			}
			if (extras.containsKey("ConsumerNetwork")) {
				cnetwork = extras.getString("ConsumerNetwork"); // to be used to
																// send request
																// onward
			}
			if (extras.containsKey("Producer")) {
				producer = extras.getString("Producer");
			}
			if (extras.containsKey("Caller")) {
				caller = extras.getString("Caller");
			}
			if (extras.containsKey("macid")) {
				dockID = extras.getString("macid");
			}

			if (returner == null) { // to ensure that there is only one returner
									// instance for one activity
				returner = new Channel("Dock", dockID); // only to be used to
														// send back responses
														// from Dock to
														// Requestor, eg, Player
				returner.set(producer, pnetwork, caller); // setting consumer =
															// producer, network
			}

			if (extras.containsKey("Params")) {
				try {
					functionData = extras.getString("Params");
					JSONObject jsonObj = new JSONObject(functionData);
					if (Constant.DEBUG)
						Log.d(TAG, "jsonObj : " + jsonObj);
					if (jsonObj.has("keyword")) {
						keywords = jsonObj.getString("keyword");
					}
					if (jsonObj.has("category")) {
						category = jsonObj.getString("category");
					}
					if (jsonObj.has("id")) {
						id = jsonObj.getString("id");
					}
					if (jsonObj.has("starttime")) {
						starttime = jsonObj.getString("starttime");
					}
					if (jsonObj.has("type")) {
						type = jsonObj.getString("type");
					}
					if (jsonObj.has("name")) {
						name = jsonObj.getString("name");
					}
					if (jsonObj.has("channeltype")) {
						channeltype = jsonObj.getString("channeltype");
					}
				} catch (JSONException e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,
							SystemLog.LOG_APPLICATION, errors.toString(),
							e.getMessage());
				}
			}
			if (extras.containsKey("Method")) {
				try {
					func = extras.getString("Method");
					if (func.equalsIgnoreCase("getSearchData")) {
						getSearchData(category, keywords);
					} else if (func.equalsIgnoreCase("getEventStatus")) {
						getEventStatus(id, type, channeltype);
					} else if (func.equalsIgnoreCase("getGenre")) {
						getGenre();
					} else if (func.equalsIgnoreCase("getLiveEventId")) {
						getLiveEventId(id, name, starttime);
					}
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,
							SystemLog.LOG_APPLICATION, errors.toString(),
							e.getMessage());
				}
			}

		}
	}

	// for getting Search list
	private void getSearchData(String category, String keywords)
			throws JSONException, InterruptedException {
		JSONArray jsonArray = new JSONArray();
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			jsonArray = getSearchPopularAndLatest(category, keywords);
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog
					.createErrorLogXml(SystemLog.TYPE_DOCK,
							SystemLog.LOG_WEBSERVICE, errors.toString(),
							e.getMessage());
		}
		data.put("result", "success");
		data.put("type", "search");
		data.put("category", category);
		if (jsonArray != null && jsonArray.length() > 0) {
			data.put("eventdata", jsonArray);
		} else {
			if (Constant.DEBUG)
				Log.d(TAG, "map null ");
		}
		resp.put("params", data);
		returner.add(method + "getSearchData", resp, "messageActivity");
		returner.send();
	}

	private void getEventStatus(String id, String type, String chltype)
			throws JSONException { // id can be from PPV or PPC channel, type is
									// pricing model
		if (Constant.DEBUG)
			Log.d("InfoAction", "getEventStatus() id " + id);
		int Id = Integer.parseInt(id);
		String eventOrService = "";
		JSONObject jsonObject = new JSONObject();
		JSONObject resp = new JSONObject();

		ProgramGateway programGateway = new ProgramGateway(
				getApplicationContext());
		StatusGateway statusGateway = new StatusGateway(getApplicationContext());
		try {
			int userId=CacheData.getUserId();
			if(userId==0){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					userId = Integer.valueOf(info.getProfile());
					CacheData.setUserId(userId);
				}
			}

			if (chltype.equalsIgnoreCase("live")) {
				ProgramInfo Info = programGateway
						.getProgramInfoByEventId(Integer.parseInt(id));
				if (Info != null) {
					boolean lock = false;
					StatusInfo lockInfo = statusGateway
							.getStatusInfoByServiceId(userId + "",
									Info.getChannelServiceId(), 2, "service");
					if (lockInfo == null) {
						lock = false;
					} else {
						if (lockInfo.getStatus() == 2) {
							lock = true;
						} else {
							lock = false;
						}
					}
					jsonObject.put("lock", lock);

					boolean like = false;
					StatusInfo likeInfo = statusGateway
							.getStatusInfoByServiceId(userId + "",
									Integer.parseInt(id), 1, "event");
					if (likeInfo == null) {
						like = false;
					} else {
						like = true;
					}
					jsonObject.put("like", like);

					if (Constant.DEBUG)
						Log.d(TAG,
								"eventId is Present in DB: "
										+ Info.getProgramId());
					jsonObject.put("id", Info.getEventId());
					jsonObject.put("servicetype", Info.getChannelType());
					jsonObject.put("serviceid", Info.getChannelServiceId());
					jsonObject.put("url", Info.getEventSrc());
					jsonObject.put("name", Info.getEventName());
					jsonObject.put("releasedate", Info.getDateAdded());
					jsonObject.put("actors", Info.getActors());
					jsonObject.put("rating", Info.getRating());
					jsonObject.put("genre", Info.getGenre());
					jsonObject.put("pricingmodel", Info.getPriceModel());
					jsonObject.put("description", Info.getDescription());
					jsonObject.put("director", Info.getDirector());
					jsonObject.put("production", Info.getProductionHouse());
					jsonObject.put("musicdirector", Info.getMusicDirector());
					jsonObject.put("price", Info.getPrice());
					jsonObject.put("result", "success");
					resp.put("params", jsonObject);
					returner.add(method + "getEventStatus", resp,
							"messageActivity");
					returner.send();
				} else {
					jsonObject.put("result", "failure");
					resp.put("params", jsonObject);
					returner.add(method + "getEventStatus", resp,
							"messageActivity");
					returner.send();
				}

			} else {
				ProgramInfo Info = programGateway.getProgramInfoByUniqueId(id);
				if (Info != null) {
					if (type.equalsIgnoreCase("PPC")) {
						// find channelid of event id passed to this function
						// assign to Id
						Id = Info.getChannelServiceId();
						eventOrService = "service";
					} else {
						Id = Info.getProgramId();
						eventOrService = "event";
					}

					boolean lock = false;
					StatusInfo lockInfo = statusGateway
							.getStatusInfoByServiceId(userId + "",
									Info.getChannelServiceId(), 2, "service");
					if (lockInfo == null) {
						lock = false;
					} else {
						if (lockInfo.getStatus() == 2) {
							lock = true;
						} else {
							lock = false;
						}
					}
					jsonObject.put("lock", lock);

					boolean subscribe = false;
					StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(
							Id, 9, eventOrService);
					if (info != null) {
						if (info.getStatus() == 9) {
							subscribe = true;
						} else {
							subscribe = false;
						}
					} else {
						subscribe = false;
					}
					jsonObject.put("subscribe", subscribe);

					boolean like = false;
					StatusInfo likeInfo = statusGateway
							.getStatusInfoByServiceId(userId + "",
									Integer.parseInt(id), 1, "event");
					if (likeInfo == null) {
						like = false;
					} else {
						like = true;
					}
					jsonObject.put("like", like);

					if (Info != null) {
						if (Constant.DEBUG)
							Log.d(TAG,
									"eventId is Present in DB: "
											+ Info.getProgramId());
						jsonObject.put("id", Info.getEventId());
						jsonObject.put("servicetype", Info.getChannelType());
						jsonObject.put("serviceid", Info.getChannelServiceId());
						// jsonObject.put("channelPrice", chlInfo.getPrice());
						jsonObject.put("url", Info.getEventSrc());
						jsonObject.put("name", Info.getEventName());
						jsonObject.put("releasedate", Info.getDateAdded());
						jsonObject.put("actors", Info.getActors());
						jsonObject.put("rating", Info.getRating());
						jsonObject.put("genre", Info.getGenre());
						jsonObject.put("pricingmodel", Info.getPriceModel());
						jsonObject.put("description", Info.getDescription());
						jsonObject.put("director", Info.getDirector());
						jsonObject.put("production", Info.getProductionHouse());
						jsonObject
								.put("musicdirector", Info.getMusicDirector());
						jsonObject.put("price", Info.getPrice());
						jsonObject.put("result", "success");
						resp.put("params", jsonObject);
						returner.add(method + "getEventStatus", resp,
								"messageActivity");
						returner.send();
					}
					// }
				} else {
					jsonObject.put("result", "failure");
					resp.put("params", jsonObject);
					returner.add(method + "getEventStatus", resp,
							"messageActivity");
					returner.send();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,
					SystemLog.LOG_APPLICATION, errors.toString(),
					e.getMessage());
		}
	}

	private JSONArray getSearchPopularAndLatest(String category, String keyword)
			throws JSONException {
		if (Constant.DEBUG)
			Log.d(TAG, "category: " + category + ", keyword: " + keyword);
		JSONArray jsonArray = new JSONArray();
		try {
			JSONObject jsonObject = SearchData.getJsonSearchData(category,
					keyword);

			if (jsonObject != null) {
				JSONObject jsonData = jsonObject.getJSONObject("data");
				if (jsonData != null) {
					String result = jsonData.getString("result");
					if (result != null
							&& !(result.trim().equalsIgnoreCase("failure"))) {
						jsonArray = jsonData.getJSONArray("eventdata");
					} else {
						String message = this.getResources().getString(
								R.string.SEARCH_RESULT_FAILURE);
						if (jsonData.has("msg")) {
							message = jsonData.getString("msg");
						}
						if (Constant.DEBUG)
							Log.d(TAG, "result came failure : " + message);
					}
				}
			}
			return jsonArray;
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,
					SystemLog.LOG_APPLICATION, errors.toString(),
					e.getMessage());
		}
		return jsonArray;
	}

	private void getGenre() throws JSONException, InterruptedException {
		ArrayList<String> genre = new ArrayList<String>();
		List<String> genreList = new ArrayList<String>();
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		ChannelGateway channelGateway = new ChannelGateway(
				getApplicationContext());

		List<ChannelInfo> channelIdList = channelGateway.getAllChannelInfo();
		if (Constant.DEBUG)
			Log.d(TAG, "From DB :channelIdList: " + channelIdList.size());
		if (channelIdList != null && channelIdList.size() > 0) {
			for (ChannelInfo info : channelIdList) {
				if (info.getServiceCategory() != null
						&& !info.getServiceCategory().equalsIgnoreCase("")) {
					genre.add(info.getServiceCategory());
				}
			}
		}
		// }

		if (genre.size() > 0) {
			genreList = removeDuplicate(genre);
			for (int i = 0; i < genreList.size(); i++) {
				jsonArray.put(genreList.get(i));
			}
			data.put("gerneList", jsonArray);
			data.put("result", "success");
			resp.put("params", data);
			returner.add(method + "getGenre", resp, "messageActivity");
			returner.send();
		} else {
			data.put("result", "failure");
			resp.put("params", data);
			returner.add(method + "getGenre", resp, "messageActivity");
			returner.send();
		}
	}

	private void getLiveEventId(String id, String eventName, String starttime)
			throws JSONException {
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		// ProgramGateway programGateway = null;
		// 2014-11-07 22:00:00.000
		String time = com.port.api.util.CommonUtil.getDates() + " " + starttime
				+ ".000";

		ProgramGateway programGateway = new ProgramGateway(
				Port.c.getApplicationContext());

		try {
			ProgramInfo liveEventInfo = programGateway.getLiveEventInfo(
					Integer.parseInt(id), eventName, time);
			if (liveEventInfo != null) {
				data.put("id", liveEventInfo.getEventId() + "");
				data.put("serviceid", liveEventInfo.getChannelServiceId());
				data.put("result", "success");
				resp.put("params", data);
				returner.add(method + "getLiveEventId", resp, "messageActivity");
				returner.send();
			} else {
				data.put("result", "failure");
				resp.put("params", data);
				returner.add(method + "getLiveEventId", resp, "messageActivity");
				returner.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,
					SystemLog.LOG_APPLICATION, errors.toString(),
					e.getMessage());
		}
	}

	public List<String> removeDuplicate(List<String> arrayList) {
		List<String> filter = new ArrayList<String>();
		List<String> Original = new ArrayList<String>();
		Original.addAll(arrayList);
		HashSet hashSet = new HashSet(Original);
		Original.clear();
		Original.addAll(hashSet);
		filter.addAll(hashSet);
		if (Constant.DEBUG)
			Log.d(TAG, "removed vales are " + hashSet);
		Collections.sort(filter);
		return filter;

	}

}
