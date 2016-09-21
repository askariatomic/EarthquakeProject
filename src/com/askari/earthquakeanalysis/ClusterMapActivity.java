package com.askari.earthquakeanalysis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.askari.earthquakeanalysis.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ALI.VectorLib;
import ALI.ClusteringLib;

public class ClusterMapActivity extends Activity {
	private GoogleMap earthquakeMap;
	static final LatLng INDONESIA = new LatLng(-2.548926, 118.0148634);
	
	JSONArray JSONArray = null;
	
	private ProgressDialog progressDialog;
	final int RQSGooglePlayServices = 1;
		
	private int fromYear = 1963;
	private int toYear = 2015;
	private int fromMagnitude = 1;
	private int toMagnitude = 10;
	
	static boolean statusJSON = false;
	
	private static String url_earthquake = "http://askari.it.student.pens.ac.id/earthquake/get_marker_cluster_map.php";
	private static String from_magnitude = "?from_magnitude=";
	private static String to_magnitude = "&to_magnitude=";
	private static String from_year = "&from_year=";
	private static String to_year = "&to_year=";
	private static String url_earthquake_search = "";
	
	private TextView txtRangeYear;
	private TextView txtRangeMagnitude;
	
	VectorLib vlib = new VectorLib();
	ClusteringLib clib = new ClusteringLib();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cluster_map_layout);
        
		// Earthquake Map
		earthquakeMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		earthquakeMap.getUiSettings().setZoomControlsEnabled(true);
		earthquakeMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		earthquakeMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INDONESIA, 3));
    }
    
    private class buildEarthquakeMap extends AsyncTask<String, String, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();   
			progressDialog = new ProgressDialog(ClusterMapActivity.this);
			progressDialog.setMessage("Clustering process...");
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
					double[][] data_cluster = new double[JSONArray.length()][2];
					
					if (JSONArray != null) {
						for (int i = 0; i < JSONArray.length(); i++) {
							JSONObject array = JSONArray.getJSONObject(i);
							double latitude = array.getDouble("latitude");
							double longitude = array.getDouble("longitude");
							
							data_cluster[i][0] = latitude;
							data_cluster[i][1] = longitude;
						}
						
						double[][] newData = vlib.Normalization("zscore", data_cluster);		
						double[] optimalK = clib.getOptimalK("average", newData);
						int[] dataClass = clib.HierarchicalKmeans(newData, (int) optimalK[0]);
						
						for (int x = 0; x < data_cluster.length; x++) {
							for (int z = 0; z < data_cluster[0].length; z++) {
								
							}
							double lat = data_cluster[x][0];
							double lng = data_cluster[x][1];
							double classData = dataClass[x];
							int color;
							
							if (classData == 0) {
								color = 0x75ff0000;
							} else if (classData == 1) {
								color = 0x7500ff00;
							} else if (classData == 2) {
								color = 0x750000ff;
							} else if (classData == 3) {
								color = 0x75ffff00;
							} else if (classData == 4) {
								color = 0x75ff00ff;
							} else if (classData == 5) {
								color = 0x7500ffff;
							} else if (classData == 6) {
								color = 0x75deb887;
							} else if (classData == 7) {
								color = 0x75ff7f50;
							} else if (classData == 8) {
								color = 0x75dc143c;
							} else if (classData == 9) {
								color = 0x75ff8c00;
							} else {
								color = 0x75ff0000;
							}
							
							LatLng latLong = new LatLng(lat, lng);

							earthquakeMap.addCircle(new CircleOptions()
								.center(latLong)
								.radius(15000)
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
	
	public void showSearchParameter() {
		final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
		final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View dialogLayout = inflater.inflate(R.layout.search_dialog, (ViewGroup) findViewById(R.id.layout_dialog));
		
		popDialog.setTitle("Search Parameter");
		popDialog.setView(dialogLayout);
		
		txtRangeYear = (TextView) dialogLayout.findViewById(R.id.txtRangeYear);
		txtRangeMagnitude = (TextView) dialogLayout.findViewById(R.id.txtRangeMagnitude);
		
		// Seek Bar Range Year
		RangeSeekBar<Integer> seekBarRangeYear = new RangeSeekBar<Integer>(1963, 2015, this);
		seekBarRangeYear.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				fromYear = minValue;
				toYear = maxValue;
				txtRangeYear.setText("Year: " + minValue + " - " + maxValue);
		    }
		});
		LinearLayout layoutRangeYear = (LinearLayout) dialogLayout.findViewById(R.id.seekbar_range_year);
		layoutRangeYear.addView(seekBarRangeYear);
		
		// Seek Bar Range Magnitude
		RangeSeekBar<Integer> seekBarRangeMagnitude = new RangeSeekBar<Integer>(1, 10, this);
		seekBarRangeMagnitude.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
				fromMagnitude = minValue;
				toMagnitude = maxValue;
				txtRangeMagnitude.setText("Magnitude: " + minValue + " - " + maxValue + " RS");
		    }
		});
		LinearLayout layoutRangeMagnitude = (LinearLayout) dialogLayout.findViewById(R.id.seekbar_range_magnitude);
		layoutRangeMagnitude.addView(seekBarRangeMagnitude);
		
		// Button OK
		popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				url_earthquake_search = url_earthquake + from_magnitude + Integer.toString(fromMagnitude) + to_magnitude + Integer.toString(toMagnitude) + from_year + Integer.toString(fromYear) + to_year + Integer.toString(toYear);
				new buildEarthquakeMap().execute();
			}
		});
		
		// Button Cancel
		popDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
			}
		});
		
		popDialog.create();
		popDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.action_location_searching:
				showSearchParameter();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
