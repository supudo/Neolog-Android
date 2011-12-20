package net.supudo.apps.aNeolog.Misc;

import net.supudo.apps.aNeolog.MainActivity;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.TextContentModel;

import android.os.Bundle;
import android.webkit.WebView;

public class About extends MainActivity {
	
	private WebView aboutWebView;
	private DataHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setTitle(R.string.menu_About);
		
		if (dbHelper == null)
			dbHelper = new DataHelper(this);
		
		TextContentModel t = dbHelper.GetTextContent(2);
		String aboutHtml = "<html><head></head>";
		aboutHtml += "<style>body, div, p, a { color: #ffffff; } a { text-decoration: underline; }</style>";
		aboutHtml += "<body>";
		aboutHtml += t.Content;
		aboutHtml += "</body></html>";
	    aboutWebView = (WebView)findViewById(R.id.webview);
	    aboutWebView.setBackgroundColor(0);
		aboutWebView.loadDataWithBaseURL("neolog://localurl", aboutHtml, "text/html", "utf-8", "");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}