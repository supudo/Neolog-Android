package net.supudo.apps.aNeolog.URLHelpers;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class URLHelper implements AsyncURLConnectionCallbacks, AsyncPostRequestCallbacks {

	private Integer serviceId;
	private URLHelperCallbacks mDelegate;
	private AsyncURLConnection conn = null;
	private AsyncPostRequest connPost = null;

	@Override
	public void onSuccess(ByteArrayBuffer baf) {
		try {
			String jsonString = EncodingUtils.getString(baf.toByteArray(), "utf8");
			Log.d("URLHelper", "JSON response = " + jsonString);
			JSONObject obj = null;
			if (!jsonString.equals("") && !jsonString.equals("[]"))
				obj = new JSONObject(jsonString);
			mDelegate.updateModelWithJSONObject(obj, this.serviceId);
		}
		catch (Exception e) {
			e.printStackTrace();
			mDelegate.connectionFailed(this.serviceId);
		}
	}

	@Override
	public void onFail() {
		mDelegate.connectionFailed(this.serviceId);
	}

	@Override
	public void onPostSuccess(String _response) {
		try {
			Log.d("URLHelper", _response);
			JSONObject obj = new JSONObject(_response);
			mDelegate.updateModelWithJSONObject(obj, this.serviceId);
		}
		catch (Exception e) {
			e.printStackTrace();
			mDelegate.connectionFailed(this.serviceId);
		}
	}

	@Override
	public void onPostFail() {
		mDelegate.connectionFailed(this.serviceId);
	}

	public URLHelper(URLHelperCallbacks delegate) {
		mDelegate = delegate;
	}
	
	public boolean isOnline(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public void cancel() {
		if (conn != null) {
			conn.cancel(true);
			conn = null;
		}
		if (connPost != null) {
			connPost.cancel(true);
			connPost = null;
		}
	}

	public void postData(String url, String postData, Integer serviceId) throws MalformedURLException {
		if (connPost != null) {
			connPost.cancel(true);
			connPost = null;
		}
		this.serviceId = serviceId;
		connPost = new AsyncPostRequest(this);
		connPost.execute(url, postData);
	}

	public void loadURLString(String url, Integer serviceId) throws MalformedURLException {
		if (conn != null) {
			conn.cancel(true);
			conn = null;
		}
		this.serviceId = serviceId;
		conn = new AsyncURLConnection(this);
		conn.execute(new URL(url));
	}

	public void loadURLString(String url, Integer serviceId, int timeout) throws MalformedURLException {
		if (conn != null) {
			conn.cancel(true);
			conn = null;
		}
		this.serviceId = serviceId;
		conn = new AsyncURLConnection(this, timeout);
		conn.execute(new URL(url));
	}
}
