package net.supudo.apps.aNeolog.URLHelpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import android.os.AsyncTask;

public class AsyncPostRequest extends AsyncTask<String, String, String> {

	AsyncPostRequestCallbacks mDelegate;
	
	public AsyncPostRequest(AsyncPostRequestCallbacks delegate) {
		mDelegate = delegate;
	}

	@Override
    protected String doInBackground(String... uri) {
		String _url = uri[0];
		String _postData = uri[1];
		String responseString = "";
		try {
		    String data = URLEncoder.encode("jsonobj", "UTF-8") + "=" + URLEncoder.encode(_postData, "UTF-8");

		    URL url = new URL(_url);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();

		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    while ((line = rd.readLine()) != null) {
		    	if (line != null)
		    		responseString += line;
		    }
		    wr.close();
		    rd.close();
		}
		catch (Exception e) {
		}
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
		if (result != null)
			mDelegate.onPostSuccess(result);
		else
			mDelegate.onPostFail();
    }

}
