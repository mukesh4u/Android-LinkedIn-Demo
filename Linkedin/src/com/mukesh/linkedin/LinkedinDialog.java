package com.mukesh.linkedin;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;

import com.example.linkedin.R;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;

/**
 * Linkedin dialog
 * 
 * @author Mukesh Kumar Yadav
 */
public class LinkedinDialog extends Dialog {
	private ProgressDialog progressDialog = null;

	public static LinkedInApiClientFactory factory;
	public static LinkedInOAuthService oAuthService;
	public static LinkedInRequestToken liToken;

	/**
	 * Construct a new LinkedIn dialog
	 * 
	 * @param context
	 *            activity {@link Context}
	 * @param progressDialog
	 *            {@link ProgressDialog}
	 */
	public LinkedinDialog(Context context, ProgressDialog progressDialog) {
		super(context);
		this.progressDialog = progressDialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// must call before super.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ln_dialog);

		setWebView();
	}

	/**
	 * set webview.
	 */
	private void setWebView() {
		LinkedinDialog.oAuthService = LinkedInOAuthServiceFactory.getInstance()
				.createLinkedInOAuthService(Config.LINKEDIN_CONSUMER_KEY,
						Config.LINKEDIN_CONSUMER_SECRET);
		LinkedinDialog.factory = LinkedInApiClientFactory.newInstance(
				Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET);

		LinkedinDialog.liToken = LinkedinDialog.oAuthService
				.getOAuthRequestToken(Config.OAUTH_CALLBACK_URL);

		WebView mWebView = (WebView) findViewById(R.id.webkitWebView1);
		mWebView.getSettings().setJavaScriptEnabled(true);

		Log.i("LinkedinSample", LinkedinDialog.liToken.getAuthorizationUrl());
		mWebView.loadUrl(LinkedinDialog.liToken.getAuthorizationUrl());
		mWebView.setWebViewClient(new HelloWebViewClient());

		mWebView.setPictureListener(new PictureListener() {
			@Override
			public void onNewPicture(WebView view, Picture picture) {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

			}
		});

	}

	/**
	 * webview client for internal url loading
	 * callback url: "https://www.linkedin.com/uas/oauth/mukeshyadav4u.blogspot.in"
	 */
	class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.contains(Config.OAUTH_CALLBACK_URL)) {
				Uri uri = Uri.parse(url);
				String verifier = uri.getQueryParameter("oauth_verifier");

				cancel();

				for (OnVerifyListener d : listeners) {
					// call listener method
					d.onVerify(verifier);
				}
			} else if (url
					.contains("https://www.linkedin.com/uas/oauth/mukeshyadav4u.blogspot.in")) {
				cancel();
			} else {
				Log.i("LinkedinSample", "url: " + url);
				view.loadUrl(url);
			}

			return true;
		}
	}

	/**
	 * List of listener.
	 */
	private List<OnVerifyListener> listeners = new ArrayList<OnVerifyListener>();

	/**
	 * Register a callback to be invoked when authentication have finished.
	 * 
	 * @param data
	 *            The callback that will run
	 */
	public void setVerifierListener(OnVerifyListener data) {
		listeners.add(data);
	}

	/**
	 * Listener for oauth_verifier.
	 */
	interface OnVerifyListener {
		/**
		 * invoked when authentication have finished.
		 * 
		 * @param verifier
		 *            oauth_verifier code.
		 */
		public void onVerify(String verifier);
	}
}
