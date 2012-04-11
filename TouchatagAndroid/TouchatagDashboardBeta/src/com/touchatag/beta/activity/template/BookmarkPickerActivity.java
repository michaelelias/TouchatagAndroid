package com.touchatag.beta.activity.template;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.touchatag.beta.R;

public class BookmarkPickerActivity extends Activity {

	public static final String EXTRA_BOOKMARK_URL = "bookmark.url";
	
	private List<BookmarkPickerActivity.Bookmark> bookmarks = new ArrayList<BookmarkPickerActivity.Bookmark>();
	private ListView listBookmarks;
	private BookmarkListAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Touchatag - Pick a bookmark");
		setContentView(R.layout.bookmark_picker);
		listBookmarks = (ListView) findViewById(R.id.list_bookmarks);
		
		listBookmarks.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bookmark bookmark = bookmarks.get(position);
				Intent intent = new Intent();
				intent.putExtra(EXTRA_BOOKMARK_URL, bookmark.getUrl());
				setResult(RESULT_OK, intent);
				finish();
			}
			
		});
		
		loadBookmarks();
		listAdapter = new BookmarkListAdapter(this);
		listBookmarks.setAdapter(listAdapter);
		
		
	}

	private void loadBookmarks() {
		Cursor cursor = getContentResolver().query(Browser.BOOKMARKS_URI, new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL }, null, null, null);
		while (cursor.moveToNext()) {
			Bookmark bookmark = new Bookmark();
			bookmark.setTitle(cursor.getString(0));
			bookmark.setUrl(cursor.getString(1));
			bookmarks.add(bookmark);
		}
		cursor.close();
	}

	private class BookmarkListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		public BookmarkListAdapter(Context ctx) {
			layoutInflater = LayoutInflater.from(ctx);
		}
		@Override
		public int getCount() {
			return bookmarks.size();
		}

		@Override
		public Object getItem(int position) {
			return bookmarks.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.bookmark_item, null);
			}
			Bookmark bookmark = bookmarks.get(position);
			
			TextView lblTitle = (TextView)convertView.findViewById(R.id.lbl_bookmark_name);
			lblTitle.setText(bookmark.getTitle());
			TextView lblUrl = (TextView)convertView.findViewById(R.id.lbl_bookmark_url);
			lblUrl.setText(bookmark.getUrl());
			return convertView;
		}

	}

	private class Bookmark {

		private String title;
		private String url;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((title == null) ? 0 : title.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Bookmark other = (Bookmark) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (title == null) {
				if (other.title != null)
					return false;
			} else if (!title.equals(other.title))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			return true;
		}

		private BookmarkPickerActivity getOuterType() {
			return BookmarkPickerActivity.this;
		}
		
		

	}

}
