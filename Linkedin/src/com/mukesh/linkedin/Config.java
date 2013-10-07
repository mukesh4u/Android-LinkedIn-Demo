package com.mukesh.linkedin;

public class Config {

	public static String LINKEDIN_CONSUMER_KEY = "your consumer key here";
	public static String LINKEDIN_CONSUMER_SECRET = "your consumer secret here";
	public static String scopeParams = "rw_nus+r_basicprofile";
	
	public static String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
	public static String OAUTH_CALLBACK_HOST = "callback";
	public static String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
}
