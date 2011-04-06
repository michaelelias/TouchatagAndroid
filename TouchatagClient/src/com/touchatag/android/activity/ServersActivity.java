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
import com.touchatag.android.store.Server;
import com.touchatag.android.store.ServerStore;
import com.touchatag.android.store.SettingsStore;

public class ServersActivity extends Activity {

	private ListView listView;
	private SettingsStore settingsStore;
	private ServerStore serverStore;
	private List<Server> servers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		//setContentView(R.layout.servers);
		
		settingsStore = new SettingsStore(this);
		serverStore = new ServerStore(this);
		
		servers = serverStore.findAll();
		//listView = (ListView)findViewById(R.id.layout_servers_list);
		listView.setAdapter(new ServerListAdapter(this));
	}
	
	private class ServerListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;
		
		public ServerListAdapter(Context ctx){
			layoutInflater = LayoutInflater.from(ctx);
		}
		
		@Override
		public int getCount() {
			return servers.size();
		}

		@Override
		public Object getItem(int position) {
			return servers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.server_item, null);
			}
			
			Server server = servers.get(position);
			
			TextView txtName = (TextView)convertView.findViewById(R.id.lbl_serveritem_name);
			txtName.setText(server.getName());
			
			TextView txtUrl = (TextView)convertView.findViewById(R.id.lbl_serveritem_url);
			txtUrl.setText(server.getUrl());
			
			return convertView;
		}
	}
}
