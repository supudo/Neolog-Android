package net.supudo.apps.aNeolog.URLHelpers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;

public class AsyncURLConnection extends AsyncTask<URL, Long, ByteArrayBuffer> {

	AsyncURLConnectionCallbacks mDelegate;
	private int timeout = 60000;
	
	public AsyncURLConnection(AsyncURLConnectionCallbacks delegate) {
		mDelegate = delegate;
	}

	public AsyncURLConnection(AsyncURLConnectionCallbacks delegate, int timeout) {
		mDelegate = delegate;
		this.timeout = timeout;
	}
	
	@Override
	protected ByteArrayBuffer doInBackground(URL... params) {
		try {
			URLConnection conn = (params[0]).openConnection();
			conn.setConnectTimeout(timeout);

			// Define input streams.
			InputStream is = conn.getInputStream();
			BufferedInputStream bif = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			long curLength = 0;
			while ((current = bif.read()) != -1) {
				baf.append((byte) current);
				if (isCancelled())
					return null;
				if (baf.length() - curLength > 4096) {
					publishProgress((long)baf.length());
					curLength = baf.length();
				}
			}
			if (isCancelled())
				return null;
			return baf;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(ByteArrayBuffer result) {
		if (result != null)
			mDelegate.onSuccess(result);
		else
			mDelegate.onFail();
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);
	}

}
