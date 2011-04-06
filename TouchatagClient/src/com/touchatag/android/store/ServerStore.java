package com.touchatag.android.store;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ServerStore {

	// TOUCHATAG("Touchatag server", "https://acs.touchatag.com", null, null),
	// //
	// PRESALES3("Presales 3", "https://presales3.ttag.be",
	// "2b81-633c-9511-487f-82d9-702b-4559-7ec7",
	// "CwoPyeN0JNl9DrpsVZbIU3AIUgz6vTAm"), //
	// LOCAL_EMULATOR("Local server for emulator", "http://10.0.2.2:8080",
	// "25f1-4e7e-ec4c-4680-8133-a8cd-47c1-759e",
	// "cWPHxlsdtmOfHLXVT4s7XxqB7IwZhNpz"), //
	// NG_CONNECT("NG Connect", "http://ngconnect.ttag.be", null, null);

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "db.touchatag.servers";
	private static final String TABLE_NAME = "server";

	private DbOpenHelper dbOpenHelper;

	public ServerStore(Context ctx) {
		dbOpenHelper = new DbOpenHelper(ctx);
	}

	public void store(Server server) {
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getWritableDatabase();
			insert(server, db);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	private void insert(Server server, SQLiteDatabase db){
		ContentValues values = new ContentValues();
		values.put(Columns.NAME, server.getName());
		values.put(Columns.URL, server.getUrl());
		values.put(Columns.KEY, server.getKey());
		values.put(Columns.SECRET, server.getSecret());
		db.insert(TABLE_NAME, null, values);
	}

	public Server findByUrl(String url) {
		Server server = null;
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.NAME, Columns.URL, Columns.KEY, Columns.SECRET }, Columns.URL + "=?", new String[]{url}, null, null, null);
			if (cursor.moveToFirst()) {
				server = new Server(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
			}
			cursor.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}

		return server;
	}

	public List<Server> findAll() {
		List<Server> servers = new ArrayList<Server>();
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.NAME, Columns.URL, Columns.KEY, Columns.SECRET }, null, null, null, null, null);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					servers.add(new Server(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
					cursor.moveToNext();
				}
			}
			cursor.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}

		return servers;
	}

	private static class Columns implements BaseColumns {

		private static final String NAME = "name";

		private static final String URL = "url";

		private static final String KEY = "key";

		private static final String SECRET = "secret";

	}

	private class DbOpenHelper extends SQLiteOpenHelper {

		DbOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + //
					Columns._ID + " INTEGER PRIMARY KEY," + //
					Columns.NAME + " TEXT," + //
					Columns.URL + " TEXT," + //
					Columns.KEY + " TEXT," + //
					Columns.SECRET + " TEXT);");
			
			insert(new Server("Touchatag server", "https://acs.touchatag.com", null, null), db);
			insert(new Server("Local server for emulator", "http://10.0.2.2:8080", "25f1-4e7e-ec4c-4680-8133-a8cd-47c1-759e", "cWPHxlsdtmOfHLXVT4s7XxqB7IwZhNpz"), db);
			insert(new Server("Presales 3", "https://presales3.ttag.be", "2b81-633c-9511-487f-82d9-702b-4559-7ec7", "CwoPyeN0JNl9DrpsVZbIU3AIUgz6vTAm"), db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);

		}

	}
}
