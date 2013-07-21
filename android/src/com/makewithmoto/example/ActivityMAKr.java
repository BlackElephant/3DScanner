package com.makewithmoto.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.makewithmoto.boards.MAKr;
import com.makewithmoto.boards.MAKr.MAKrListener;
import com.makewithmoto.makr.fragments.DebugFragment;
import com.makewithmoto.makr.views.PlotView;
import com.makewithmoto.makr.views.PlotView.Plot;

/*
 * write to the board makr.writeSerial(cmd)
 * get data from the board onMessageReceived
 * 
 * 
 */

@SuppressLint("NewApi")
public class ActivityMAKr extends FragmentActivity {

	private static final String TAG = "ExAPP";
	public MAKr makr;
	RadioButton ledon, ledoff;

	private DebugFragment df;
	private boolean f2V = true;
	
	int bufferLoc = 0;
	public ArrayList<Float> buffer = new ArrayList<Float>();
	
	public ArrayList<Float> distance = new ArrayList<Float>();
	public ArrayList<Float> rotation = new ArrayList<Float>();
	public ArrayList<Float> heights = new ArrayList<Float>();
	public float height = 0;
	ActionBar actionBar;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_makr);

		ledon = (RadioButton) findViewById(R.id.ledon);
		ledoff = (RadioButton) findViewById(R.id.ledoff);

		ledon.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					makr.writeSerial("LEDON");
					Toast.makeText(getApplicationContext(), "LEDON",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		ledoff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					makr.writeSerial("LEDOFF");
					Toast.makeText(getApplicationContext(), "LEDOFF",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setLogo(null);
		actionBar.setTitle("MakeWithMoto");

		df = new DebugFragment();
		addFragment(df, R.id.f1);

		final PlotView graphView = (PlotView) findViewById(R.id.plotView1);
		final Plot p1 = graphView.new Plot(Color.RED);
		graphView.addPlot(p1);

		makr = new MAKr(this);
		makr.addListener(new MAKrListener() {


			@Override
			public void onRawDataReceived(byte[] buffer, int size) {

			}
			
			//Float[] finalValues = new Float[1000]; 
			//int i = 0;

			@Override
			public void onMessageReceived(String value) {
				df.adapter.addRightItem(" " + value);
				float val = Float.parseFloat(value);
				val = (float) (val / 74.0 / 2.0);
				
				Log.d("received: ", String.valueOf(val*74*2));
				if(val < 0) {
					if(val*74*2 >= -1.0) {
						Log.d("Starting","Start");
						height = 0;
						bufferLoc = 0;
					
						buffer.clear();
						distance.clear();
						rotation.clear();
						heights.clear();
					}
					else if(val*74*2 >= -2.0) {
						Log.d("Ending","End");
						sendData();
					}
					else {
						float totalBuffer = (float)buffer.size();
						Log.d("Buffer:",String.valueOf(totalBuffer));
						for(int i = 0; i < totalBuffer; i++) {
							heights.add(height);
						}
						
						float radianIncrement = (float) (360.0/totalBuffer);
						Log.d("Increment:",String.valueOf(radianIncrement));
						
						float degrees = 0.0f;
						for(int i = 0; i < totalBuffer; i++) {
							rotation.add(degrees);
							degrees += radianIncrement;
						}
						
						for(int i = 0; i < totalBuffer; i++) {
							distance.add(buffer.get(i));
						}
						
						buffer.clear();
						bufferLoc = 0;
						height++;
					}
				}
				else {
					Log.d("camera: ", String.valueOf(val));
					buffer.add(val);
				}
				//for (i=0; i<1000;i++){
					//finalValues[i] = val;
				//}
				//graphView.setValue(p1, val);
				
				
			}
		});
		makr.start();
	}

	
	public void sendData() {
		Thread thread = new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	String distanceStr = "";
		        	for(int i = 0; i < distance.size(); i++) {
		        		distanceStr += String.valueOf(distance.get(i));
		        		if(i < distance.size()-1) distanceStr += ",";
		        	}
		        	
		        	String rotationStr = "";
		        	for(int i = 0; i < rotation.size(); i++) {
		        		rotationStr += String.valueOf(rotation.get(i));
		        		if(i < rotation.size()-1) rotationStr += ",";
		        	}
		        	
		        	String heightStr = "";
		        	for(int i = 0; i < heights.size(); i++) {
		        		heightStr += String.valueOf(heights.get(i));
		        		if(i < heights.size()-1) heightStr += ",";
		        	}
		        	
		    		String toSend = "{\"distances\" : [" + distanceStr + "], \"rotations\" : [" + rotationStr + "], \"heights\" : [" + heightStr + "]}";
		    		Log.d("Data",toSend);
		    		
		    		HttpClient httpclient = new DefaultHttpClient();
		    		HttpPost httppost = new HttpPost("http://makewithmoto.appspot.com/view=0");

		    	
		    		    // Add your data
		    		    httppost.setEntity(new StringEntity(toSend));
		    		    
		    		    // Execute HTTP Post Request
		    		    HttpResponse response = httpclient.execute(httppost);
		    		    Log.d("Repsonse",response.toString());
		    
//		    		
//		    		URL url;
//		            URLConnection urlConnection;
//		            DataOutputStream outStream;
//		     
//		            // Create connection
//		           
//		            url = new URL("http://makewithmoto.appspot.com/view=0");
//		            urlConnection = url.openConnection();
//		            ((HttpURLConnection)urlConnection).setRequestMethod("POST");
//		            urlConnection.setDoInput(true);
//		            urlConnection.setDoOutput(true);
//		            urlConnection.setUseCaches(false);
//		            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		            urlConnection.setRequestProperty("Content-Length", ""+ toSend.length());
//		     
//		            // Create I/O streams
//		            outStream = new DataOutputStream(urlConnection.getOutputStream());
//		     
//		            // Send request
//		            outStream.writeBytes(toSend);
//		            outStream.flush();
//		            outStream.close();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		});

		thread.start(); 
	
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
	}

	@Override
	protected void onResume() {
		super.onResume();
		makr.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		makr.pause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		makr.pause();

	}

	public void addFragment(Fragment f, int fragmentPosition) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(fragmentPosition, f);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		FrameLayout layout = (FrameLayout) findViewById(R.id.f2);

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

			if (f2V) {
				layout.setVisibility(View.GONE);
				f2V = false;
			} else {
				layout.setVisibility(View.VISIBLE);
				f2V = true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

		}

		return super.onKeyDown(keyCode, event);
	}



	

	@Override
	public void onStart() {
		super.onStart();
	}



	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + resultCode);
	}

}
