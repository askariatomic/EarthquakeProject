package com.askari.earthquakeanalysis;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        
        TextView foo = (TextView)findViewById(R.id.textView1);
        foo.setText(Html.fromHtml(getString(R.string.about_content)));
        
        TextView pens = (TextView)findViewById(R.id.textView2);
        pens.setText(Html.fromHtml(getString(R.string.pens)));
    }
}
