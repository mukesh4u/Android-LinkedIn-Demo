package com.mukesh.linkedin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linkedin.R;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.mukesh.linkedin.LinkedinDialog.OnVerifyListener;

/**
 * @author Mukesh Kumar Yadav
 */
public class LinkedInSampleActivity extends Activity {
	Button login;
	Button share;
	EditText et;
	TextView name;
	ImageView photo;
	public static final String OAUTH_CALLBACK_HOST = "litestcalback";

	final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory
            .getInstance().createLinkedInOAuthService(
                    Config.LINKEDIN_CONSUMER_KEY,Config.LINKEDIN_CONSUMER_SECRET, Config.scopeParams);
	final LinkedInApiClientFactory factory = LinkedInApiClientFactory
			.newInstance(Config.LINKEDIN_CONSUMER_KEY,
					Config.LINKEDIN_CONSUMER_SECRET);
	LinkedInRequestToken liToken;
	LinkedInApiClient client;
	LinkedInAccessToken accessToken = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		share = (Button) findViewById(R.id.share);
		name = (TextView) findViewById(R.id.name);
		et = (EditText) findViewById(R.id.et_share);
		login = (Button) findViewById(R.id.login);
		photo = (ImageView) findViewById(R.id.photo);

		login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				linkedInLogin();
			}
		});

		// share on linkedin
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String share = et.getText().toString();
				if (null != share && !share.equalsIgnoreCase("")) {
					client = factory.createLinkedInApiClient(accessToken);
					client.postNetworkUpdate(share);
					et.setText("");
					Toast.makeText(LinkedInSampleActivity.this,
							"Shared sucessfully", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LinkedInSampleActivity.this,
							"Please enter the text to share",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void linkedInLogin() {
		ProgressDialog progressDialog = new ProgressDialog(
				LinkedInSampleActivity.this);

		LinkedinDialog d = new LinkedinDialog(LinkedInSampleActivity.this,
				progressDialog);
		d.show();

		// set call back listener to get oauth_verifier value
		d.setVerifierListener(new OnVerifyListener() {
			@Override
			public void onVerify(String verifier) {
				try {
					Log.i("LinkedinSample", "verifier: " + verifier);

					accessToken = LinkedinDialog.oAuthService
							.getOAuthAccessToken(LinkedinDialog.liToken,
									verifier);
					LinkedinDialog.factory.createLinkedInApiClient(accessToken);
					client = factory.createLinkedInApiClient(accessToken);
					// client.postNetworkUpdate("Testing by Mukesh!!! LinkedIn wall post from Android app");
					Log.i("LinkedinSample",
							"ln_access_token: " + accessToken.getToken());
					Log.i("LinkedinSample",
							"ln_access_token: " + accessToken.getTokenSecret());
					Person p = client.getProfileForCurrentUser();
					name.setText("Welcome " + p.getFirstName() + " "
							+ p.getLastName());
					name.setVisibility(0);
					login.setVisibility(4);
					share.setVisibility(0);
					et.setVisibility(0);

				} catch (Exception e) {
					Log.i("LinkedinSample", "error to get verifier");
					e.printStackTrace();
				}
			}
		});

		// set progress dialog
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
}
