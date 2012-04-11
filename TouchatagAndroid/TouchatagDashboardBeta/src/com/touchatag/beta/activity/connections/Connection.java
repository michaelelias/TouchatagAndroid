package com.touchatag.beta.activity.connections;

import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import android.net.Uri;

public enum Connection {

	FOURSQUARE("foursquare-access-token", "FGBJ11X3B4JNIMRA1HBO3OYNN3WBIFSYUAPW5GVZAVQTDT3X", "SQVEPFJB1ZRTLYASBJVUHTWX4M00OSYXKS3SQ05DHGWR03L0") {
		
		private String callbackUrl = "touchatag://callback/foursquare";
		
		@Override
		public void onAuthorize(ConnectionAuthorizer authorizer) {
			String authorizeUrl = "https://foursquare.com/oauth2/authenticate?client_id="+ getKey() +"&response_type=token&redirect_uri=" + callbackUrl;
			authorizer.authorize(authorizeUrl);
		}
		
		@Override
		public boolean onAuthorized(String url, ConnectionAuthorizer authorizer) {
			if(url.startsWith(callbackUrl)){
				Uri uri = Uri.parse(url);
				String fragment = uri.getFragment();
				if(fragment.startsWith("access_token=")){
					String accessToken = fragment.split("=")[1];
					authorizer.storeAccessToken(this, accessToken);
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean onTokenRetrieved(String url, String content, ConnectionAuthorizer authorizer) {
			// TODO Auto-generated method stub
			return false;
		}
		
	},
	GOWALLA("gowalla-access-token","cdaa7bbd15f543588c192abbd6c02c14", "23b0420adfa1481296953f4bcdd9bc96"){

		private String callbackUrl = "touchatag://callback/gowalla";
		
		@Override
		public void onAuthorize(ConnectionAuthorizer authorizer) {
			String authorizeUrl = "https://gowalla.com/api/oauth/new?client_id="+ getKey() +"&redirect_uri=" + callbackUrl;
			authorizer.authorize(authorizeUrl);
		}

		@Override
		public boolean onAuthorized(String url, ConnectionAuthorizer authorizer) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onTokenRetrieved(String url, String content, ConnectionAuthorizer authorizer) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	private String metadataType;
	private String key;
	private String secret;
	
	private Connection(String metadataType, String key, String secret){
		this.metadataType = metadataType;
		this.key = key;
		this.secret = secret;
	}
	
	public CommonsHttpOAuthConsumer getConsumer(){
		return new CommonsHttpOAuthConsumer(key, secret); 
	}
	
	public abstract void onAuthorize(ConnectionAuthorizer authorizer);
	
	public abstract boolean onAuthorized(String url, ConnectionAuthorizer authorizer);

	public abstract boolean onTokenRetrieved(String url, String content, ConnectionAuthorizer authorizer);

	public String getKey() {
		return key;
	}

	public String getSecret() {
		return secret;
	}

	public String getMetadataType() {
		return metadataType;
	}
	
}
