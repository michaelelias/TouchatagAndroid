package com.touchatag.beta.activity;

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

import com.touchatag.acs.api.client.CorrelationDefinitionApiClient;
import com.touchatag.acs.api.client.TagApiClient;
import com.touchatag.acs.api.client.model.Application;
import com.touchatag.acs.api.client.model.ClaimingRule;
import com.touchatag.acs.api.client.model.Tag;
import com.touchatag.acs.api.client.model.ruleset.Association;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;
import com.touchatag.beta.R;
import com.touchatag.beta.activity.template.TouchatagTemplate;
import com.touchatag.beta.client.AcsApiClientFactory;
import com.touchatag.beta.store.ApplicationStore;
import com.touchatag.beta.store.AssociationStore;
import com.touchatag.beta.store.SettingsStore;
import com.touchatag.beta.store.TagStore;
import com.touchatag.beta.util.NotificationUtils;
import com.touchatag.beta.util.QrCodeGenerator;

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

	private TextView lblIdentifier;
	private ImageView imgType;
	private Button btnUnlink;
	private Button btnDelete;
	private Button btnExecute;
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

		btnExecute = (Button) findViewById(R.id.btn_tag_execute);
		btnExecute.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				executeTag();
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

		Button btnViewQr = (Button) findViewById(R.id.btn_tag_viewqr);
		btnViewQr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewQrCode();
			}
		});
		
		Button btnShareQr = (Button) findViewById(R.id.btn_tag_shareqr);
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
		setAssociation(assStore.findByTagHash(tagHash));
	}

	private void setTag(Tag tag) {
		this.tag = tag;
		if (tag.getType().equals("RFID")) {
			imgType.setImageDrawable(imgTypeRFID);
		} else {
			imgType.setImageDrawable(imgTypeQR);
		}
		lblIdentifier.setText(tag.getIdentifier());

		btnToggleLock.setChecked(tag.getClaimingRule() == ClaimingRule.LOCKED);
		btnToggleDisable.setChecked(!tag.isDisabled());
	}

	private void setAssociation(Association asso) {
		if (asso != null) {
			this.asso = asso;
			String appId = asso.getAppId();
			if (appId != null) {
				Application app = appStore.findByIdentifier(appId);
				if (app != null) {
					TextView lblAppName = (TextView) findViewById(R.id.lbl_tag_app_name);
					TextView lblAppDescription = (TextView) findViewById(R.id.lbl_tag_app_description);
					ImageView imgAppIcon = (ImageView) findViewById(R.id.img_tag_app_icon);
					lblAppName.setText(app.getName());
					lblAppDescription.setText(app.getDescription());
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

	private void executeTag() {
		String identifier = tag.getIdentifier();
		identifier = identifier.substring(2);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ttag.be/m/" + identifier));
		startActivity(intent);
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

	private void viewQrCode() {
		startActivity(ViewQrCodeActivity.getViewQrTagIntent(this, tag));
	}

	private void shareQrCode() {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/bmp");

		Uri uri = QrCodeGenerator.getCachedBitmapUri(this, tag.getIdentifier());

		sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(sharingIntent, "Share using"));
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
		if (deleted) {
			tagStore.remove(tag);
			NotificationUtils.showFeedbackMessage(this, Html.fromHtml("Deleted tag <b>" + tag.getIdentifier() + "</b>"));
			finish();
		} else {
			NotificationUtils.showFeedbackMessage(this, "Failed to delete tag");
		}
	}
	
	private void onTagUnlinked(Tag tag, boolean updated) {
		if (updated) {
			Application app = appStore.findByIdentifier(asso.getAppId());
			NotificationUtils.showFeedbackMessage(this, Html.fromHtml("Unlinked tag <b>" + tag.getIdentifier() + "</b> from app <b>" + app.getName() + "</b>"));
			setAssociation(null);
		} else {
			NotificationUtils.showFeedbackMessage(this, "No unlinking needed");
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
							new UnlinkTagAsyncTask().execute(tag);
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
				TagApiClient tagApi = AcsApiClientFactory.createTagApiClient(settingsStore);
				ClaimingRule newRule = tagApi.setClaimingRule(tag.getHash(), rule);
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
			if (disable) {
				message = "Disabling Tag...";
			} else {
				message = "Enabling Tag...";
			}
			progressDialog = ProgressDialog.show(TagActivity.this, null, message);
		}

		@Override
		protected Tag doInBackground(Tag... params) {
			try {
				Tag tag = params[0];
				TagApiClient tagApi = AcsApiClientFactory.createTagApiClient(settingsStore);
				if (disable) {
					tagApi.disable(tag.getHash());
					tag.setDisabled(true);
				} else {
					tagApi.enable(tag.getHash());
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
			if (tag != null) {
				onTagDisabledChanged(tag);
			} else {
				NotificationUtils.showFeedbackMessage(TagActivity.this, "Failed to " + (disable ? "disable" : "enable") + " tag");
			}
		}

	}

	private class ReleaseTagAsyncTask extends AsyncTask<Tag, Void, Boolean> {

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
				TagApiClient tagApi = AcsApiClientFactory.createTagApiClient(settingsStore);
				CorrelationDefinitionApiClient correlationDefinitionApi = AcsApiClientFactory.createCorrelationDefinitionApiClient(settingsStore);
				boolean deleted = tagApi.relinquish(tag.getHash());
				if (deleted) {
					CorrelationDefinition corrDef = assStore.getCorrelationDefinition();
					if (corrDef.removeAssociationsByTagHash(tag.getHash())) {
						corrDef = correlationDefinitionApi.update(corrDef);
						assStore.update(corrDef);
					}
				}
				return deleted;
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

	private class UnlinkTagAsyncTask extends AsyncTask<Tag, Void, Boolean> {

		private ProgressDialog progressDialog;
		private Tag tag;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(TagActivity.this, null, "Unlinking Tag...");
		}

		@Override
		protected Boolean doInBackground(Tag... params) {
			try {
				Tag tag = params[0];
				this.tag = tag;
				CorrelationDefinitionApiClient correlationDefinitionApi = AcsApiClientFactory.createCorrelationDefinitionApiClient(settingsStore);
				CorrelationDefinition corrDef = assStore.getCorrelationDefinition();
				if (corrDef.removeAssociationsByTagHash(tag.getHash())) {
					corrDef = correlationDefinitionApi.update(corrDef);
					assStore.update(corrDef);
					return true;
				}
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean updated) {
			super.onPostExecute(updated);
			progressDialog.dismiss();
			onTagUnlinked(tag, updated);
		}

	}

}
