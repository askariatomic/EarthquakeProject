package com.askari.earthquakeanalysis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class MapOfIndonesiaActivity extends Activity {
	private GoogleMap earthquakeMap;
	static final LatLng INDONESIA = new LatLng(-2.548926, 118.0148634);
	final int RQSGooglePlayServices = 1;
	
	JSONArray JSONArray = null;
	
	private ProgressDialog progressDialog;
	
	static boolean statusJSON = false;
	private static String url_earthquake_search = "http://askari.it.student.pens.ac.id/earthquake/get_marker_map_of_indonesia.php";
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_of_indonesia_layout);
        
        // Earthquake Map
        earthquakeMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		earthquakeMap.getUiSettings().setZoomControlsEnabled(true);
        earthquakeMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
     	earthquakeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INDONESIA, 3));
     	new buildEarthquakeMap().execute();
    }
    
    private class buildEarthquakeMap extends AsyncTask<String, String, JSONObject> {
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			progressDialog = new ProgressDialog(MapOfIndonesiaActivity.this);
			progressDialog.setMessage("Connecting to the server...");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		protected JSONObject doInBackground(String... args) {
			JSONParser JSONParser = new JSONParser();
			JSONObject JSONObj = JSONParser.getJSON(url_earthquake_search);
			if (JSONObj == null) {
				statusJSON = false;
			} else {
				statusJSON = true;    
			}
			return JSONObj;
		}
		
		@Override
		protected void onPostExecute(JSONObject JSONObject) {
			if (statusJSON == true) {
				try {
					Log.e("Status: ", statusJSON + "");
					earthquakeMap.clear();
					JSONArray = JSONObject.optJSONArray("earthquake");
					
					if (JSONArray != null) {
						for (int i = 0; i < JSONArray.length(); i++) {
							JSONObject array = JSONArray.getJSONObject(i);
							double latitude = array.getDouble("latitude");
							double longitude = array.getDouble("longitude");
							float magnitude = array.getInt("magnitude");
							int color = 0;
							
							if (magnitude > 3 && magnitude <= 5) {
								color = 0x9000ffff;
							} else if (magnitude > 5 && magnitude <= 6) {
								color = 0x9000ff00;
							} else if (magnitude > 6 && magnitude <= 7) {
								color = 0x90ffa500;
							} else if (magnitude > 7 && magnitude <=8 ) {
								color = 0x90ff0000;
							} else if (magnitude > 8) {
								color = 0x90800000;
							}
							
							LatLng latLng = new LatLng(latitude, longitude);

							earthquakeMap.addCircle(new CircleOptions()
								.center(latLng)
								.radius(magnitude * magnitude * 3000)
								.strokeColor(color)
								.fillColor(color));
						}					
						progressDialog.dismiss();
					} else {
						Toast.makeText(getApplicationContext(), "Data not found!", Toast.LENGTH_SHORT).show();
						progressDialog.dismiss();
					}
				} catch(JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Unable to get the data! Please check your connection.", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				Log.e("Status: ", statusJSON + "");
			}
		}
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		int statusGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (statusGooglePlayServices != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(statusGooglePlayServices, this, RQSGooglePlayServices);
		}
	}
}