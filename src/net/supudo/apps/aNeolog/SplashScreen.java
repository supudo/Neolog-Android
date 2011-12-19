package net.supudo.apps.aNeolog;

import net.supudo.apps.aBombaJob.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SplashScreen extends Activity {
	
    private Thread mSplashThread;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

    	setContentView(R.layout.splash);
    	
	    final ImageView splashImageView = (ImageView) findViewById(R.id.splashImageView);
	    splashImageView.setBackgroundResource(R.drawable.splashscreen);
    	final SplashScreen sPlashScreen = this;   
    	
    	mSplashThread =  new Thread(){
    		@Override
    		public void run(){
    			try {
    				synchronized (this) {
    					wait(2000);
    				}
    			} 
    			catch (InterruptedException ex) { }

    			finish();
    			
    			Intent intent = new Intent();
    			intent.setClass(sPlashScreen, BombaJobActivity.class);
    			startActivity(intent);
    		}
    	};
    	mSplashThread.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return false;
	} 
	  
    @Override
    public boolean onTouchEvent(MotionEvent evt) {
    	if (evt.getAction() == MotionEvent.ACTION_DOWN) {
    		synchronized (mSplashThread) {
    			mSplashThread.notifyAll();
    		}
    	}
    	return true;
    }
}
