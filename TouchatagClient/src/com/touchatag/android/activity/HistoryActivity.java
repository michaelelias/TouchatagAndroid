package com.touchatag.android.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.touchatag.android.R;
import com.touchatag.android.client.soap.command.TagEventCommand;
import com.touchatag.android.store.TagEventCommandStore;

public class HistoryActivity extends Activity {
	
	private TagEventCommandStore store;
	
	private ListView listView;
	
	private List<TagEventCommand> commands;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.history);
		
		store = new TagEventCommandStore(this);
		commands = store.findAll();
		
		listView = (ListView)findViewById(R.id.layout_history_list);
		
		listView.setAdapter(new TagEventCommandListAdapter(this));
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
	}

	
	private class TagEventCommandListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;
		
		public TagEventCommandListAdapter(Context ctx){
			layoutInflater = LayoutInflater.from(ctx);
		}
		
		@Override
		public int getCount() {
			return commands.size();
		}

		@Override
		public Object getItem(int position) {
			return commands.get(position);
		}

		@Override
		public long getItemId(int position) {
			return commands.get(position).getTimeRequestSent();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				//convertView = layoutInflater.inflate(R.layout.history_item, null);
			}
//			TextView txt = (TextView)convertView.findViewById(R.id.lbl_history_item_title);
//			txt.setText(commands.get(position).getRequest().getActionTag().getTagId().getIdentifier());
//			
			return convertView;
		}
	}
}
