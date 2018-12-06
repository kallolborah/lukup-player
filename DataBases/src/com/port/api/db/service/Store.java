package com.port.api.db.service;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Store extends SQLiteOpenHelper {
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MediaPlayerInfo";
    
    public Store(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
    
    public static final String CACHEDATA_TABLE = "Cache";
    public static final String CACHEDATA_ID = "Id";
    public static final String CACHEDATA_SSID = "Wifi_Ssid";
    public static final String CACHEDATA_BSSID = "Wifi_Bssid";
    public static final String CACHEDATA_WIFIPWD = "Wifi_Password";
    public static final String CACHEDATA_WIFISECURITY = "Wifi_Security";
    public static final String CACHEDATA_DISTRIBUTOR = "Distributor";
    public static final String CACHEDATA_SUBSCRIBER = "Subscriber";
    public static final String CACHEDATA_PROFILE = "Profile";
    public static final String CACHEDATA_HOTSPOTNAME = "HotspotName";
    public static final String CACHEDATA_HOTSPOTPWD = "HotspotPwd";
    
    private static final String CACHEDATA_TABLE_CREATE = "create table "
    		+ CACHEDATA_TABLE + "( " + CACHEDATA_ID
			+ " integer primary key, " + CACHEDATA_SSID + " text, " 
    		+ CACHEDATA_BSSID + " text, "
			+ CACHEDATA_WIFIPWD + " text, " 
			+ CACHEDATA_WIFISECURITY + " text,"
			+ CACHEDATA_DISTRIBUTOR + " text, "
			+ CACHEDATA_SUBSCRIBER + " text, "
			+ CACHEDATA_PROFILE + " text, "
			+ CACHEDATA_HOTSPOTNAME + " text, "
			+ CACHEDATA_HOTSPOTPWD + " text);";
    
    
    public static final String BOUQUET_TABLE = "Bouquet";
    public static final String BOUQUET_ID = "Bouquet_id";
    public static final String BOUQUET_NAME = "Bouquet_name";
    public static final String BOUQUET_CATEGORY = "Category";
    public static final String BOUQUET_DATE = "date";
    public static final String BOUQUET_TIMESTAMP = "timestamp";
    //DVB middleware
    public static final String BOUQUET_SERVICE_ID = "Service_id";
    public static final String BOUQUET_TS_ID = "TS_id";
    public static final String BOUQUET_LCN = "LCN";
    
    private static final String BOUQUET_TABLE_CREATE = "create table "
			+ BOUQUET_TABLE + "( " + BOUQUET_ID
			+ " integer primary key AUTOINCREMENT, " + BOUQUET_NAME
			+ " text, "+ BOUQUET_CATEGORY
			+ " text, "+ BOUQUET_DATE
			+ " text, "+ BOUQUET_TIMESTAMP
			+ " text);";
    
    //DVB middleware
    private static final String DVB_BOUQUET_TABLE_CREATE = "create table "
			+ BOUQUET_TABLE + "( " + BOUQUET_ID + " integer , " 
			+ BOUQUET_NAME + " text, "
			+ BOUQUET_SERVICE_ID + " integer, "
    		+ BOUQUET_TS_ID + " integer, "
    		+ BOUQUET_LCN + " integer, "
    		+ BOUQUET_CATEGORY + " text, "
    		+ BOUQUET_DATE + " text, "
			+ BOUQUET_TIMESTAMP + " text, " 
			+ "PRIMARY KEY ("+ BOUQUET_ID+ ","+ BOUQUET_SERVICE_ID + ","+ BOUQUET_TS_ID +"));";
    
    
    public static final String PROFILE_TABLE = "Profile";
    public static final String PROFILE_USER_ID = "User_id";
    public static final String PROFILE_USER_NAME = "User_name";
    public static final String PROFILE_IMAGE_PWD = "Image_pwd";
    public static final String PROFILE_FB_ID = "FB_id";
    public static final String PROFILE_FB_TOKEN = "FB_token";
    public static final String PROFILE_FB_SYNC = "FB_sync";
    public static final String PROFILE_FB_PHOTO = "FB_photo";
    public static final String PROFILE_SUBSCRIBER_ID = "Subscriber_id";
    public static final String PROFILE_SUB_STATUS = "Sub_status";
    public static final String PROFILE_LAST_VIEWED_SERVICE = "Last_viewed_service";
    public static final String PROFILE_LAST_VIEWED = "Last_viewed";
    public static final String PROFILE_NETWORK_ID = "Network_id";
    
    private static final String PROFILE_TABLE_CREATE = "create table "
			+ PROFILE_TABLE + "( " + PROFILE_USER_ID
			+ " integer primary key, " + PROFILE_USER_NAME
			+ " text, "+ PROFILE_IMAGE_PWD
			+ " text, "+ PROFILE_FB_ID
			+ " text, "+ PROFILE_FB_TOKEN
			+ " text, "+ PROFILE_FB_SYNC
			+ " integer, "+ PROFILE_FB_PHOTO
			+ " text, "+ PROFILE_SUBSCRIBER_ID
			+ " text, "+ PROFILE_SUB_STATUS
			+ " integer, "+ PROFILE_LAST_VIEWED_SERVICE
			+ " integer, "+ PROFILE_LAST_VIEWED
			+ " integer, "+ PROFILE_NETWORK_ID
			+ " text);";
    
    public static final String STATUS_TABLE = "Status";
    public static final String STATUS_USER_ID = "User_id";
    public static final String STATUS_SERVICE_ID = "Service_id";
    public static final String STATUS_EVENT_ID = "Event_id";
    public static final String STATUS_UNIQUE_ID = "Unique_id";
    public static final String STATUS_STATUS = "Status";
    public static final String STATUS_FREQUENCY = "Frequency";
    public static final String STATUS_LAST_VIEWED = "Last_viewed";
    public static final String STATUS_TIMESTAMP = "timestamp";
    public static final String STATUS_DATE = "date";
    
    private static final String STATUS_TABLE_CREATE = "create table "
			+ STATUS_TABLE + "( " + STATUS_USER_ID
			+ " integer, " + STATUS_SERVICE_ID
			+ " integer, "+ STATUS_EVENT_ID
			+ " integer, "+ STATUS_UNIQUE_ID
			+ " integer, "+ STATUS_STATUS
			+ " integer, "+ STATUS_FREQUENCY
			+ " integer, "+ STATUS_LAST_VIEWED
			+ " integer, "+ STATUS_DATE
			+ " text, "+ STATUS_TIMESTAMP
			+ " text);";
    
    public static final String PROGRAM_TABLE = "Program";
    public static final String PROGRAM_EVENT_ID = "Event_id";
    public static final String PROGRAM_EVENT_SRC = "Event_src";
    public static final String PROGRAM_TYPE = "Type";
    public static final String PROGRAM_CHANNEL_SERVICE_ID = "Service_id";
    public static final String PROGRAM_GENRE = "Genre";
//    public static final String PROGRAM_CONTENT  = "Content";
    public static final String PROGRAM_PRICE = "Price";
    public static final String PROGRAM_PRICING_MODEL = "Pricing_model";
    public static final String PROGRAM_EXPIRY_DATE = "Expiry_date";
//    public static final String PROGRAM_VIEWS = "Views";
    public static final String PROGRAM_DATE_ADDED = "Date_added";
    public static final String PROGRAM_DESCRIPTION = "Description";
    public static final String PROGRAM_MATURITY = "Maturity";
    public static final String PROGRAM_IMAGE = "Image";
    public static final String PROGRAM_RANKING = "Ranking";
    public static final String PROGRAM_ACTORS = "Actors";
    public static final String PROGRAM_DIRECTOR = "Director";
    public static final String PROGRAM_MUSIC_DIRECTOR = "Music_director";
    public static final String PROGRAM_PRODUCTIONHOUSE = "Productionhouse";
    public static final String PROGRAM_START_TIME = "Start_time";
    public static final String PROGRAM_DURATION = "Duration";
    public static final String PROGRAM_RATING = "Rating";
    public static final String PROGRAM_LANGUAGE = "Language";
    public static final String PROGRAM_EVENT_NAME = "Event_name";
    public static final String PROGRAM_EVENT_CATEGORY = "Event_category";
    public static final String PROGRAM_UNIQUE_ID = "Unique_id";
    public static final String PROGRAM_TIMESTAMP = "timestamp";
    public static final String PROGRAM_BOUQUET_ID = "Bouquet_id";
    public static final String PROGRAM_CHANNELNAME = "Channel_name";
    public static final String PROGRAM_COLLECTIONID = "Collection_id";
    public static final String PROGRAM_COLLECTIONNAME = "Collection_name";
    public static final String PROGRAM_DATE = "date";
    public static final String PROGRAM_SUMMARY = "Summary";
    //DVB middleware
    public static final String PROGRAM_START_DATE = "Start_date";
    public static final String PROGRAM_RUNNING_STATUS = "running_status";
    public static final String PROGRAM_FREE_CA_MODE = "free_CA_mode";
    
    private static final String PROGRAM_TABLE_CREATE = "create table "
			+ PROGRAM_TABLE + "( " + PROGRAM_EVENT_ID
			+ " integer primary key AUTOINCREMENT, " + PROGRAM_EVENT_SRC
			+ " text, "+ PROGRAM_TYPE
			+ " text, "+ PROGRAM_CHANNEL_SERVICE_ID
			+ " integer, "+ PROGRAM_GENRE
			+ " text, "+ PROGRAM_PRICE
			+ " real, "+ PROGRAM_PRICING_MODEL
			+ " text, "+ PROGRAM_EXPIRY_DATE
			+ " text, "+ PROGRAM_DATE_ADDED
			+ " text, "+ PROGRAM_DESCRIPTION
			+ " text, "+ PROGRAM_MATURITY
			+ " text, "+ PROGRAM_IMAGE
			+ " text, "+ PROGRAM_RANKING
			+ " integer, "+ PROGRAM_ACTORS
			+ " text, "+ PROGRAM_DIRECTOR
			+ " text, "+ PROGRAM_MUSIC_DIRECTOR
			+ " text, "+ PROGRAM_PRODUCTIONHOUSE
			+ " text, "+ PROGRAM_START_TIME
			+ " text, "+ PROGRAM_DURATION
			+ " text, "+ PROGRAM_RATING
			+ " text, "+ PROGRAM_LANGUAGE
			+ " text, "+ PROGRAM_EVENT_NAME
			+ " text, "+ PROGRAM_EVENT_CATEGORY
			+ " text, "+ PROGRAM_UNIQUE_ID
			+ " integer, "+ PROGRAM_SUMMARY
			+ " text, "+ PROGRAM_BOUQUET_ID
			+ " integer, "+ PROGRAM_CHANNELNAME
			+ " text, "+ PROGRAM_COLLECTIONID
			+ " integer, "+ PROGRAM_COLLECTIONNAME
			+ " text, "+ PROGRAM_DATE
			+ " text, "+ PROGRAM_TIMESTAMP
			+ " text);";
    
    //DVB middleware
    private static final String DVB_PROGRAM_TABLE_CREATE = "create table "
			+ PROGRAM_TABLE + "( " + PROGRAM_EVENT_ID
			+ " integer, " + PROGRAM_EVENT_SRC
			+ " text, "+ PROGRAM_TYPE
			+ " text, "+ PROGRAM_CHANNEL_SERVICE_ID
			+ " integer, "+ PROGRAM_GENRE
			+ " text, "+ PROGRAM_PRICE
			+ " real, "+ PROGRAM_PRICING_MODEL
			+ " text, "+ PROGRAM_EXPIRY_DATE
			+ " text, "+ PROGRAM_DATE_ADDED
			+ " text, "+ PROGRAM_DESCRIPTION
			+ " text, "+ PROGRAM_MATURITY
			+ " text, "+ PROGRAM_IMAGE
			+ " text, "+ PROGRAM_RANKING
			+ " integer, "+ PROGRAM_ACTORS
			+ " text, "+ PROGRAM_DIRECTOR
			+ " text, "+ PROGRAM_MUSIC_DIRECTOR
			+ " text, "+ PROGRAM_PRODUCTIONHOUSE
			+ " text, "+ PROGRAM_START_DATE
			+ " text, "+ PROGRAM_START_TIME
			+ " text, "+ PROGRAM_DURATION
			+ " text, "+ PROGRAM_RUNNING_STATUS
			+ " integer, "+ PROGRAM_FREE_CA_MODE
			+ " integer, "+ PROGRAM_RATING
			+ " text, "+ PROGRAM_LANGUAGE
			+ " text, "+ PROGRAM_EVENT_NAME
			+ " text, "+ PROGRAM_EVENT_CATEGORY
			+ " text, "+ PROGRAM_UNIQUE_ID
			+ " integer, "+ PROGRAM_SUMMARY
			+ " text, "+ PROGRAM_BOUQUET_ID
			+ " integer, "+ PROGRAM_CHANNELNAME
			+ " text, "+ PROGRAM_COLLECTIONID
			+ " integer, "+ PROGRAM_COLLECTIONNAME
			+ " text, "+ PROGRAM_DATE
			+ " text, "+ PROGRAM_TIMESTAMP
			+ " text, PRIMARY KEY ("+ PROGRAM_EVENT_ID+ ","+ PROGRAM_CHANNEL_SERVICE_ID +"));";
    
    
    public static final String CHANNEL_TABLE = "Channel";
    public static final String CHANNEL_SERVICE_ID = "Service_id";
    public static final String CHANNEL_TS_ID = "TS_id";
    public static final String CHANNEL_NETWORK_ID = "Network_id";
    public static final String CHANNEL_TYPE = "Type";
    public static final String CHANNEL_NAME = "Channel_name";
    public static final String CHANNEL_BOUQUET_ID = "Bouquet_id";
    public static final String CHANNEL_USER_ID = "User_id";
    public static final String CHANNEL_DESCRIPTION = "Description";
    public static final String CHANNEL_MATURITY = "Maturity";
    public static final String CHANNEL_PRICE = "Price";
    public static final String CHANNEL_PRICING_MODEL = "Pricing_model";
    public static final String CHANNEL_EXPIRY_DATE = "Expiry_date";
    public static final String CHANNEL_CA_SCRAMBLED = "Ca_scrambled";
    public static final String CHANNEL_RUNNING_STATUS = "Running_status";
    public static final String CHANNEL_CATEGORY = "serviceCategory";
    public static final String CHANNEL_LOGO = "Channel_logo";
    public static final String CHANNEL_TIMESTAMP = "timestamp";
    public static final String CHANNEL_DATE = "date";
    public static final String CHANNEL_URL = "channelurl";
    //DVB middleware
    public static final String CHANNEL_EIT_SCHEDULE = "EIT_Schedule";
    public static final String CHANNEL_EIT_PRESENT = "EIT_Present";
    public static final String CHANNEL_LCN = "LCN";
    public static final String CHANNEL_DVBTYPE = "Service_type";
    
    private static final String CHANNEL_TABLE_CREATE = "create table "
			+ CHANNEL_TABLE + "( " + CHANNEL_SERVICE_ID
			+ " integer, "+ CHANNEL_TS_ID
			+ " integer, "+ CHANNEL_NETWORK_ID
			+ " text, "+ CHANNEL_TYPE
			+ " text, "+ CHANNEL_NAME
			+ " text, "+ CHANNEL_BOUQUET_ID
			+ " integer, "+ CHANNEL_USER_ID
			+ " integer, "+ CHANNEL_DESCRIPTION
			+ " text, "+ CHANNEL_MATURITY
			+ " text, "+ CHANNEL_PRICE
			+ " real, "+ CHANNEL_PRICING_MODEL
			+ " text, "+ CHANNEL_EXPIRY_DATE
			+ " text, "+ CHANNEL_CA_SCRAMBLED
			+ " integer, "+ CHANNEL_RUNNING_STATUS
			+ " integer, "+ CHANNEL_CATEGORY
			+ " text, "+ CHANNEL_LOGO
			+ " text, "+ CHANNEL_DATE
			+ " text, "+ CHANNEL_TIMESTAMP
			+ " text, "+ CHANNEL_URL
			+ " text);";
    
    //DVB middleware
    private static final String DVB_CHANNEL_TABLE_CREATE = "create table "
			+ CHANNEL_TABLE + "( " + CHANNEL_SERVICE_ID
			+ " integer, "+ CHANNEL_TS_ID
			+ " integer, "+ CHANNEL_NETWORK_ID
			+ " integer, "+ CHANNEL_TYPE
			+ " text, "+ CHANNEL_NAME
			+ " text, "+ CHANNEL_BOUQUET_ID
			+ " integer, "+ CHANNEL_USER_ID
			+ " integer, "+ CHANNEL_DESCRIPTION
			+ " text, "+ CHANNEL_MATURITY
			+ " text, "+ CHANNEL_PRICE
			+ " real, "+ CHANNEL_PRICING_MODEL
			+ " text, "+ CHANNEL_EXPIRY_DATE
			+ " text, "+ CHANNEL_CA_SCRAMBLED
			+ " integer, "+ CHANNEL_RUNNING_STATUS
			+ " integer, "+ CHANNEL_EIT_SCHEDULE
			+ " integer, "+ CHANNEL_EIT_PRESENT
			+ " integer, "+ CHANNEL_CATEGORY
			+ " text, "+ CHANNEL_LOGO
			+ " text, "+ CHANNEL_DATE
			+ " text, "+ CHANNEL_TIMESTAMP
			+ " text, "+ CHANNEL_LCN
			+ " integer, "+ CHANNEL_DVBTYPE
			+ " integer, "+ CHANNEL_URL
			+ " text);";
    
    
	// TABLE Network
	// ---------------------
	// 1) ONID UNSIGNED INT PRIMARY KEY NOT NULL
	// 2) Name TEXT NOT NULL
	// 3) NWID UNSIGNED INT NOT NULL
	public static final String NETWORK_TABLE = "Network";
	public static final String NETWORK_ONID = "ONID";
	public static final String NETWORK_NAME = "Name";
	public static final String NETWORK_NWID = "NWID";

	private static final String NETWORK_TABLE_CREATE = "create table "
			+ NETWORK_TABLE + "( " + NETWORK_ONID + " integer , " 
    		+ NETWORK_NAME + " text, "
			+ NETWORK_NWID + " integer);";

	// TABLE Version_Table
	// ----------------------------
	// 1) Table_Type UNSIGNED INT NOT NULL
	// 2) Table_ID UNSIGNED INT NOT NULL
	// 3) Version UNSIGNED INT
	// PRIMARY KEY (Table_Type,Table_ID)
	// CREATE TABLE something (column1, column2, column3, PRIMARY KEY (column1,
	// column2));
	public static final String VERSION_TABLE = "TblVersion";
	public static final String VERSION_TYPE = "Table_Type";
	public static final String VERSION_TYPEID = "Table_ID";
	public static final String VERSION_NO = "Version";

	private static final String VERSION_TABLE_CREATE = "create table "
			+ VERSION_TABLE + "( " + NETWORK_ONID + " integer , " 
    		+ VERSION_TYPE + " integer, "
    		+ VERSION_TYPEID + " integer, "
			+ VERSION_NO + " integer, " 
			+ "PRIMARY KEY ("+ VERSION_TYPE+ ","+ VERSION_TYPEID +"));";

	// TABLE TS
	// --------------
	// 1) TSID UNSIGNED INT PRIMARY KEY NOT NULL
	// 2) ONID UNSIGNED INT NOT NULL
	// 3) Frequency UNSIGNED INT NOT NULL
	public static final String TS_TABLE = "TS";
	public static final String TS_TSID = "TSID";
	public static final String TS_ONID = "ONID";
	public static final String TS_FREQUENCY = "Frequency";

	private static final String TS_TABLE_CREATE = "create table "
			+ TS_TABLE + "( " + TS_TSID + " integer , " 
    		+ TS_ONID + " integer, "
			+ TS_FREQUENCY + " integer);";

	// TABLE Delivery
	// ---------------------
	// 1) type UNSIGNED INT NOT NULL
	// 2) frequency UNSIGNED INT NOT NULL
	// 3) symbol_rate UNSIGNED INT
	// 4) orbital_position UNSIGNED INT
	// 5) modulation UNSIGNED INT
	// 6) FEC_inner UNSIGNED INT
	// 7) west_east_flag UNSIGNED INT
	// 8) polarisation UNSIGNED INT
	// 9) FEC_outer UNSIGNED INT
	// 10) bandwidth UNSIGNED INT
	// 11) hierarchy_info UNSIGNED INT
	// 12) code_rate_lo UNSIGNED INT
	// 13) code_rate_hi UNSIGNED INT
	// 14) constellation UNSIGNED INT
	// 15) guard_interval UNSIGNED INT
	// 16) transmission_mode UNSIGNED INT
	// 17) other_frequency_flag UNSIGNED INT
	// 18) plp_id UNSIGNED INT
	// 19) siso_miso UNSIGNED INT
	public static final String DELIVERY_TABLE = "Delivery";
	public static final String DELIVERY_TYPE = "type";
	public static final String DELIVERY_FREQUENCY = "frequency";
	public static final String DELIVERY_SYMBOL_RATE = "symbol_rate";
	public static final String DELIVERY_ORBITAL_POSITION = "orbital_position";
	public static final String DELIVERY_MODULATION = "modulation";
	public static final String DELIVERY_FEC_INNER = "FEC_inner";
	public static final String DELIVERY_WEST_EAST_FLAG = "west_east_flag";
	public static final String DELIVERY_POLARISATION = "polarisation";
	public static final String DELIVERY_FEC_OUTER = "FEC_outer";
	public static final String DELIVERY_BANDWIDTH = "bandwidth";
	public static final String DELIVERY_HIERARCHY_INFO = "hierarchy_info";
	public static final String DELIVERY_CODE_RATE_LO = "code_rate_lo";
	public static final String DELIVERY_CODE_RATE_HI = "code_rate_hi";
	public static final String DELIVERY_CONSTELLATION = "constellation";
	public static final String DELIVERY_GUARD_INTERVAL = "guard_interval";
	public static final String DELIVERY_TRANSMISSION_MODE = "transmission_mode";
	public static final String DELIVERY_OTHER_FREQUENCY_FLAG = "other_frequency_flag";
	public static final String DELIVERY_PLP_ID = "plp_id";
	public static final String DELIVERY_SISO_MISO = "siso_miso";

	private static final String DELIVERY_TABLE_CREATE = "create table "
			+ DELIVERY_TABLE + "( " + DELIVERY_TYPE + " integer , " 
    		+ DELIVERY_FREQUENCY + " integer, "
    		+ DELIVERY_SYMBOL_RATE + " integer, "
    		+ DELIVERY_ORBITAL_POSITION + " integer, "
    		+ DELIVERY_MODULATION + " integer, "
    		+ DELIVERY_FEC_INNER + " integer, "
    		+ DELIVERY_WEST_EAST_FLAG + " integer, "
    		+ DELIVERY_POLARISATION + " integer, "
    		+ DELIVERY_FEC_OUTER + " integer, "
    		+ DELIVERY_BANDWIDTH + " integer, "
    		+ DELIVERY_HIERARCHY_INFO + " integer, "
    		+ DELIVERY_CODE_RATE_LO + " integer, "
    		+ DELIVERY_CODE_RATE_HI + " integer, "
    		+ DELIVERY_CONSTELLATION + " integer, "
    		+ DELIVERY_GUARD_INTERVAL + " integer, "
    		+ DELIVERY_TRANSMISSION_MODE + " integer, "
    		+ DELIVERY_OTHER_FREQUENCY_FLAG + " integer, "
    		+ DELIVERY_PLP_ID + " integer, "
			+ DELIVERY_SISO_MISO + " integer, " 
			+ "PRIMARY KEY ("+ DELIVERY_TYPE+ ","+ DELIVERY_FREQUENCY +"));";
    

	// TABLE Auto_Scan_Freq_List
	// -------------------------------------
	// 1) Sl_no INTEGER PRIMARY KEY AUTOINCREMENT
	// 2) Frequency UNSIGNED INT NOT NULL
	public static final String AUTOSCAN_TABLE = "FreqList";
	public static final String AUTOSCAN_SL_NO = "Sl_no";
	public static final String AUTOSCAN_FREQUENCY = "Frequency";

	private static final String AUTOSCAN_TABLE_CREATE = "create table "
			+ AUTOSCAN_TABLE + "( " + AUTOSCAN_SL_NO
			+ " integer primary key AUTOINCREMENT , " + AUTOSCAN_FREQUENCY
			+ " integer);";

	// TABLE PAT
	// ----------------
	// 1)Program_No UNSIGNED INT PRIMARY KEY NOT NULL
	// 2)PMT_Pid UNSIGNED INT NOT NULL
	// 3)TS_id UNSIGNED INT NOT NULL
	// 4)Frequency UNSIGNED INT NOT NULL
	public static final String PAT_TABLE = "PAT";
	public static final String PAT_PROGRAM_NO = "Program_No";
	public static final String PAT_PMT_PID = "PMT_Pid";
	public static final String PAT_TS_ID = "TS_id";
	public static final String PAT_FREQUENCY = "Frequency";

	private static final String PAT_TABLE_CREATE = "create table "
			+ PAT_TABLE + "( " + PAT_PROGRAM_NO + " integer primary key, " 
			+ PAT_PMT_PID + " integer, "
    		+ PAT_TS_ID + " integer, "
			+ PAT_FREQUENCY + " integer);";
	// TABLE PMT
	// ----------------
	// 1)Service_id UNSIGNED INT
	// 2)Pid UNSIGNED INT
	// 3)Component_Type UNSIGNED INT
	// PRIMARY KEY (Service_id,Component_Type)
	public static final String PMT_TABLE = "PMT";
	public static final String PMT_SERVICE_ID = "Service_id";
	public static final String PMT_PID = "Pid";
	public static final String PMT_COMPONENT_TYPE = "Component_Type";

	private static final String PMT_TABLE_CREATE = "create table "
			+ PMT_TABLE + "( " + PMT_SERVICE_ID + " integer, " 
    		+ PMT_PID + " integer, "
			+ PMT_COMPONENT_TYPE + " integer, " 
			+ "PRIMARY KEY ("+ PMT_SERVICE_ID+ ","+ PMT_COMPONENT_TYPE +"));";
  

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			if (Constant.DVB) {
				db.execSQL(DVB_BOUQUET_TABLE_CREATE);
				db.execSQL(DVB_PROGRAM_TABLE_CREATE);
				db.execSQL(DVB_CHANNEL_TABLE_CREATE);
				db.execSQL(PROFILE_TABLE_CREATE);
				db.execSQL(STATUS_TABLE_CREATE);
				
				db.execSQL(NETWORK_TABLE_CREATE);
				db.execSQL(VERSION_TABLE_CREATE);
				db.execSQL(TS_TABLE_CREATE);
				db.execSQL(DELIVERY_TABLE_CREATE);
				db.execSQL(AUTOSCAN_TABLE_CREATE);
				db.execSQL(PMT_TABLE_CREATE);
				db.execSQL(PAT_TABLE_CREATE);
				db.execSQL(CACHEDATA_TABLE_CREATE);
			} else {
				db.execSQL(BOUQUET_TABLE_CREATE);
				db.execSQL(PROGRAM_TABLE_CREATE);
				db.execSQL(CHANNEL_TABLE_CREATE);
				db.execSQL(PROFILE_TABLE_CREATE);
				db.execSQL(STATUS_TABLE_CREATE);
				db.execSQL(CACHEDATA_TABLE_CREATE);
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	  }
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try{
			if (Constant.DVB) {
				db.execSQL("DROP TABLE IF EXISTS " + BOUQUET_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + STATUS_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PROGRAM_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + CHANNEL_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + CACHEDATA_TABLE);
				
				db.execSQL("DROP TABLE IF EXISTS " + NETWORK_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + VERSION_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + TS_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + DELIVERY_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + AUTOSCAN_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PMT_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PAT_TABLE);
			}else{
				db.execSQL("DROP TABLE IF EXISTS " + BOUQUET_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + STATUS_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + PROGRAM_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + CHANNEL_TABLE);
				db.execSQL("DROP TABLE IF EXISTS " + CACHEDATA_TABLE);
			}
			
			onCreate(db);
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
