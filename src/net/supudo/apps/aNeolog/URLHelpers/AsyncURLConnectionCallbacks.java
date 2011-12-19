package net.supudo.apps.aNeolog.URLHelpers;

import org.apache.http.util.ByteArrayBuffer;

public interface AsyncURLConnectionCallbacks {
	public void onSuccess(ByteArrayBuffer baf);
	public void onFail();
}
