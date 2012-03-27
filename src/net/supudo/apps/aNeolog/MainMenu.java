package net.supudo.apps.aNeolog;

import android.os.Bundle;

public class MainMenu extends MainActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    	setTitle(R.string.app_name);
    }

}
