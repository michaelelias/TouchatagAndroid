package com.touchatag.beta.store;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.touchatag.acs.api.client.model.ruleset.Association;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;

public class AssociationStore {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "db.touchatag.correlations";
	private static final String TABLE_NAME = "correlation";

	private DbOpenHelper dbOpenHelper;

	public AssociationStore(Context ctx) {
		dbOpenHelper = new DbOpenHelper(ctx);
	}

	/**
	 * Updates the table's rows to the given <code>CorrelationDefinition</code>
	 * Removes existing rows if they are not present in the correlation
	 * definition
	 * 
	 * @param corrDef
	 */
	public void update(CorrelationDefinition corrDef) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		try {
			db.delete(TABLE_NAME, null, null);
			for (Association asso : corrDef.getAssociations()) {
				insert(asso, db);
			}
		} finally {
			db.close();
		}
	}

	public CorrelationDefinition getCorrelationDefinition() {
		List<Association> associations = findAll();
		CorrelationDefinition corrDef = new CorrelationDefinition();
		corrDef.getAssociations().addAll(associations);
		return corrDef;
	}

//	public void store(Association asso) {
//		SQLiteDatabase db = null;
//		try {
//			Map<String, Boolean> existsMap = exists(Arrays.asList(new String[] { app.getId() }));
//			db = dbOpenHelper.getWritableDatabase();
//			if (existsMap.get(app.getId())) {
//				update(app, db);
//			} else {
//				insert(app, db);
//			}
//		} finally {
//			if (db != null) {
//				db.close();
//			}
//		}
//	}

	private void insert(Association asso, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(Columns.TAGHASH, asso.getTagId());
		values.put(Columns.COMMAND, asso.getCommand());
		values.put(Columns.APP_ID, asso.getAppId());
		long result = db.insert(TABLE_NAME, null, values);
		if (result == -1) {
			throw new RuntimeException("Insert of association failed");
		}
	}

	private void delete(String tagHash, SQLiteDatabase db) {
		db.delete(TABLE_NAME, Columns.TAGHASH + "=?", new String[] { tagHash });
	}

	public void remove(Association asso) {
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getWritableDatabase();
			delete(asso.getTagId(), db);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public Association findByTagHash(String tagHash) {
		Association asso = null;
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.TAGHASH, Columns.COMMAND }, Columns.TAGHASH + "=?", new String[] { tagHash }, null, null, null);
			if (cursor.moveToFirst()) {
				asso = createAssociationfromCursor(cursor);
			}
			cursor.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return asso;
	}

	public List<Association> findByAppId(String appId) {
		List<Association> associations = new ArrayList<Association>();
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.TAGHASH, Columns.COMMAND }, Columns.APP_ID + "=?", new String[] { appId }, null, null, null);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Association asso = createAssociationfromCursor(cursor);
					associations.add(asso);
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

		return associations;
	}

	public List<Association> findAll() {
		List<Association> associations = new ArrayList<Association>();
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.TAGHASH, Columns.COMMAND }, null, null, null, null, null);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Association asso = createAssociationfromCursor(cursor);
					associations.add(asso);
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

		return associations;
	}

	private Association createAssociationfromCursor(Cursor cursor) {
		Association asso = new Association();
		asso.setTagId(cursor.getString(0));
		asso.setCommand(cursor.getString(1));
		return asso;
	}

	private static class Columns implements BaseColumns {

		private static final String TAGHASH = "tagHash";

		private static final String APP_ID = "appId";

		private static final String COMMAND = "command";

	}

	private class DbOpenHelper extends SQLiteOpenHelper {

		DbOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + //
					Columns._ID + " INTEGER PRIMARY KEY," + //
					Columns.TAGHASH + " TEXT UNIQUE," + //
					Columns.APP_ID + " TEXT," + //
					Columns.COMMAND + " TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);

		}

	}

}
