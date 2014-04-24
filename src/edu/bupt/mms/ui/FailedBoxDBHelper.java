package edu.bupt.mms.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FailedBoxDBHelper extends SQLiteOpenHelper{

	
	final String CREATE_TABLE_SQL="CREATE TABLE failedbox (_id INTEGER PRIMARY KEY,thread_id INTEGER,address TEXT,person INTEGER,date INTEGER,date_sent INTEGER DEFAULT 0,protocol INTEGER,read INTEGER DEFAULT 0,status INTEGER DEFAULT -1,type INTEGER,reply_path_present TEXT,subject TEXT,body TEXT,service_center TEXT,service_date INTEGER,dest_port INTEGER,locked INTEGER DEFAULT 0,sub_id INTEGER DEFAULT 0,error_code INTEGER DEFAULT 0,seen INTEGER DEFAULT 0,recipient_cc_ids TEXT ,recipient_bcc_ids TEXT ,sms_pdu TEXT ,expiry INTEGER DEFAULT 0,pri INTEGER DEFAULT -1)";
	
	final String CREATE_MMS_TABLE_SQL="CREATE TABLE mmsfailedbox (_id INTEGER PRIMARY KEY,msg_id INTEGER,name TEXT,address TEXT,thread_id INTEGER,date INTEGER,date_sent INTEGER DEFAULT 0,msg_box INTEGER,read INTEGER DEFAULT 0,m_id TEXT,sub TEXT,sub_cs INTEGER,ct_t TEXT,ct_l TEXT,exp INTEGER,m_cls TEXT,m_type INTEGER,v INTEGER,m_size INTEGER,pri INTEGER,rr INTEGER,rpt_a INTEGER,resp_st INTEGER,st INTEGER,tr_id TEXT,retr_st INTEGER,retr_txt TEXT,retr_txt_cs INTEGER,read_status INTEGER,ct_cls INTEGER,resp_txt TEXT,d_tm INTEGER,d_rpt INTEGER,locked INTEGER DEFAULT 0,seen INTEGER DEFAULT 0,sub_id INTEGER DEFAULT 0,recipient_cc_ids TEXT,recipient_bcc_ids TEXT)";
	
	
	public FailedBoxDBHelper(Context context, String name, int version){
		super(context, name, null, version);
		
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_SQL);
		db.execSQL(CREATE_MMS_TABLE_SQL);
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		db.execSQL(CREATE_TABLE_SQL);
		db.execSQL(CREATE_MMS_TABLE_SQL);
		System.out.println("------onUpgrade called-------"+oldVersion+"----->"+newVersion);
		
		
	}
	
	

}
