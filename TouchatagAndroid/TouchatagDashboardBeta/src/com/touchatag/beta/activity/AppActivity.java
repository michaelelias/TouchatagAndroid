package com.touchatag.beta.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touchatag.acs.api.client.AcsApiException;
import com.touchatag.acs.api.client.ApplicationApiClient;
import com.touchatag.acs.api.client.CorrelationDefinitionApiClient;
import com.touchatag.acs.api.client.NoInternetException;
import com.touchatag.acs.api.client.UnexpectedHttpResponseCodeException;
import com.touchatag.acs.api.client.model.Application;
import com.touchatag.acs.api.client.model.Tag;
import com.touchatag.acs.api.client.model.TagType;
import com.touchatag.acs.api.client.model.ruleset.Association;
import com.touchatag.acs.api.client.model.ruleset.CorrelationDefinition;
import com.touchatag.beta.R;
import com.touchatag.beta.activity.common.AcsApiAsyncTask;
import com.touchatag.beta.activity.template.TouchatagTemplate;
import com.touchatag.beta.client.AcsApiClientFactory;
import com.touchatag.beta.store.ApplicationStore;
import com.touchatag.beta.store.AssociationStore;
import com.touchatag.beta.store.SettingsStore;
import com.touchatag.beta.store.TagStore;
import com.touchatag.beta.util.NotificationUtils;

public class AppActivity extends Activity {

	private static final int DIALOG_SELECT_APP_TEMPLATE = 1;
	private static final int DIALOG_FILTERED_SELECT_APP_TEMPLATE = 2;
	private static final int REQ_CODE_PICKTAG = 1000;
	private static final int REQ_CODE_CLAIMTAG = 1001;
	private static final String EXTRA_APPLICATION_ID = "application.id";
	private static final int MODE_NEW = 10;
	private static final int MODE_EDIT = 11;

	private List<TouchatagTemplate> filteredTemplates = new ArrayList<TouchatagTemplate>();

	private SettingsStore settingsStore;
	private TagStore tagStore;
	private AssociationStore assStore;
	private ApplicationStore appStore;

	private EditText txtName;
	private Button btnAppTemplate;
	private Drawable imgNoAppTemplateSelected;
	private Drawable imgMore;
	private TouchatagTemplate selectedTemplate;
	private LinearLayout layoutTemplateConfig;
	private Button btnSave;
	private Button btnChooseTag;
	private LinearLayout layoutTags;
	private Map<Tag, View> mapTagViews = new HashMap<Tag, View>();
	private List<Tag> selectedTags = new ArrayList<Tag>();
	private Drawable imgTypeRFID;
	private Drawable imgTypeQR;
	private LayoutInflater layoutInflater;
	private int mode;
	private boolean processedActivityResult;

	public static Intent getNewAppIntent(Context ctx) {
		Intent intent = new Intent(ctx, AppActivity.class);
		return intent;
	}

	public static Intent getAppDetailIntent(Context ctx, Application app) {
		Intent intent = new Intent(ctx, AppActivity.class);
		intent.putExtra(EXTRA_APPLICATION_ID, app.getId());
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_detail);

		settingsStore = new SettingsStore(this);
		tagStore = new TagStore(this);
		assStore = new AssociationStore(this);
		appStore = new ApplicationStore(this);
		layoutInflater = LayoutInflater.from(this);

		txtName = (EditText) findViewById(R.id.txt_appdetail_name);

		btnAppTemplate = (Button) findViewById(R.id.btn_app_item_template);
		btnSave = (Button) findViewById(R.id.btn_appdetail_save);
		btnChooseTag = (Button) findViewById(R.id.btn_appdetail_picktag);
		Button btnClaimTag = (Button) findViewById(R.id.btn_appdetail_claimtag);
		layoutTags = (LinearLayout) findViewById(R.id.layout_appdetail_tags);

		Resources resources = getResources();
		imgTypeRFID = resources.getDrawable(R.drawable.ttlogo_48);
		imgTypeQR = resources.getDrawable(R.drawable.qr_small);
		imgNoAppTemplateSelected = resources.getDrawable(android.R.drawable.sym_def_app_icon);
		imgMore = resources.getDrawable(android.R.drawable.ic_menu_more);

		btnAppTemplate.setCompoundDrawablesWithIntrinsicBounds(imgNoAppTemplateSelected, null, imgMore, null);

		btnAppTemplate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_SELECT_APP_TEMPLATE);
			}
		});

		btnChooseTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(TagsActivity.getPickTagIntent(AppActivity.this), REQ_CODE_PICKTAG);
			}
		});

		btnClaimTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				claimTag();
			}
		});

		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveApplication();
			}
		});

		layoutTemplateConfig = (LinearLayout) findViewById(R.id.layout_appdetail_template_config);

		mode = MODE_NEW;
		setTitle("Touchatag - New Application");
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (Intent.ACTION_SEND.equals(intent.getAction())) {
			Bundle bundle = intent.getExtras();
			for (String key : bundle.keySet()) {
				Log.i("AppActivity-BundleFromSendIntent", key + " : " + bundle.getString(key));
			}
		} else if (extras != null && extras.containsKey(EXTRA_APPLICATION_ID)) {
			mode = MODE_EDIT;
			setTitle("Touchatag - Edit Application");
			Application app = extractApplicationFromIntent(intent);

			txtName.setText(app.getName());
			loadTags(app);
			LoadApplicationDetailsAsyncTask loadAppDetailTask = new LoadApplicationDetailsAsyncTask(app);
			loadAppDetailTask.execute();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = getIntent();
		if (Intent.ACTION_SEND.equals(intent.getAction())) {
			filteredTemplates = TouchatagTemplate.getTemplatesThatCanHandle(intent);
			if (filteredTemplates.size() > 1) {
				showDialog(DIALOG_FILTERED_SELECT_APP_TEMPLATE);
			} else {
				TouchatagTemplate template = filteredTemplates.get(0);
				setSelectedAppTemplate(template);
				template.initWithData(this, intent);
			}
		} else {
			filteredTemplates.clear();
		}
	}

	private void loadTags(Application app) {
		List<Association> associations = assStore.findByAppId(app.getId());
		List<Tag> tags = new ArrayList<Tag>();
		for (Association asso : associations) {
			Tag tag = tagStore.findByHash(asso.getTagId());
			if (tag == null) {
				//throw new RuntimeException("Unknown tag in associations, implement loading");
				continue;
			}
			tags.add(tag);
		}
		selectedTags = tags;
		for (Tag tag : selectedTags) {
			addTagView(tag);
		}
	}

	private void claimTag() {
		Intent intent = new Intent(this, ClaimTagActivity.class);
		startActivityForResult(intent, REQ_CODE_CLAIMTAG);
	}

	private Application extractApplicationFromIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String appIdentifier = extras.getString(EXTRA_APPLICATION_ID);
		return appStore.findByIdentifier(appIdentifier);
	}

	private boolean isInNewMode() {
		return mode == MODE_NEW;
	}

	private boolean isInEditMode() {
		return mode == MODE_EDIT;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		processedActivityResult = true;
		if (resultCode == RESULT_OK) {
			if (selectedTemplate != null && requestCode == selectedTemplate.getRequestCode()) {
				selectedTemplate.processActivityResult(this, data);
			} else if (requestCode == REQ_CODE_PICKTAG) {
				String tagIdentifier = data.getExtras().getString(TagsActivity.EXTRA_TAG_IDENTIFIER);
				Tag tag = tagStore.findByIdentifier(tagIdentifier);
				if (!selectedTags.contains(tag)) {
					selectedTags.add(tag);
					addTagView(tag);
				}
			} else if (requestCode == REQ_CODE_CLAIMTAG) {
				String tagIdentifier = data.getExtras().getString(ClaimTagActivity.EXTRA_TAG_IDENTIFIER);
				Tag tag = tagStore.findByIdentifier(tagIdentifier);
				if (!selectedTags.contains(tag)) {
					selectedTags.add(tag);
					addTagView(tag);
				}
			}
		}
	}

	private void addTagView(final Tag tag) {
		View view = layoutInflater.inflate(R.layout.tag_selected_item, null);
		view.setId(selectedTags.indexOf(tag));

		ImageView imgType = (ImageView) view.findViewById(R.id.img_tag_selected_item_type);
		if (tag.getType() == TagType.RFID) {
			imgType.setImageDrawable(imgTypeRFID);
		} else {
			imgType.setImageDrawable(imgTypeQR);
		}

		TextView txtTagUID = (TextView) view.findViewById(R.id.lbl_tag_selected_item_taguid);
		txtTagUID.setText(tag.getIdentifier());

		ImageButton btn = (ImageButton) view.findViewById(R.id.btn_tag_selected_remove);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectedTags.remove(tag);
				removeTagView(tag);
			}
		});
		mapTagViews.put(tag, view);
		layoutTags.addView(view);
	}

	private void removeTagView(final Tag tag) {
		View view = mapTagViews.get(tag);
		if (view != null) {
			layoutTags.removeView(view);
		}
		selectedTags.remove(tag);
	}

	private void setSelectedAppTemplate(TouchatagTemplate template) {
		selectedTemplate = template;
		btnAppTemplate.setCompoundDrawablesWithIntrinsicBounds(selectedTemplate.getIcon(getPackageManager()), null, imgMore, null);
		btnAppTemplate.setText(selectedTemplate.getName());

		ViewGroup viewGroup = template.getViewGroup(this);
		if (viewGroup != null) {
			layoutTemplateConfig.removeAllViews();
			layoutTemplateConfig.addView(viewGroup);
		}
	}

	private boolean validateForm() {
		if (txtName.getText().length() == 0) {
			NotificationUtils.showFeedbackMessage(this, "Provide a name for you application.");
			return false;
		}
		if (selectedTemplate == null) {
			NotificationUtils.showFeedbackMessage(this, "Select a template for your application.");
			return false;
		}
		if (!selectedTemplate.validateComponents(this)) {
			return false;
		}
		return true;
	}

	private void saveApplication() {
		if (validateForm()) {
			if (isInNewMode()) {
				CreateApplicationAsyncTask task = new CreateApplicationAsyncTask("Creating Application...", "Failed to create application.", this);
				Application app = new Application();
				app.setName(txtName.getText().toString());
				app.setDescription(selectedTemplate.generateDescription(this));
				app.setTemplate(selectedTemplate.name());
				app.setSpecification(selectedTemplate.createSpecification(this));
				task.execute(app);
			} else if (isInEditMode()) {
				UpdateApplicationAsyncTask task = new UpdateApplicationAsyncTask("Updating Application...", "Failed to update application.", this);
				Application app = extractApplicationFromIntent(getIntent());
				app.setName(txtName.getText().toString());
				app.setDescription(selectedTemplate.generateDescription(this));
				app.setTemplate(selectedTemplate.name());
				app.setSpecification(selectedTemplate.createSpecification(this));
				task.execute(app);
			}
		}
	}

	private void onApplicationCreated(Application app) {
		NotificationUtils.showFeedbackMessage(AppActivity.this, Html.fromHtml("Created application <b>" + app.getName() + "</b>"));
		finish();
	}

	private void onApplicationUpdated(Application app) {
		NotificationUtils.showFeedbackMessage(AppActivity.this, Html.fromHtml("Updated application <b>" + app.getName() + "</b>"));
		finish();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (DIALOG_SELECT_APP_TEMPLATE == id) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select Template");
			final List<TouchatagTemplate> apps = TouchatagTemplate.getTemplateList();
			TouchatagTemplateListAdapter adapter = new TouchatagTemplateListAdapter(this, apps);
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setSelectedAppTemplate(apps.get(which));
				}
			});
			return builder.create();
		} else if (DIALOG_FILTERED_SELECT_APP_TEMPLATE == id) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select Template");
			final List<TouchatagTemplate> apps = filteredTemplates;
			TouchatagTemplateListAdapter adapter = new TouchatagTemplateListAdapter(this, apps);
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					TouchatagTemplate template = apps.get(which);
					setSelectedAppTemplate(template);
					template.initWithData(AppActivity.this, getIntent());
				}
			});
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	public class TouchatagTemplateListAdapter extends ArrayAdapter<TouchatagTemplate> {

		private LayoutInflater layoutInflater;

		public TouchatagTemplateListAdapter(Context context, List<TouchatagTemplate> objects) {
			super(context, R.id.lbl_app_template_item_appname, objects);
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TouchatagTemplate app = getItem(position);
			if (convertView == null) {
				convertView = (View) layoutInflater.inflate(R.layout.app_template_item, null);
			}

			ImageView icon = (ImageView) convertView.findViewById(R.id.img_app_template_icon);
			Drawable imgTemplateIcon = app.getIcon(getPackageManager());
			if (imgTemplateIcon != null) {
				icon.setImageDrawable(imgTemplateIcon);
			}

			TextView appName = (TextView) convertView.findViewById(R.id.lbl_app_template_item_appname);
			appName.setText(Html.fromHtml("<b>" + app.getName() + "</b>"));

			TextView appDescription = (TextView) convertView.findViewById(R.id.lbl_app_template_item_description);
			appDescription.setText(Html.fromHtml("<i>" + app.getDescription() + "</i>"));

			return convertView;
		}

	}

	class CreateApplicationAsyncTask extends AcsApiAsyncTask<Application, Application> {

		public CreateApplicationAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		@Override
		public Application doApiCall(Application... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			Application app = params[0];
			CorrelationDefinitionApiClient correlationApi = AcsApiClientFactory.createCorrelationDefinitionApiClient(settingsStore);
			ApplicationApiClient applicationApi = AcsApiClientFactory.createApplicationApiClient(settingsStore);
			Application createdApp = applicationApi.create(app);
			createdApp.setSpecification(app.getSpecification());
			createdApp.setName(app.getName());
			createdApp.setDescription(app.getDescription());
			createdApp.setTemplate(app.getTemplate());
			appStore.store(createdApp);
			if (selectedTags.size() > 0) {
				CorrelationDefinition corrDef = correlationApi.get();
				for (Tag tag : selectedTags) {
					corrDef.associateTagToCommand(tag.getHash(), createdApp.getCommand());
				}
				CorrelationDefinition updatedCorrDef = correlationApi.update(corrDef);
				assStore.update(updatedCorrDef);
			}
			return createdApp;
		}

		@Override
		public void processOutput(Application app) {
			onApplicationCreated(app);
		}

	}

	class UpdateApplicationAsyncTask extends AcsApiAsyncTask<Application, Application> {

		public UpdateApplicationAsyncTask(String message, String acsApiExpMessage, Context ctx) {
			super(message, acsApiExpMessage, ctx);
		}

		@Override
		public Application doApiCall(Application... params) throws AcsApiException, NoInternetException, UnexpectedHttpResponseCodeException {
			Application app = params[0];
			CorrelationDefinitionApiClient correlationApi = AcsApiClientFactory.createCorrelationDefinitionApiClient(settingsStore);
			ApplicationApiClient applicationApi = AcsApiClientFactory.createApplicationApiClient(settingsStore);
			
			Application updatedApp = applicationApi.update(app);
			updatedApp.setSpecification(app.getSpecification());
			updatedApp.setName(app.getName());
			updatedApp.setDescription(app.getDescription());
			updatedApp.setTemplate(app.getTemplate());
			appStore.store(updatedApp);

			if (selectedTags.size() > 0) {
				CorrelationDefinition corrDef = correlationApi.get();
				for (Tag tag : selectedTags) {
					corrDef.associateTagToCommand(tag.getHash(), app.getCommand());
				}
				CorrelationDefinition updatedCorrDef = correlationApi.update(corrDef);
				assStore.update(updatedCorrDef);
			}
			return app;
		}

		@Override
		public void processOutput(Application app) {
			onApplicationUpdated(app);
		}
	}

	class LoadApplicationDetailsAsyncTask extends AsyncTask<Void, Void, Object> {

		private Application app;
		private TouchatagTemplate template;
		private ProgressDialog progressDialog;

		LoadApplicationDetailsAsyncTask(Application app) {
			this.app = app;
			if (app.getTemplate() != null) {
				template = TouchatagTemplate.valueOf(app.getTemplate());
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (template.isPreInitLongRunning()) {
				progressDialog = ProgressDialog.show(AppActivity.this, null, "Loading...");
			}
		}

		@Override
		protected Object doInBackground(Void... params) {
			if (template != null) {
				return template.preInitComponentsForEdit(AppActivity.this, app.getSpecification());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if (template.isPreInitLongRunning()) {
				progressDialog.dismiss();
			}
			AppActivity.this.setSelectedAppTemplate(template);
			selectedTemplate.initComponentsForEdit(AppActivity.this, app.getSpecification(), result);
		}
	}

}
