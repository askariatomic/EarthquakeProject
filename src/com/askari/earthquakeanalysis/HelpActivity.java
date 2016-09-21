package com.askari.earthquakeanalysis;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class HelpActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_layout);
        
        TextView foo = (TextView)findViewById(R.id.textView1);
        foo.setText(Html.fromHtml(getString(R.string.help1)));
        
        TextView pens = (TextView)findViewById(R.id.textView2);
        pens.setText(Html.fromHtml(getString(R.string.pens)));
        
        TextView help2 = (TextView)findViewById(R.id.textView6);
        help2.setText(Html.fromHtml(getString(R.string.help2)));
        
        TextView help3 = (TextView)findViewById(R.id.textView8);
        help3.setText(Html.fromHtml(getString(R.string.help3)));
        
        ImageView img3 = (ImageView)findViewById(R.id.imageView4);
        img3.setVisibility(View.GONE);
    }
}
