package net.supudo.apps.aNeolog.Misc;

import java.util.ArrayList;

import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.TableActivity;
import net.supudo.apps.aNeolog.DataAdapters.SettingsAdapter;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.SettingModel;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class Settings extends TableActivity {

	private ArrayList<SettingModel> listSettings;
	private DataHelper dbHelper;
	private TextView txtEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		setTitle(R.string.settings);
		
		txtEmpty = (TextView)this.getListView().getEmptyView();
		txtEmpty.setText("");

		if (dbHelper == null)
			dbHelper = new DataHelper(this);

		listSettings = dbHelper.selectEditableSettings();

		if (listSettings.size() == 0)
			txtEmpty.setText("");
		else {
    		setListAdapter(new SettingsAdapter(Settings.this, R.layout.list_item, listSettings));

    		ListView lv = getListView();
    		lv.setTextFilterEnabled(true);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
