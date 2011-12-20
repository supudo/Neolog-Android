package net.supudo.apps.aNeolog.Words;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.R;

public class LettersAndNests extends TabActivity {

	private DataHelper dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letters_and_nests);

		if (dbHelper == null)
			dbHelper = new DataHelper(this);

		Resources res = getResources();
		TabHost tabHost = getTabHost();

    	tabHost.clearAllTabs();
    	tabHost.setup();

		Intent intentCategories = new Intent(this, Nests.class);
		String titleCat = getString(R.string.tab_nests);
		TabSpec tabCategories = tabHost.newTabSpec("Nests").setIndicator(titleCat, res.getDrawable(R.drawable.tbnests)).setContent(intentCategories);
		tabHost.addTab(tabCategories);

		Intent intentOffers = new Intent(this, Letters.class);
		String titleOffers = getString(R.string.tab_letters);
		TabSpec tabOffers = tabHost.newTabSpec("Offers").setIndicator(titleOffers, res.getDrawable(R.drawable.tbletters)).setContent(intentOffers);
		tabHost.addTab(tabOffers);

		tabHost.getTabWidget().getChildTabViewAt(0).setVisibility(View.VISIBLE);
		tabHost.getTabWidget().getChildTabViewAt(1).setVisibility(View.VISIBLE);
		tabHost.setCurrentTab(0);
		tabHost.setBackgroundColor(Color.TRANSPARENT);
		tabHost.getTabWidget().setBackgroundColor(Color.TRANSPARENT);
	}
}
