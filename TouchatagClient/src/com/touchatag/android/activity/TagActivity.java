package com.touchatag.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.touchatag.android.R;
import com.touchatag.android.activity.template.TouchatagTemplate;
import com.touchatag.android.client.TouchatagRestClient;
import com.touchatag.android.client.rest.model.Application;
import com.touchatag.android.client.rest.model.Association;
import com.touchatag.android.client.rest.model.ClaimingRule;
import com.touchatag.android.client.rest.model.Tag;
import com.touchatag.android.store.ApplicationStore;
import com.touchatag.android.store.AssociationStore;
import com.touchatag.android.store.SettingsStore;
import com.touchatag.android.store.TagStore;
import com.touchatag.android.util.NotificationUtils;
import com.touchatag.android.util.QrCodeGenerator;

public class TagActivity extends Activity {

	public static final String ACTION_VIEW_TAG_DETAILS = "com.touchatag.action.VIEW_TAG_DETAILS";
	public static final Uri URI_TAG_DETAILS = Uri.parse("touchatag://tagdetails");
	public static final String EXTRA_TAGHASH = "taghash";

	private static final int DIALOG_RELEASE = 1;
	private static final int DIALOG_UNLINK = 2;

	private SettingsStore settingsStore;
	private TagStore tagStore;
	private ApplicationStore appStore;
	private AssociationStore assStore;
	private TouchatagRestClient client;

	private TextView lblIdentifier;
	private ImageView imgType;
	private Button btnUnlink;
	private Button btnDelete;
	private ToggleButton btnToggleLock;
	private ToggleButton btnToggleDisable;
	private Drawable imgTypeRFID;
	private Drawable imgTypeQR;

	private Tag tag;
	private Association asso;

	public static Intent getTagDetailIntent(Context ctx, Tag tag) {
		Intent intent = new Intent(ctx, TagActivity.class);
		intent.putExtra(EXTRA_TAGHASH, tag.getHash());
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settingsStore = new SettingsStore(this);
		tagStore = new TagStore(this);
		appStore = new ApplicationStore(this);
		assStore = new AssociationStore(this);
		setContentView(R.layout.tag_detail);

		lblIdentifier = (TextView) findViewById(R.id.lbl_tag_identifier);
		imgType = (ImageView) findViewById(R.id.img_tag_type);
		btnUnlink = (Button) findViewById(R.id.btn_tag_unlink);
		btnUnlink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				unlink();
			}
		});
		btnDelete = (Button) findViewById(R.id.btn_tag_delete);
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				releaseTag();
			}
		});

		btnToggleLock = (ToggleButton) findViewById(R.id.btn_tag_togglelock);
		btnToggleLock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleLock();
			}
		});
		
		btnToggleDisable = (ToggleButton) findViewById(R.id.btn_tag_toggledisable);
		btnToggleDisable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleDisable();
			}
		});
		
		Button btnViewQr = (Button) findViewById(R.id.btn_tag_detail_viewqr);
		btnViewQr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewQrCode();
			}
		});
		
		Button btnShareQr = (Button) findViewById(R.id.btn_tag_detail_shareqr);
		btnShareQr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareQrCode();
			}
		});

		Resources resources = getResources();
		imgTypeRFID = resources.getDrawable(R.drawable.ttlogo_48);
		imgTypeQR = resources.getDrawable(R.drawable.qr_small);

		setTitle("Touchatag - Tag Details");
		String tagHash = getIntent().getStringExtra(EXTRA_TAGHASH);

		setTag(tagStore.findByHash(tagHash));
		setAssociation( assStore.findByTagHash(tagHash));
	}

	private void setTag(Tag tag) {
		this.tag = tag;
		if (tag.getType().equals("RFID")) {
			imgType.setImageDrawable(imgTypeRFID);
			View viewQrActions = (View)findViewById(R.id.layout_tag_detail_qractions);
			viewQrActions.setVisibility(View.GONE);
		} else {
			imgType.setImageDrawable(imgTypeQR);
		}
		lblIdentifier.setText(tag.getIdentifier());
		
		btnToggleLock.setChecked(tag.getClaimingRule() == ClaimingRule.LOCKED);
		btnToggleDisable.setChecked(!tag.isDisabled());
	}

	private void setAssociation(Association asso) {
		if(asso != null){
			this.asso = asso;
			String appId = asso.getAppId();
			if (appId != null) {
				Application app = appStore.findByIdentifier(appId);
				if (app != null) {
					TextView lblAppName = (TextView) findViewById(R.id.lbl_tag_app_name);
					ImageView imgAppIcon = (ImageView) findViewById(R.id.img_tag_app_icon);
					lblAppName.setText(app.getName());
					TouchatagTemplate template = TouchatagTemplate.getTemplateFromApplication(app);
					if (template != null) {
						Drawable drawTemplateIcon = template.getIcon(getPackageManager());
						if (drawTemplateIcon != null) {
							imgAppIcon.setImageDrawable(drawTemplateIcon);
						}
					} else {
						lblAppName.setText("Unknown Application");
					}
				}
			}
		} else {
			ViewGroup layoutAppHeader = (ViewGroup) findViewById(R.id.layout_tag_detail_app_header);
			ViewGroup layoutApp = (ViewGroup) findViewById(R.id.layout_tag_detail_app);
			layoutAppHeader.setVisibility(View.INVISIBLE);
			layoutApp.setVisibility(View.INVISIBLE);
		}
	}

	private void releaseTag() {
		showDialog(DIALOG_RELEASE);
	}

	private void toggleLock() {
		ClaimingRule rule = tag.getClaimingRule();
		ClaimingRule newRule = tag.getClaimingRule();
		if (rule == ClaimingRule.LOCKED) {
			newRule = ClaimingRule.UNLOCKED;
		} else if (rule == ClaimingRule.UNLOCKED) {
			newRule = ClaimingRule.LOCKED;
		}
		new SetClaimingRuleTagAsyncTask(newRule).execute(tag);
	}
	
	private void toggleDisable() {
		new ToggleDisableTagAsyncTask(!tag.isDisabled()).execute(tag);
	}

	private void viewQrCode(){
		startActivity(ViewQrCodeActivity.getViewQrTagIntent(this, tag));
	}
	
	private void shareQrCode(){
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/bmp");
		sharingIntent.putExtra(android.content.Intent.EXTRA_, QrCodeGenerator.getUri(tag.getIdentifier()).toString());
		startActivity(Intent.createChooser(sharingIntent,"Share using"));
	}
	
	
	private void onTagClaimingRuleChanged(Tag tag) {
		tagStore.store(tag);
		this.tag = tag;
	}
	
	private void onTagDisabledChanged(Tag tag) {
		tagStore.store(tag);
		this.tag = tag;
	}
	
	private void onTagReleased(Tag tag, boolean deleted) {
		if(deleted){
			tagStore.remove(tag);
			NotificationUtils.showFeedbackMessage(this, Html.fromHtml("Deleted tag <b>" + tag.getIdentifier() + "</b>"));
			finish();
		} else {
			NotificationUtils.showFeedbackMessage(this, "Failed to delete tag");
		}
	}


	private void unlink() {
		showDialog(DIALOG_UNLINK);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_RELEASE:
			String message = "Releases the tag from your control. You will have to reclaim the tag to use it again.";
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Release Tag") //
					.setIcon(android.R.drawable.ic_dialog_info) //
					.setMessage(message) //
					.setCancelable(false) //
					.setPositiveButton("Release", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							new ReleaseTagAsyncTask().execute(tag);
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			return builder.create();
		case DIALOG_UNLINK:
			message = "Removes the association between the tag and the application.";
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Unlink Tag") //
					.setIcon(android.R.drawable.ic_dialog_info) //
					.setMessage(message) //
					.setCancelable(false) //
					.setPositiveButton("Unlink", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	private class SetClaimingRuleTagAsyncTask extends AsyncTask<Tag, Void, Tag> {

		private ProgressDialog progressDialog;
		private ClaimingRule rule;

		public SetClaimingRuleTagAsyncTask(ClaimingRule rule) {
			this.rule = rule;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			String message = "";
			switch (rule) {
			case LOCKED:
				message = "Locking Tag...";
				break;
			case UNLOCKED:
				message = "Unlocking Tag...";
				break;
			}
			progressDialog = ProgressDialog.show(TagActivity.this, null, message);
		}

		@Override
		protected Tag doInBackground(Tag... params) {
			try {
				Tag tag = params[0];
				TouchatagRestClient restClient = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
				ClaimingRule newRule = restClient.setClaimingRule(tag.getHash(), rule);
				tag.setClaimingRule(newRule);
				return tag;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Tag tag) {
			super.onPostExecute(tag);
			progressDialog.dismiss();
			onTagClaimingRuleChanged(tag);
		}

	}
	
	private class ToggleDisableTagAsyncTask extends AsyncTask<Tag, Void, Tag> {

		private ProgressDialog progressDialog;
		private boolean disable;

		public ToggleDisableTagAsyncTask(boolean disable) {
			this.disable = disable;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			String message = "";
			if(disable){
				message = "Enabling Tag...";
			} else {
				message = "Disabling Tag...";
			}
			progressDialog = ProgressDialog.show(TagActivity.this, null, message);
		}

		@Override
		protected Tag doInBackground(Tag... params) {
			try {
				Tag tag = params[0];
				TouchatagRestClient restClient = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
				if(disable){
					restClient.disableTag(tag.getHash());
					tag.setDisabled(true);
				} else {
					restClient.enableTag(tag.getHash());
					tag.setDisabled(false);
				}
				return tag;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Tag tag) {
			super.onPostExecute(tag);
			progressDialog.dismiss();
			if(tag != null){
				onTagDisabledChanged(tag);
			} else {
				NotificationUtils.showFeedbackMessage(TagActivity.this, "Failed to " + (disable ? "disable" : "enable") + " tag");
			}
		}

	}
	
	private class ReleaseTagAsyncTask extends AsyncTask<Tag, Void, Boolean>{

		private ProgressDialog progressDialog;
		private Tag tag;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(TagActivity.this, null, "Releasing Tag...");
		}

		@Override
		protected Boolean doInBackground(Tag... params) {
			try {
				Tag tag = params[0];
				this.tag = tag;
				TouchatagRestClient restClient = TouchatagRestClient.create(settingsStore.getServer(), settingsStore.getAccessToken(), settingsStore.getAccessTokenSecret());
				return restClient.deleteTag(tag.getHash());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean deleted) {
			super.onPostExecute(deleted);
			progressDialog.dismiss();
			onTagReleased(tag, deleted);
		}
		
	}

}
