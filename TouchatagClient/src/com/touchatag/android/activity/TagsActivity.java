package com.touchatag.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.touchatag.android.R;
import com.touchatag.android.TouchatagApplication;
import com.touchatag.android.client.TouchatagRestClient;
import com.touchatag.android.client.rest.model.AcsApiException;
import com.touchatag.android.client.rest.model.CorrelationDefinition;
import com.touchatag.android.client.rest.model.Page;
import com.touchatag.android.client.rest.model.Tag;
import com.touchatag.android.store.AssociationStore;
import com.touchatag.android.store.SettingsStore;
import com.touchatag.android.store.TagStore;
import com.touchatag.android.util.NotificationUtils;

public class TagsActivity extends Activity {

	private static final String EXTRA_PICKTAG = "picktag";
	public static final String EXTRA_TAG_IDENTIFIER = "tag.identifier";
	private static final int REQ_CODE_CLAIM_TAG = 100;

	private static boolean forceRefresh = false;

	private ListView listView;
	private TagListAdapter listAdapter;

	private SettingsStore settingsStore;
	private TagStore tagStore;
	private AssociationStore assStore;

	private Page<Tag> pageTags = new Page<Tag>();

	private Drawable imgTypeRFID;
	private Drawable imgTypeQR;

	private boolean pickTagMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tags);

		listView = (ListView) findViewById(R.id.layout_tags_list);

		settingsStore = new SettingsStore(this);
		assStore = new AssociationStore(this);
		tagStore = new TagStore(this);
		fetchTags();

		listAdapter = new TagListAdapter(this);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Tag tag = pageTags.getItems().get(position);
				selectTag(tag);
			}

		});

		Resources resources = getResources();
		imgTypeRFID = resources.getDrawable(R.drawable.ttlogo_48);
		imgTypeQR = resources.getDrawable(R.drawable.qr_small);
	}

	@Override
	protected void onResume() {
		super.onStart();
		if (getIntent().getExtras() != null && EXTRA_PICKTAG.equals(getIntent().getExtras().get(EXTRA_PICKTAG))) {
			pickTagMode = true;
			setTitle("Touchatag - Pick a Tag");
		} else {
			pickTagMode = false;
			if (settingsStore.isAuthorized()){ 
				if(!TouchatagApplication.tagsLoaded) {
					loadTags();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tags_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_tags_refresh:
			forceRefresh = true;
			loadTags();
			return true;
		case R.id.menuitem_tags_claimtag:
			acquireTag();
			return true;
		case R.id.menuitem_tags_generateqr:
			generateQrCode();
			return true;
		case R.id.menuitem_tags_search:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void fetchTags() {
		pageTags = tagStore.getPage(1, 25);
		for (Tag tag : pageTags.getItems()) {
			tag.setLinked(assStore.findByTagHash(tag.getHash()) != null);
		}
	}

	private void loadTags() {
		if (settingsStore.isAuthorized()) {
			fetchTags();
			if ((pageTags.getItems().size() == 0 && !TouchatagApplication.tagsLoaded) || forceRefresh) {
				LoadTagsAsyncTask task = new LoadTagsAsyncTask();
				task.execute();
				forceRefresh = false;
			} else {
				listAdapter.notifyDataSetChanged();
			}
		}
	}

	private void selectTag(Tag tag) {
		if (pickTagMode) {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_TAG_IDENTIFIER, tag.getIdentifier());
			setResult(RESULT_OK, intent);
			finish();
		} else {
			startActivity(TagActivity.getTagDetailIntent(TagsActivity.this, tag));
		}
	}

	private void acquireTag() {
		Intent intent = new Intent(this, ClaimTagActivity.class);
		startActivityForResult(intent, REQ_CODE_CLAIM_TAG);
	}
	
	private void generateQrCode(){
		new GenerateQrCodeAsyncTask().execute();
	}
	
	private void onQrTagGenerated(Tag tag){
		tagStore.store(tag);
		pageTags.getItems().add(tag);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_CLAIM_TAG && resultCode == RESULT_OK) {
			String tagIdentifier = data.getExtras().getString(TagsActivity.EXTRA_TAG_IDENTIFIER);
			Tag tag = tagStore.findByIdentifier(tagIdentifier);
			startActivity(TagActivity.getTagDetailIntent(this, tag));
		}
	}

	public static Intent getPickTagIntent(Context ctx) {
		Intent intent = new Intent(ctx, TagsActivity.class);
		intent.putExtra(EXTRA_PICKTAG, EXTRA_PICKTAG);
		return intent;
	}

	private boolean mergeToStore(Page<Tag> tagPage) {
		boolean mergeNeeded = false;
		List<String> identifiers = new ArrayList<String>();
		Map<String, Tag> tags = new HashMap<String, Tag>();
		for (Tag tag : tagPage.getItems()) {
			identifiers.add(tag.getIdentifier());
			tags.put(tag.getIdentifier(), tag);
		}
		Map<String, Boolean> existingTags = tagStore.exists(identifiers);
		for (Entry<String, Boolean> entry : existingTags.entrySet()) {
			if (!entry.getValue()) {
				mergeNeeded = true;
				tagStore.store(tags.get(entry.getKey()));
			}
		}
		return mergeNeeded;
	}

	private class TagListAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;
		private Drawable imgLocked;
		private Drawable imgUnlocked;

		public TagListAdapter(Context ctx) {
			layoutInflater = LayoutInflater.from(ctx);
			imgLocked = ctx.getResources().getDrawable(R.drawable.locked_16_glow);
			imgUnlocked = ctx.getResources().getDrawable(R.drawable.unlocked_16_glow);
		}

		@Override
		public int getCount() {
			return pageTags.getItems().size();
		}

		@Override
		public Object getItem(int position) {
			return pageTags.getItems().get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.tag_item, null);
			}

			Tag tag = pageTags.getItems().get(position);

			ImageView imgType = (ImageView) convertView.findViewById(R.id.img_tag_item_type);
			if (tag.getType().equalsIgnoreCase("RFID")) {
				imgType.setImageDrawable(imgTypeRFID);
			} else {
				imgType.setImageDrawable(imgTypeQR);
			}

			TextView txtTagUID = (TextView) convertView.findViewById(R.id.lbl_tag_item_taguid);
			txtTagUID.setText(tag.getIdentifier());

			ImageView imgDisabled = (ImageView) convertView.findViewById(R.id.img_tag_item_disabled);
			ImageView imgLinked = (ImageView) convertView.findViewById(R.id.img_tag_item_linked);
			ImageView imgRule = (ImageView) convertView.findViewById(R.id.img_tag_item_rule);

			imgDisabled.setVisibility(tag.isDisabled() ? View.VISIBLE : View.INVISIBLE);
			imgLinked.setVisibility(tag.isLinked() ? View.VISIBLE : View.INVISIBLE);

			switch (tag.getClaimingRule()) {
			case LOCKED:
				imgRule.setImageDrawable(imgLocked);
				break;
			case UNLOCKED:
				imgRule.setImageDrawable(imgUnlocked);
				break;
			}

			return convertView;
		}
	}

	private class LoadTagsAsyncTask extends AsyncTask<Void, Void, Page<Tag>> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(TagsActivity.this, null, "Loading Tags...");
		}

		@Override
		protected Page<Tag> doInBackground(Void... params) {
			TouchatagRestClient client = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
			Page<Tag> tags = client.getTags(1, 25);
			CorrelationDefinition corrDef = client.getCorrelationDefinition();
			assStore.update(corrDef);
			return tags;
		}

		@Override
		protected void onPostExecute(Page<Tag> tags) {
			super.onPostExecute(tags);
			if (mergeToStore(tags)) {
				fetchTags();
			}
			progressDialog.dismiss();
			listAdapter.notifyDataSetChanged();
			TouchatagApplication.tagsLoaded = true;
		}

	}
	
	private class GenerateQrCodeAsyncTask extends AsyncTask<Void, Void, Tag>{

		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(TagsActivity.this, null, "Generating QR Tag...");
		}

		@Override
		protected Tag doInBackground(Void... params) {
			TouchatagRestClient client = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
			try {
				return client.generateQrTag();
			} catch (AcsApiException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Tag tag) {
			super.onPostExecute(tag);
			progressDialog.dismiss();
			if(tag != null){
				onQrTagGenerated(tag);
			} else {
				NotificationUtils.showFeedbackMessage(TagsActivity.this, "Failed to generate a QR tag, try again");
			}
		}
		
	}

}
