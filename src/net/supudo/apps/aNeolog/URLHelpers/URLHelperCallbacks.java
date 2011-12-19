package net.supudo.apps.aNeolog.URLHelpers;

import org.json.JSONObject;

public interface URLHelperCallbacks {
	public void updateModelWithJSONObject(JSONObject object, Integer serviceId);
	public void connectionFailed(Integer serviceId);
}
