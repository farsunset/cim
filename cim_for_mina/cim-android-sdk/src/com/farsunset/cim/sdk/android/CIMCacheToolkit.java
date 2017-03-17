/**
 * Copyright 2013-2023 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim.sdk.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class CIMCacheToolkit extends SQLiteOpenHelper {

	private static final String DATABASE_NAME  = "CIM_CONFIG_INFO.db";
	private static final int DATABASE_VERSION  = 20160406;
	private static final String TABLE_NAME  = "T_CIM_CONFIG";
	private static  CIMCacheToolkit toolkit;
	
	private static final String TABLE_SQL  = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (KEY VARCHAR(64) PRIMARY KEY,VALUE TEXT)";

	private static final String DELETE_SQL  = "DELETE FROM "+TABLE_NAME+" WHERE KEY = ?";

	private static final String QUERY_SQL  = "SELECT VALUE FROM "+TABLE_NAME+" WHERE KEY = ?";

	private SQLiteDatabase mSQLiteDatabase;

	public static final String CIM_CONFIG_INFO  = "CIM_CONFIG_INFO";
	
	public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
	
	public static final String KEY_MANUAL_STOP = "KEY_MANUAL_STOP";
	
	public static final String KEY_CIM_DESTROYED = "KEY_CIM_DESTROYED";
	
	public static final String KEY_CIM_SERVIER_HOST = "KEY_CIM_SERVIER_HOST";

	public static final String KEY_CIM_SERVIER_PORT = "KEY_CIM_SERVIER_PORT";
	
	public static final String KEY_CIM_CONNECTION_STATE = "KEY_CIM_CONNECTION_STATE";

	
	public synchronized static CIMCacheToolkit getInstance(Context context){
		
        if(toolkit==null){
		    toolkit = new CIMCacheToolkit(context);
	    }
      
	    return toolkit;
	}
	
	 
	public  CIMCacheToolkit(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public CIMCacheToolkit(Context context){
		this(context, DATABASE_NAME,null, DATABASE_VERSION);
	}


	public synchronized void remove(String key)
	{
		getSQLiteDatabase().execSQL(DELETE_SQL,new String[]{key});
	}


	public synchronized void putString(String key,String value)
	{
		
	    ContentValues values = new ContentValues();
	    values.put("VALUE", value);
		int result = getSQLiteDatabase().updateWithOnConflict(TABLE_NAME, values, "KEY=?",new String[]{key},SQLiteDatabase.CONFLICT_FAIL);
		if(result<=0){
			
			values.put("KEY", key);
			getSQLiteDatabase().insert(TABLE_NAME, null, values);
		}

	}
	
	public synchronized String getString(String key)
	{
		String value = null;
		Cursor cursor = getSQLiteDatabase().rawQuery(QUERY_SQL, new String[]{key});
		if (cursor!=null)
		{
			if(cursor.moveToFirst()){
				value = cursor.getString(0);
			}
			
			cursor.close();
		}
		 
		return value;
	}
	
	public  void putBoolean(String key,boolean value)
	{
		putString(key,Boolean.toString(value));
	}
	
	public  boolean getBoolean(String key)
	{
		String value = getString(key);
		return value == null?false:Boolean.parseBoolean(value);
	}
	
	
	public  void putInt(String key,int value)
	{
		putString(key, String.valueOf(value));
	}
	
	public  int getInt(String key)
	{
		String value = getString(key);
		return value == null?0:Integer.parseInt(value);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_SQL);
	}

	
	public  static synchronized  void destroy(){
		if (toolkit!=null){
			try{toolkit.mSQLiteDatabase.close();}catch(Exception e){}
			try{toolkit.close();}catch(Exception e){}
		}
		
		toolkit = null;
		
	}
	
	
	private SQLiteDatabase getSQLiteDatabase(){
		if(mSQLiteDatabase!=null){
			return mSQLiteDatabase;
		}else
		{
			mSQLiteDatabase =  getWritableDatabase();
		}
		return  mSQLiteDatabase;
	}
	
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

	}
}
