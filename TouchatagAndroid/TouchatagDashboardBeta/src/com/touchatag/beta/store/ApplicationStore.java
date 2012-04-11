package com.touchatag.beta.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.touchatag.acs.api.client.model.Application;
import com.touchatag.acs.api.client.model.ApplicationPage;
import com.touchatag.acs.api.client.model.specification.Specification;

public class ApplicationStore {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "db.touchatag.applications";
	private static final String TABLE_NAME = "application";

	private DbOpenHelper dbOpenHelper;

	public ApplicationStore(Context ctx) {
		dbOpenHelper = new DbOpenHelper(ctx);
	}

	public void store(Application app) {
		SQLiteDatabase db = null;
		try {
			Map<String, Boolean> existsMap = exists(Arrays.asList(new String[] { app.getId() }));
			db = dbOpenHelper.getWritableDatabase();
			if (existsMap.get(app.getId())) {
				update(app, db);
			} else {
				insert(app, db);
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void update(Application app, SQLiteDatabase db) {
		try {
			ContentValues values = new ContentValues();
			values.put(Columns.ID, app.getId());
			values.put(Columns.OWNERID, app.getOwnerId());
			values.put(Columns.CREATED, app.getCreated());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(app.getSpecification());
			values.put(Columns.SPECIFICATION, baos.toByteArray());
			values.put(Columns.NAME, app.getName());
			values.put(Columns.DESCRIPTION, app.getDescription());
			values.put(Columns.TEMPLATE, app.getTemplate());

			long result = db.update(TABLE_NAME, values, Columns.ID + "=?", new String[] { app.getId() });
			if (result == -1) {
				throw new RuntimeException("Update of application failed");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void insert(Application app, SQLiteDatabase db) {
		try {
			ContentValues values = new ContentValues();
			values.put(Columns.ID, app.getId());
			values.put(Columns.OWNERID, app.getOwnerId());
			values.put(Columns.CREATED, app.getCreated());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(app.getSpecification());
			values.put(Columns.SPECIFICATION, baos.toByteArray());
			values.put(Columns.NAME, app.getName());
			values.put(Columns.DESCRIPTION, app.getDescription());
			values.put(Columns.TEMPLATE, app.getTemplate());
			long result = db.insert(TABLE_NAME, null, values);
			if (result == -1) {
				throw new RuntimeException("Insert of application failed");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void delete(String id, SQLiteDatabase db) {
		db.delete(TABLE_NAME, Columns.ID + "=?", new String[] { id });
	}

	public void remove(Application app) {
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getWritableDatabase();
			delete(app.getId(), db);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public ApplicationPage getPage(int pageNumber, int pageSize) {
		ApplicationPage page = new ApplicationPage();
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			String limitClause = ((pageNumber - 1) * pageSize) + ", " + pageSize;
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.ID, Columns.OWNERID, Columns.CREATED, Columns.SPECIFICATION, Columns.NAME, Columns.DESCRIPTION, Columns.TEMPLATE }, null, null,
					null, null, null, limitClause);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Application app = createAppFromCursor(cursor);
					page.getItems().add(app);
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
		return page;
	}

	public Application findByIdentifier(String appIdentifier) {
		Application app = null;
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.ID, Columns.OWNERID, Columns.CREATED, Columns.SPECIFICATION, Columns.NAME, Columns.DESCRIPTION, Columns.TEMPLATE }, Columns.ID
					+ "=?", new String[] { appIdentifier }, null, null, null);
			if (cursor.moveToFirst()) {
				app = createAppFromCursor(cursor);
			}
			cursor.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return app;
	}

	public List<Application> findByIdentifierNotIn(List<String> identifiers) {
		List<Application> apps = new ArrayList<Application>();

		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			int size = identifiers.size();
			String inClause = "";
			for (int i = 0; i < size; i++) {
				inClause += "'" + identifiers.get(i) + "'";
				if (i < size - 1) {
					inClause += ",";
				}
			}
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.ID, Columns.OWNERID, Columns.CREATED, Columns.SPECIFICATION, Columns.NAME, Columns.DESCRIPTION, Columns.TEMPLATE }, Columns.ID
					+ " NOT IN (" + inClause + ")", null, null, null, null);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Application app = createAppFromCursor(cursor);
					apps.add(app);
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
		return apps;
	}

	private Application createAppFromCursor(Cursor cursor) {
		Application app = new Application();
		app.setId(cursor.getString(0));
		app.setOwnerId(cursor.getString(1));
		app.setCreated(cursor.getString(2));

		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(cursor.getBlob(3));
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			if (obj instanceof Specification) {
				app.setSpecification((Specification) obj);
			}
		} catch (Exception e) {
			
		}
		app.setName(cursor.getString(4));
		app.setDescription(cursor.getString(5));
		app.setTemplate(cursor.getString(6));
		return app;
	}

	public Map<String, Boolean> exists(List<String> identifiers) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		for (String identifier : identifiers) {
			map.put(identifier, false);
		}

		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			int size = identifiers.size();
			String inClause = "";
			for (int i = 0; i < size; i++) {
				inClause += "'" + identifiers.get(i) + "'";
				if (i < size - 1) {
					inClause += ",";
				}
			}
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.ID }, Columns.ID + " IN (" + inClause + ")", null, null, null, null);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					map.put(cursor.getString(0), true);
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
		return map;
	}

	public List<Application> findAll(String ownerId) {
		List<Application> applications = new ArrayList<Application>();
		if (ownerId == null) {
			return applications;
		}
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.ID, Columns.OWNERID, Columns.CREATED, Columns.SPECIFICATION, Columns.NAME, Columns.DESCRIPTION, Columns.TEMPLATE },
					Columns.OWNERID + "=?", new String[] { ownerId }, null, null, null);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Application app = createAppFromCursor(cursor);
					applications.add(app);
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

		return applications;
	}

	private static class Columns implements BaseColumns {

		private static final String ID = "id";

		private static final String OWNERID = "ownerId";

		private static final String CREATED = "created";

		private static final String SPECIFICATION = "specification";

		private static final String NAME = "name";

		private static final String DESCRIPTION = "description";

		private static final String TEMPLATE = "template";

	}

	private class DbOpenHelper extends SQLiteOpenHelper {

		DbOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + //
					Columns._ID + " INTEGER PRIMARY KEY," + //
					Columns.ID + " TEXT UNIQUE," + //
					Columns.OWNERID + " TEXT," + //
					Columns.CREATED + " TEXT," + //
					Columns.SPECIFICATION + " BLOB," + //
					Columns.NAME + " TEXT," + //
					Columns.DESCRIPTION + " TEXT," + //
					Columns.TEMPLATE + " TEXT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);

		}

	}
}
