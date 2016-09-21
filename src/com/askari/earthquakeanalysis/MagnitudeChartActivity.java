package com.askari.earthquakeanalysis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.askari.earthquakeanalysis.RangeSeekBar.OnRangeSeekBarChangeListener;
import ALI.VectorLib;
import ALI.ClusteringLib;

public class MagnitudeChartActivity extends Activity {
	VectorLib vlib = new VectorLib();
	ClusteringLib clib = new ClusteringLib();
	
	private ProgressDialog progressDialog;
	static boolean statusJSON = false;
	JSONArray JSONArray = null;
	
	private int fromYear = 1963;
	private int toYear = 2015;
	private int fromMagnitude = 1;
	private int toMagnitude = 10;
	private static String url_earthquake = "http://askari.it.student.pens.ac.id/earthquake/get_marker_cluster_chart.php";
	private static String from_magnitude = "?from_magnitude=";
	private static String to_magnitude = "&to_magnitude=";
	private static String from_year = "&from_year=";
	private static String to_year = "&to_year=";
	private static String url_earthquake_search = "";
	
	private TextView txtRangeYear;
	private TextView txtRangeMagnitude;
	
	private GraphicalView mChart;
	TimeSeries series;
	XYMultipleSeriesDataset dataset;
	XYMultipleSeriesRenderer multiRenderer;
	LinearLayout chartContainer;
	StringBuffer title = new StringBuffer("");
		
	XYSeriesRenderer cluster1Renderer;
	XYSeriesRenderer cluster2Renderer;
	XYSeriesRenderer cluster3Renderer;
	XYSeriesRenderer cluster4Renderer;
	XYSeriesRenderer cluster5Renderer;
	XYSeriesRenderer cluster6Renderer;
	XYSeriesRenderer cluster7Renderer;
	XYSeriesRenderer cluster8Renderer;
	XYSeriesRenderer cluster9Renderer;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnitude_chart_layout);
        
        // Creating a XYMultipleSeriesRenderer to customize the whole chart
    	multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.BLACK);
        multiRenderer.setXLabels(0);
        multiRenderer.setXTitle("Year");
        multiRenderer.setYLabelsAlign(Align.RIGHT);
        multiRenderer.setYTitle("Magnitude");
        multiRenderer.setZoomButtonsVisible(true);
        multiRenderer.setBarSpacing(4);
    }
    
    private class buildEarthquakeChart extends AsyncTask<String, String, JSONObject> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();   
			progressDialog = new ProgressDialog(MagnitudeChartActivity.this);
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
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			dataset = new XYMultipleSeriesDataset();
			
			if (statusJSON == true) {
				try {
					Log.e("Status: ", statusJSON + "");
					JSONArray = JSONObject.optJSONArray("earthquake");
					double[][] data_cluster = new double[JSONArray.length()][2];
					String[] data_year = new String[JSONArray.length()];
					String[] data_place = new String[JSONArray.length()];
					double[] data_magnitude = new double[JSONArray.length()];
					
					if (JSONArray == null) {
						Toast.makeText(getApplicationContext(), "Data not found!", Toast.LENGTH_SHORT).show();
						progressDialog.dismiss();
					} else {
						for (int i = 0; i < JSONArray.length(); i++) {
							JSONObject array = JSONArray.getJSONObject(i);
							String year = array.getString("year");
							String place = array.getString("place");
							double magnitude = array.getDouble("magnitude");
							double latitude = array.getDouble("latitude");
							double longitude = array.getDouble("longitude");
							
							data_year[i] = year;
							data_place[i] = place;
							data_magnitude[i] = magnitude;
							data_cluster[i][0] = latitude;
							data_cluster[i][1] = longitude;
						}
						
						double[][] newData = vlib.Normalization("zscore", data_cluster);	
						double[] optimalK = clib.getOptimalK("average", newData);
						int[] dataClass = clib.HierarchicalKmeans(newData, (int) optimalK[0]);
						
						ArrayList<String> clusterTitles = new ArrayList<String>();
						clusterTitles.add("Cluster 1");
						clusterTitles.add("Cluster 2");
						clusterTitles.add("Cluster 3");
						clusterTitles.add("Cluster 4");
						clusterTitles.add("Cluster 5");
						clusterTitles.add("Cluster 6");
						clusterTitles.add("Cluster 7");
						clusterTitles.add("Cluster 8");
						clusterTitles.add("Cluster 9");
						
						chartContainer = (LinearLayout) findViewById(R.id.chart_container);
				        if (mChart != null) {				        	
				        	for (int i = 0; i < 9; i++) {
				        		series.remove(i);
				        	}
				        	
				        	multiRenderer.removeAllRenderers();
				        	chartContainer.removeView(mChart);
				        }
						
						for (int i = 0; i < (int) optimalK[0]; i++) {
							series = new TimeSeries(clusterTitles.get(i));
							
							for (int j = 0; j < data_year.length; j++) {
								if (dataClass[j] == i) {
									series.add(sdf.parse(data_year[j]), data_magnitude[j]);
								}
							}							
							dataset.addSeries(series);
							
							if (i == 0) {
								cluster1Renderer = new XYSeriesRenderer();
							    cluster1Renderer.setColor(Color.BLUE);
							    cluster1Renderer.setPointStyle(PointStyle.DIAMOND);
							    cluster1Renderer.setFillPoints(true);
							    cluster1Renderer.setLineWidth(2);
							    
							    multiRenderer.addSeriesRenderer(cluster1Renderer);
							} else if (i == 1) {
								cluster2Renderer = new XYSeriesRenderer();
						        cluster2Renderer.setColor(Color.YELLOW);
						        cluster2Renderer.setPointStyle(PointStyle.CIRCLE);
						        cluster2Renderer.setFillPoints(true);
						        cluster2Renderer.setLineWidth(2);
						        
						        multiRenderer.addSeriesRenderer(cluster2Renderer);
							} else if (i == 2) {
								cluster3Renderer = new XYSeriesRenderer();
						        cluster3Renderer.setColor(Color.GREEN);
						        cluster3Renderer.setPointStyle(PointStyle.TRIANGLE);
						        cluster3Renderer.setFillPoints(true);
						        cluster3Renderer.setLineWidth(2);
						        
						        multiRenderer.addSeriesRenderer(cluster3Renderer);
							} else if (i == 3) {
								cluster4Renderer = new XYSeriesRenderer();
						        cluster4Renderer.setColor(Color.RED);
						        cluster4Renderer.setPointStyle(PointStyle.SQUARE);
						        cluster4Renderer.setFillPoints(true);
						        cluster4Renderer.setLineWidth(2);
						        
						        multiRenderer.addSeriesRenderer(cluster4Renderer);
							} else if (i == 4) {
								cluster5Renderer = new XYSeriesRenderer();
						        cluster5Renderer.setColor(Color.MAGENTA);
						        cluster5Renderer.setPointStyle(PointStyle.X);
						        cluster5Renderer.setFillPoints(true);
						        cluster5Renderer.setLineWidth(2);
						        
						        multiRenderer.addSeriesRenderer(cluster5Renderer);
							} else if (i == 5) {
								cluster6Renderer = new XYSeriesRenderer();
						        cluster6Renderer.setColor(Color.CYAN);
						        cluster6Renderer.setPointStyle(PointStyle.SQUARE);
						        cluster6Renderer.setFillPoints(true);
						        cluster6Renderer.setLineWidth(2);
						        
						        multiRenderer.addSeriesRenderer(cluster6Renderer);
							} else if (i == 6) {
								cluster7Renderer = new XYSeriesRenderer();
						        cluster7Renderer.setColor(Color.BLUE);
						        cluster7Renderer.setPointStyle(PointStyle.CIRCLE);
						        cluster7Renderer.setFillPoints(true);
						        cluster7Renderer.setLineWidth(2);
						        
						        multiRenderer.addSeriesRenderer(cluster7Renderer);
							} else if (i == 7) {
								cluster8Renderer = new XYSeriesRenderer();
						        cluster8Renderer.setColor(Color.YELLOW);
						        cluster8Renderer.setPointStyle(PointStyle.DIAMOND);
						        cluster8Renderer.setFillPoints(true);
						        cluster8Renderer.setLineWidth(2);
						        
						        multiRenderer.addSeriesRenderer(cluster8Renderer);
							} else if (i == 8) {
								cluster9Renderer = new XYSeriesRenderer();
						        cluster9Renderer.setColor(Color.GREEN);
						        cluster9Renderer.setPointStyle(PointStyle.X);
						        cluster9Renderer.setFillPoints(true);
						        cluster9Renderer.setLineWidth(2);
						        
						        multiRenderer.addSeriesRenderer(cluster9Renderer);
							}
						}					
												
				        mChart = (GraphicalView) ChartFactory.getScatterChartView(getBaseContext(), dataset, multiRenderer);      
				        
				        multiRenderer.setClickEnabled(true);
				        multiRenderer.setSelectableBuffer(10);
				        				        
				        // Adding the chart
				        chartContainer.addView(mChart);
					}
					progressDialog.dismiss();
				} catch(JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Unable to get the data! Please check your connection.", Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				Log.e("Status: ", statusJSON + "");
			}
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
				title.append(Integer.toString(fromYear) + " - " + Integer.toString(toYear));
				multiRenderer.setChartTitle(title.toString());
				new buildEarthquakeChart().execute();
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
