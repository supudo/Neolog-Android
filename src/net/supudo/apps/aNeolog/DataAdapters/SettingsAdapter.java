package net.supudo.apps.aNeolog.DataAdapters;

import java.util.ArrayList;

import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.SettingModel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsAdapter extends ArrayAdapter<SettingModel> {

	private LayoutInflater mInflater;
	private ArrayList<SettingModel> items;
	private Context context;
	private DataHelper dbHelper;

	public SettingsAdapter(Context _context, int textViewResourceId, ArrayList<SettingModel> items) {
		super(_context, textViewResourceId, items);
		mInflater = LayoutInflater.from(_context);
		this.items = items;
		this.context = _context;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_setting, parent, false);
			holder = new ViewHolder();
			holder.infoIcon = (ImageView) convertView.findViewById(R.id.info_icon);
			holder.settingName = (TextView) convertView.findViewById(R.id.setting);
			holder.settingSwitch = (ToggleButton)convertView.findViewById(R.id.toggle);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		SettingModel stt = (SettingModel)items.get(position);
		
		holder.infoIcon.setImageResource(R.drawable.iconinfo);
		holder.infoIcon.setTag(stt.SName);
		holder.infoIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	AlertDialog.Builder infoBox = new AlertDialog.Builder(context);
            	String info = (String) context.getResources().getText(context.getResources().getIdentifier("about_info_" + (String)v.getTag(), "string", "net.supudo.apps.aNeolog"));
            	infoBox.setMessage(info);
            	infoBox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
            	infoBox.show();
            }
        });

		holder.settingName.setText((String) this.context.getResources().getText(this.context.getResources().getIdentifier("about_" + stt.SName, "string", "net.supudo.apps.aNeolog")));

		holder.settingSwitch.setTag(stt.SName);
		holder.settingSwitch.setChecked(Boolean.parseBoolean(stt.SValue));
		holder.settingSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (dbHelper == null)
					dbHelper = new DataHelper(context);
				String sName = (String)buttonView.getTag(), sValue = (isChecked ? "TRUE" : "FALSE");
				dbHelper.SetSetting(sName, sValue);

				if (sName.equalsIgnoreCase("StorePrivateData"))
					CommonSettings.stStorePrivateData = Boolean.valueOf(sValue);
				else if (sName.equalsIgnoreCase("OnlineSearch"))
					CommonSettings.stSearchOnline = Boolean.valueOf(sValue);
				else if (sName.equalsIgnoreCase("InAppEmail"))
					CommonSettings.stInAppEmail = Boolean.valueOf(sValue);
	        }
		});

  		return convertView;
 	}

  	static class ViewHolder {
 		TextView settingName;
 		ToggleButton settingSwitch;
 		ImageView infoIcon;
 	}
}
