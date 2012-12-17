package com.rentmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapMarkersHandler extends AsyncTask<MapView, Void, Void>{
	private Context context;
	
	MapMarkersHandler(Context arg) {
		context = arg;
	}
	
	@Override
	protected Void doInBackground(MapView... map) {
		this.addMarkersToMap(map[0]);
		
		return null;
	}
	
	private void addMarkersToMap(MapView map){
		JSONArray markers = getJSONMapMarkers();
		
		List<Overlay> mapOverlays = map.getOverlays();
		Drawable drawable = context.getResources().getDrawable(R.drawable.home);
		HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable);
		
		
		try{
			for (int i = 0; i < markers.length(); i++) {
				JSONObject JSONMarker = markers.getJSONObject(i);

				GeoPoint geoPoint = new GeoPoint((int)(JSONMarker.getDouble("latitude") * 1E6), (int)(JSONMarker.getDouble("longitude") * 1E6));				
				
				OverlayItem overlayitem = new OverlayItem(geoPoint, JSONMarker.getString("title"), JSONMarker.getString("content"));
		        
				itemizedoverlay.addOverlay(overlayitem);
				mapOverlays.add(itemizedoverlay);
		    }		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JSONArray getJSONMapMarkers(){
		String serverResponse = getMapMarkersFromServer();
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(serverResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonArray;
	}
    
	public String getMapMarkersFromServer(){
	    StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		
	    HttpGet httpGet = new HttpGet(PreferenceManager.getDefaultSharedPreferences(context).getString("pref_key_server_address", "")); 
	    
	    try {
	        HttpResponse response = client.execute(httpGet);
	        StatusLine statusLine = response.getStatusLine();
	        int statusCode = statusLine.getStatusCode();
        	if (statusCode == 200) {
        		HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
				    builder.append(line);
				}
	        } else {
	        	new AlertDialog.Builder(context).setTitle("Error!").setMessage("Can't connect to server!").setNeutralButton("Close", null).show(); 
	        }
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return builder.toString();
	}

}
