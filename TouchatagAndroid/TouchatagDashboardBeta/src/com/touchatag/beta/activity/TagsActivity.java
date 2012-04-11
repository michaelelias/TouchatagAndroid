package com.touchatag.beta.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.touchatag.acs.api.client.AcsApiException;
import com.touchatag.acs.api.client.CorrelationDefinitionApiClient;
import com.touchatag.acs.api.client.NoInternetException;
import com.touchatag.acs.api.client.TagApiClient;
import com.touchatag.acs.api.client.UnexpectedHttpResponseCodeException;
import com.touchatag.acs.api.client.model.Tag;
import com.touchatag.acs.api.client.model.TagPage;
import com.touchatag.acs.api.client.model.TagType;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;
import com.touchatag.beta.R;
import com.touchatag.beta.TouchatagApplication;
import com.touchatag.beta.activity.common.AcsApiAsyncTask;
import com.touchatag.beta.client.AcsApiClientFactory;
import com.touchatag.beta.store.AssociationStore;
import com.touchatag.beta.store.SettingsStore;
import com.touchatag.beta.store.TagStore;
import com.touchatag.beta.util.NotificationUtils;

public class TagsActivity extends Activity {

	private static final int DIALOG_ABOUT = 101;
	
	private static final String EXTRA_PICKTAG = "picktag";
	public static final String EXTRA_TAG_IDENTIFIER = "tag.identifier";
	private static final int REQ_CODE_CLAIM_TAG = 100;

	private ListView listView;
	private TagListAdapter listAdapter;
	private ViewGroup layoutSplash;

	private SettingsStore settingsStore;
	private TagStore tagStore;
	private AssociationStore assStore;

	private List<Tag> listTags = new ArrayList<Tag>();
	private CorrelationDefinition corrDef;

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

		listAdapter = new TagListAdapter(this);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Tag tag = listTags.get(position);
				selectTag(tag);
			}

		});

		Resources resources = getResources();
		imgTypeRFID = resources.getDrawable(R.drawable.ttlogo_48);
		imgTypeQR = resources.getDrawable(R.drawable.qr_small);
		
		layoutSplash = (ViewGroup)findViewById(R.id.layout_tags_splash);
		Button btnClaimTag = (Button)findViewById(R.id.btn_tags_create);
		Button btnRefresh = (Button)findViewById(R.id.btn_tags_refresh);
		
		btnClaimTag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				acquireTag();
			}
		});
		
		btnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refreshTags();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onStart();
		if (getIntent().getExtras() != null && EXTRA_PICKTAG.equals(getIntent().getExtras().get(EXTRA_PICKTAG))) {
			pickTagMode = true;
			setTitle("Touchatag - Pick a Tag");
		} else {
			pickTagMode = false;
		}
		fetchTags();
//		if ((listTags.size() == 0 && !TouchatagApplication.tagsLoaded) || forceRefresh) {
//			loadTags();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tags_menu, menu);
		return true;
	}

	private void refreshTags(){
		loadTags();
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_tags_refresh:
			refreshTags();
			return true;
		case R.id.menuitem_tags_claimtag:
			acquireTag();
			return true;
//		case R.id.menuitem_tags_generateqr:
//			generateQrCode();
//			return true;
		case R.id.menuitem_about:
			showDialog(DIALOG_ABOUT);
			return true;
		case R.id.menuitem_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ABOUT:
			return ActivityUtils.getAboutDialog(this);
		}
		return super.onCreateDialog(id);
	}

	private void fetchTags() {
		listTags = tagStore.findAll(settingsStore.getIdentityId());
		setSplashScreenVisible(listTags.size() == 0);
		corrDef = assStore.getCorrelationDefinition();
		for (Tag tag : listTags) {
			tag.setLinked(corrDef.getAssociatedAppIdForTag(tag.getHash()) != null);
		}
		listAdapter.notifyDataSetChanged();
	}

	private void loadTags() {
		new LoadTagsAsyncTask("Loading Tags...", "Failed to load tags.", this).execute();
	}
	
	private void setSplashScreenVisible(boolean visible){
		layoutSplash.setVisibility(visible ? View.VISIBLE : View.GONE);
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

	private void generateQrCode() {
		new GenerateQrCodeAsyncTask("Generating QR Code...", "Failed to generate QR code.", this).execute();
	}

	private void onQrTagGenerated(Tag tag) {
		tagStore.store(tag);
		listTags.add(tag);
		listAdapter.notifyDataSetChanged();
	}

	private void onTagsLoaded(TagPage pageTags) {
		mergeToStore(pageTags);
		listTags = tagStore.findAll(settingsStore.getIdentityId());
		setSplashScreenVisible(listTags.size() == 0);
		listAdapter.notifyDataSetChanged();
		TouchatagApplication.tagsLoaded = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_CLAIM_TAG && resultCode == RESULT_OK) {
			String tagIdentifier = data.getExtras().getString(TagsActivity.EXTRA_TAG_IDENTIFIER);
			Tag tag = tagStore.findByIdentifier(tagIdentifier);
			selectTag(tag);
		}
	}

	public static Intent getPickTagIntent(Context ctx) {
		Intent intent = new Intent(ctx, TagsActivity.class);
		intent.putExtra(EXTRA_PICKTAG, EXTRA_PICKTAG);
		return intent;
	}

	private boolean mergeToStore(TagPage tagPage) {
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
		List<Tag> staleTags = tagStore.findByIdentifierNotIn(identifiers);
		for (Tag staleTag : staleTags) {
			tagStore.remove(staleTag);
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
			return listTags.size();
		}

		@Override
		public Object getItem(int position) {
			return listTags.get(position);
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

			Tag tag = listTags.get(position);

			ImageView imgType = (ImageView) convertView.findViewById(R.id.img_tag_item_type);
			if (tag.getType() == TagType.RFID) {
				imgType.setImageDrawable(imgTypeRFID);
			} else {
				imgType.setImageDrawable(imgTypeQR);
			}

			TextView txtTagUID = (TextView) convertView.findViewById(R.id.lbl_tag_item_taguid);
			txtTagUID.setText(tag.getIdentifier());

			ImageView imgDisabled = (ImageView) convertView.findViewById(R.id.img_tag_item_disabled);
			ImageView imgLinked = (ImageView) convertView.findViewById(R.id.img_tag_item_linked);
			ImageView imgRule = (ImageView) convertView.findViewById(R.id.img_tag_item_rule);

			imgDisabled.setVisibility(tag.isDisabled() ? View.VISIBLE : View.GONE);
			imgLinked.setVisibility(tag.isLinked() ? View.VISIBLE : View.GONE);

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

	private class LoadTagsAsyncTask extends AcsApiAsyncTask<Void, TagPage> {

		public LoadTagsAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		@Override
		public TagPage doApiCall(Void... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			TagApiClient tagApi = AcsApiClientFactory.createTagApiClient(settingsStore);
			CorrelationDefinitionApiClient correlationDefinitionApi = AcsApiClientFactory.createCorrelationDefinitionApiClient(settingsStore);
			TagPage tags = tagApi.getPage(1, 25);
			corrDef = correlationDefinitionApi.get();
			assStore.update(corrDef);
			return tags;
		}

		@Override
		public void processOutput(TagPage pageTags) {
			onTagsLoaded(pageTags);
		}

	}

	private class GenerateQrCodeAsyncTask extends AcsApiAsyncTask<Void, Tag> {

		public GenerateQrCodeAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		@Override
		public Tag doApiCall(Void... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			TagApiClient tagApi = AcsApiClientFactory.createTagApiClient(settingsStore);
			return tagApi.generateQRTag();
		}

		@Override
		public void processOutput(Tag tag) {
			if (tag != null) {
				onQrTagGenerated(tag);
			} else {
				NotificationUtils.showFeedbackMessage(TagsActivity.this, "Failed to generate a QR tag, try again");
			}
		}

	}

}
