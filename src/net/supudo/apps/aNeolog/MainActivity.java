package net.supudo.apps.aNeolog;

import net.supudo.apps.aNeolog.Misc.About;
import net.supudo.apps.aNeolog.Misc.Settings;
import net.supudo.apps.aNeolog.Words.LettersAndNests;
import net.supudo.apps.aNeolog.Words.Search;
import net.supudo.apps.aNeolog.Words.SendWord;

import net.supudo.apps.aNeolog.R;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent mIntent;
        switch (item.getItemId()) {
	        case R.id.words:
	        	mIntent = new Intent().setClass(this, LettersAndNests.class);
            	startActivityForResult(mIntent, 0);
	        	break;
	        case R.id.sendword:
	        	mIntent = new Intent().setClass(this, SendWord.class);
            	startActivityForResult(mIntent, 0);
	        	break;
	        case R.id.settings:
	        	mIntent = new Intent().setClass(this, Settings.class);
            	startActivityForResult(mIntent, 0);
	        	break;
	        case R.id.search:
	        	mIntent = new Intent().setClass(this, Search.class);
            	startActivityForResult(mIntent, 0);
	        	break;
	        case R.id.about:
	        	mIntent = new Intent().setClass(this, About.class);
            	startActivityForResult(mIntent, 0);
	        	break;
        }
        return true;
    }
}
