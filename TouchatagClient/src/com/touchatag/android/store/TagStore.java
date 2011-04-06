package com.touchatag.android.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.touchatag.android.client.rest.model.ClaimingRule;
import com.touchatag.android.client.rest.model.Page;
import com.touchatag.android.client.rest.model.Tag;

public class TagStore {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "db.touchatag.tags";
	private static final String TABLE_NAME = "tag";

	private DbOpenHelper dbOpenHelper;

	public TagStore(Context ctx) {
		dbOpenHelper = new DbOpenHelper(ctx);
	}

	public void store(Tag tag) {
		SQLiteDatabase db = null;
		try {
			Tag existingTag = findByIdentifier(tag.getIdentifier());
			db = dbOpenHelper.getWritableDatabase();
			if (existingTag == null) {
				insert(tag, db);
			} else {
				update(tag, db);
			}
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	private void update(Tag tag, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(Columns.TYPE, tag.getType());
		values.put(Columns.OWNERID, tag.getOwnerId());
		values.put(Columns.IDENTIFIER, tag.getIdentifier());
		values.put(Columns.HASH, tag.getHash());
		values.put(Columns.DISABLED, Boolean.toString(tag.isDisabled()));
		values.put(Columns.CREATED, tag.getCreated());
		values.put(Columns.CLAIMINGRULE, tag.getClaimingRule().name());
		values.put(Columns.TAGIDHASH, tag.getTagIdHash());
		values.put(Columns.SHORTCODE, tag.getShortcode());

		long result = db.update(TABLE_NAME, values, Columns.IDENTIFIER + "=?", new String[] { tag.getIdentifier() });
		if (result == -1) {
			throw new RuntimeException("Update of tag failed");
		}
	}

	private void insert(Tag tag, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(Columns.TYPE, tag.getType());
		values.put(Columns.OWNERID, tag.getOwnerId());
		values.put(Columns.IDENTIFIER, tag.getIdentifier());
		values.put(Columns.HASH, tag.getHash());
		values.put(Columns.DISABLED, Boolean.toString(tag.isDisabled()));
		values.put(Columns.CREATED, tag.getCreated());
		values.put(Columns.CLAIMINGRULE, tag.getClaimingRule().name());
		values.put(Columns.TAGIDHASH, tag.getTagIdHash());
		values.put(Columns.SHORTCODE, tag.getShortcode());
		db.insert(TABLE_NAME, null, values);
	}

	private void delete(String identifier, SQLiteDatabase db) {
		db.delete(TABLE_NAME, Columns.IDENTIFIER + "=?", new String[] { identifier });
	}

	public void remove(Tag tag) {
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getWritableDatabase();
			delete(tag.getIdentifier(), db);
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	public Page<Tag> getPage(int pageNumber, int pageSize) {
		Page<Tag> page = new Page<Tag>();
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			String limitClause = ((pageNumber - 1) * pageSize) + ", " + pageSize;
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.TYPE, Columns.OWNERID, Columns.IDENTIFIER, Columns.HASH, Columns.DISABLED, Columns.CREATED, Columns.CLAIMINGRULE,
					Columns.TAGIDHASH, Columns.SHORTCODE }, null, null, null, null, null, limitClause);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Tag tag = new Tag();
					tag.setType(cursor.getString(0));
					tag.setOwnerId(cursor.getString(1));
					tag.setIdentifier(cursor.getString(2));
					tag.setHash(cursor.getString(3));
					tag.setDisabled(Boolean.valueOf(cursor.getString(4)));
					tag.setCreated(cursor.getString(5));
					tag.setClaimingRule(ClaimingRule.valueOf(cursor.getString(6)));
					tag.setTagIdHash(cursor.getString(7));
					tag.setShortcode(cursor.getString(8));
					page.getItems().add(tag);
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
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.IDENTIFIER }, Columns.IDENTIFIER + " IN (" + inClause + ")", null, null, null, null);
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

	public Tag findByIdentifier(String identifier) {
		Tag tag = null;
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.TYPE, Columns.OWNERID, Columns.IDENTIFIER, Columns.HASH, Columns.DISABLED, Columns.CREATED, Columns.CLAIMINGRULE,
					Columns.TAGIDHASH, Columns.SHORTCODE }, Columns.IDENTIFIER + "=?", new String[] { identifier }, null, null, null);
			if (cursor.moveToFirst()) {
				tag = new Tag();
				tag.setType(cursor.getString(0));
				tag.setOwnerId(cursor.getString(1));
				tag.setIdentifier(cursor.getString(2));
				tag.setHash(cursor.getString(3));
				tag.setDisabled(Boolean.valueOf(cursor.getString(4)));
				tag.setCreated(cursor.getString(5));
				tag.setClaimingRule(ClaimingRule.valueOf(cursor.getString(6)));
				tag.setTagIdHash(cursor.getString(7));
				tag.setShortcode(cursor.getString(8));
			}
			cursor.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return tag;
	}

	public Tag findByHash(String hash) {
		Tag tag = null;
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.TYPE, Columns.OWNERID, Columns.IDENTIFIER, Columns.HASH, Columns.DISABLED, Columns.CREATED, Columns.CLAIMINGRULE,
					Columns.TAGIDHASH, Columns.SHORTCODE }, Columns.HASH + "=?", new String[] { hash }, null, null, null);
			if (cursor.moveToFirst()) {
				tag = new Tag();
				tag.setType(cursor.getString(0));
				tag.setOwnerId(cursor.getString(1));
				tag.setIdentifier(cursor.getString(2));
				tag.setHash(cursor.getString(3));
				tag.setDisabled(Boolean.valueOf(cursor.getString(4)));
				tag.setCreated(cursor.getString(5));
				tag.setClaimingRule(ClaimingRule.valueOf(cursor.getString(6)));
				tag.setTagIdHash(cursor.getString(7));
				tag.setShortcode(cursor.getString(8));
			}
			cursor.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (db != null) {
				db.close();
			}
		}
		return tag;
	}

	public List<Tag> findAll() {
		List<Tag> tags = new ArrayList<Tag>();
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.TYPE, Columns.OWNERID, Columns.IDENTIFIER, Columns.HASH, Columns.DISABLED, Columns.CREATED, Columns.CLAIMINGRULE,
					Columns.TAGIDHASH, Columns.SHORTCODE }, null, null, null, null, null);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Tag tag = new Tag();
					tag.setType(cursor.getString(0));
					tag.setOwnerId(cursor.getString(1));
					tag.setIdentifier(cursor.getString(2));
					tag.setHash(cursor.getString(3));
					tag.setDisabled(Boolean.valueOf(cursor.getString(4)));
					tag.setCreated(cursor.getString(5));
					tag.setClaimingRule(ClaimingRule.valueOf(cursor.getString(6)));
					tag.setTagIdHash(cursor.getString(7));
					tag.setShortcode(cursor.getString(8));
					tags.add(tag);
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

		return tags;
	}

	private static class Columns implements BaseColumns {

		private static final String TYPE = "type";

		private static final String OWNERID = "ownerId";

		private static final String IDENTIFIER = "identifier";

		private static final String HASH = "hash";

		private static final String DISABLED = "disabled";

		private static final String CREATED = "created";

		private static final String CLAIMINGRULE = "claimingRule";

		private static final String TAGIDHASH = "tagIdHash";

		private static final String SHORTCODE = "shortcode";

	}

	private class DbOpenHelper extends SQLiteOpenHelper {

		DbOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + //
					Columns._ID + " INTEGER PRIMARY KEY," + //
					Columns.TYPE + " TEXT," + //
					Columns.OWNERID + " TEXT," + //
					Columns.IDENTIFIER + " TEXT UNIQUE," + //
					Columns.HASH + " TEXT," + //
					Columns.DISABLED + " TEXT," + //
					Columns.CREATED + " TEXT," + //
					Columns.CLAIMINGRULE + " TEXT," + //
					Columns.TAGIDHASH + " TEXT," + //
					Columns.SHORTCODE + " TEXT);");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);

		}

	}
}
