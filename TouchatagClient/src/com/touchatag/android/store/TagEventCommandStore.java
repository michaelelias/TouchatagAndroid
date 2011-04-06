package com.touchatag.android.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.touchatag.android.client.soap.command.TagEventCommand;

public class TagEventCommandStore {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "db.touchatag.tageventcommand";
	private static final String TABLE_NAME = "tageventcommand";

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private DbOpenHelper dbOpenHelper;

	public TagEventCommandStore(Context ctx) {
		dbOpenHelper = new DbOpenHelper(ctx);
	}

	public void store(TagEventCommand command) {
		SQLiteDatabase db = null;
		try {
			ContentValues values = new ContentValues();

			values.put(Columns.TAGUID, command.getRequest().getActionTag().getTagId().getIdentifier());
			values.put(Columns.CREATED, dateFormat.format(new Date(command.getTimeRequestSent())));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(command);

			values.put(Columns.COMMAND, baos.toByteArray());

			db = dbOpenHelper.getWritableDatabase();

			db.insert(TABLE_NAME, Columns.COMMAND, values);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(db != null){
				db.close();
			}
		}
	}

	public List<TagEventCommand> findAll() {
		List<TagEventCommand> commands = new ArrayList<TagEventCommand>();
		SQLiteDatabase db = null;
		try {
			db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, new String[] { Columns.COMMAND }, null, null, null, null, Columns.CREATED);
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					ByteArrayInputStream bais = new ByteArrayInputStream(cursor.getBlob(0));
					ObjectInputStream ois = new ObjectInputStream(bais);
					Object obj = ois.readObject();
					if (obj instanceof TagEventCommand) {
						commands.add((TagEventCommand) obj);
					}

					cursor.moveToNext();
				}
			}
			cursor.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(db != null){
				db.close();
			}
		}

		return commands;
	}

	private static class Columns implements BaseColumns {

		private static final String CREATED = "created";

		private static final String TAGUID = "taguid";

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
					Columns.TAGUID + " TEXT," + //
					Columns.CREATED + " TEXT," + //
					Columns.COMMAND + " BLOB" + ");");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);

		}

	}
}
