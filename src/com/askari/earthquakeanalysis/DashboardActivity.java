package com.askari.earthquakeanalysis;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard_layout);
		
        Button btn_map_of_indonesia = (Button) findViewById(R.id.btn_map_of_indonesia);
        Button btn_magnitude_map = (Button) findViewById(R.id.btn_magnitude_map);
        Button btn_cluster_map = (Button) findViewById(R.id.btn_cluster_map);
        Button btn_magnitude_chart = (Button) findViewById(R.id.btn_magnitude_chart);
        Button btn_about = (Button) findViewById(R.id.btn_about);
        Button btn_help = (Button) findViewById(R.id.btn_help);
        Button btn_help2 = (Button) findViewById(R.id.btn_help2);
        btn_help2.setVisibility(View.INVISIBLE);
        Button btn_exit = (Button) findViewById(R.id.btn_exit);
                
        btn_map_of_indonesia.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapOfIndonesiaActivity.class);
                startActivity(i);
            }
        });
        
        btn_magnitude_map.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MagnitudeMapActivity.class);
                startActivity(i);
            }
        });
        
        btn_cluster_map.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ClusterMapActivity.class);
                startActivity(i);
            }
        });
        
        btn_magnitude_chart.setOnClickListener(new View.OnClickListener() {  
            
        	@Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MagnitudeChartActivity.class);
                startActivity(i);
            }
        });
        
        btn_about.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(i);
            }
        });
        
        btn_help.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(i);
            }
        });
        
        btn_exit.setOnClickListener(new View.OnClickListener() {
            
        	@Override
            public void onClick(View view) {
            	finish();
                System.exit(0);
            }
        });
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}
}