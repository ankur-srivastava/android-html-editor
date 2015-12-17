package com.zen.androidhtmleditor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.zen.androidhtmleditor.R;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ThemeList extends ListActivity{
	
	public static String PREFS_NAME="DEVTOOLS_PREF";
	ArrayAdapter<String> r_adapter;
	ProgressDialog sdialog;
	ArrayList<String> themes=null;
	public static String customThemes = "";
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.themelist);
	        
	        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	     	customThemes = settings.getString("Themes", "");
	     	Log.i("themes in list",customThemes);

	     		Gson gson = new Gson();
	     	    SearchResponse response = gson.fromJson(customThemes, SearchResponse.class);
	     	    List<Result> results = response.data;
	     	   themes = new ArrayList<String>();
	     	    for(Result l:results){
	     	    	
	     	    	themes.add(l.themeName);
	     	    }
	     	    r_adapter = new ArrayAdapter<String>(this, R.layout.themelistitems, themes);
	     	    setListAdapter(r_adapter);
	     	
	   
	     	
	     	ListView lv = getListView();
	  	  lv.setTextFilterEnabled(true);
	  	lv.setOnItemClickListener(new OnItemClickListener() {
		    
			  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				  SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			     	String customThemes = settings.getString("Themes", "");
				  Gson gson = new Gson();
				    SearchResponse response = gson.fromJson(customThemes, SearchResponse.class);
				    List<Result> results = response.data;
				   
				    Result l = results.get(position);
				    
				    Toast.makeText(ThemeList.this, "Theme changed to \""+l.themeName+"\"", Toast.LENGTH_SHORT).show();
				    SharedPreferences.Editor editor = settings.edit();
	    			editor.putString("selectedTheme", l.themeName); 
	    			editor.commit(); 
				    
			  }
	  	});
	  	
	  	lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	  	    public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
	  	    	final int position = pos;
	  	    	final CharSequence[] items = {"Share Theme", "Delete Theme", "Cancel"};
	  	    	AlertDialog.Builder builder = new AlertDialog.Builder(ThemeList.this);
	  	    	builder.setTitle("Manage Theme");
	  	    	builder.setItems(items, new DialogInterface.OnClickListener() {
	  	    		
	  	    		 public void onClick(DialogInterface dialog, int item) {
	  	    	        if(items[item].equals("Share Theme")){
	  	    	        	
	  	    	        	String themeString = "";
	  	    	        	   
	  	    	        	 SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	  				     	String customThemes = settings.getString("Themes", "");
	  					  Gson gson = new Gson();
	  					    SearchResponse response = gson.fromJson(customThemes, SearchResponse.class);
	  					    List<Result> results = response.data;
	  					    
	  					    Result l = results.get(position);
	  	    	        	   
	  					  themeString = "{";
	  					  themeString += "\"data\": [{";
	  					themeString += "\"themeName\": \""+l.themeName+"\",";
	  						themeString += "\"backgroundColor\": \""+Integer.toHexString(Integer.parseInt(l.backgroundColor))+"\",";
	  						themeString += "\"variableColor\": \""+Integer.toHexString(Integer.parseInt(l.variableColor))+"\",";
	  						themeString += "\"stringColor\": \""+Integer.toHexString(Integer.parseInt(l.stringColor))+"\",";
	  						themeString += "\"commentColor\": \""+Integer.toHexString(Integer.parseInt(l.commentColor))+"\",";
	  						themeString += "\"keywordColor\": \""+Integer.toHexString(Integer.parseInt(l.keywordColor))+"\",";
	  						themeString += "\"textColor\": \""+Integer.toHexString(Integer.parseInt(l.textColor))+"\"";
	  						themeString += "}]";
	  						themeString += "}";
	  	    	        	   
	  	    	        	 new MyShareTask(themeString).execute();
	  	    	        	
	  	    	        }else if(items[item].equals("Delete Theme")){
	  	    	        	
	  	    	        	String themeString = "";
	  	    	        	   
	  	    	        	 SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	  				     	String customThemes = settings.getString("Themes", "");
	  					  Gson gson = new Gson();
	  					    SearchResponse response = gson.fromJson(customThemes, SearchResponse.class);
	  					    List<Result> results = response.data;
	  					    

	  					  themeString = "{";
	  					  themeString += "\"data\": [";
	  					    
	  					    int i = 0;
	  					    for(Result l:results){
	  					    	if(i!=position){
	  					    	themeString += "{\"themeName\": \""+l.themeName+"\",";
		  						themeString += "\"backgroundColor\": \""+l.backgroundColor+"\",";
		  						themeString += "\"variableColor\": \""+l.variableColor+"\",";
		  						themeString += "\"stringColor\": \""+l.stringColor+"\",";
		  						themeString += "\"commentColor\": \""+l.commentColor+"\",";
		  						themeString += "\"keywordColor\": \""+l.keywordColor+"\",";
		  						themeString += "\"textColor\": \""+l.textColor+"\"";
		  						themeString += "},";
	  					    	}
	  					    	i++;
	  					    }
	  					    
	  					  themeString = themeString.substring(0, themeString.length() - 1);
	  					    
	  					  themeString += "]}";
	  					  
	  					SharedPreferences.Editor editor = settings.edit();
		    			editor.putString("Themes", themeString); 
		    			editor.commit();
	  					  
	  					themes.remove(position);
	  					  r_adapter.notifyDataSetChanged();
	  					  
	 
	  	    	        	
	  	    	        }else{
	  	    	        	dialog.cancel();
	  	    	        }
	  	    	    }
	  	    		
	  	    	});
	  	    	       
	  	    	AlertDialog alert = builder.create();
	  	    	alert.show();
	  	    		return true;
	  	    	
	  	    }
	  	});

	        
	 }
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.themelistitems, null);
				}
			TextView tt = (TextView) v.findViewById(R.id.themeName);
			Shader textShader2 = new LinearGradient(0, 0, 0, tt.getHeight(),
		             new int[]{Color.parseColor("#bccad3"),Color.parseColor("#f6fafc")},
		             new float[]{0, 1}, TileMode.CLAMP);
			tt.getPaint().setShader(textShader2);
			
			return v;
			
		}
		
		

		
public class ShareTask extends AsyncTask<Void, Void, String> {

	protected String mTheme;

	
	protected ShareTask(String theme) {
		mTheme = theme;
	}	
	
	@Override
	protected String doInBackground(Void... v) {
		return getFeed(mTheme);
		
		
	}

	private String getFeed(String theme) {
		
		//http://androidhtmleditor.com/sh.php?s=theme JSON STRING
		try {
			String query = URLEncoder.encode(theme, "utf-8");
		
		String fullurl = "http://androidhtmleditor.com/sh.php?s="+query;
		Log.i("query",query);
		DefaultHttpClient client = new DefaultHttpClient(); 
        
        HttpGet getRequest = new HttpGet(fullurl);
          
        try {
           
           HttpResponse getResponse = client.execute(getRequest);
           final int statusCode = getResponse.getStatusLine().getStatusCode();
           
           if (statusCode != HttpStatus.SC_OK) { 

              return null;
           }

           
           return theme;
           
        } 
        catch (IOException e) {
           getRequest.abort();
          
        }
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return theme;
		
       
    }	 
	
}



private class MyShareTask extends ShareTask {
	public MyShareTask(String theme) {
		super(theme);
	}
	
	@Override
	protected void onPreExecute() {
		sdialog = ProgressDialog.show(ThemeList.this, "", 
                "Sending...", true);
	}
	
	@Override
	protected void onPostExecute(String s) {
		sdialog.cancel();
		
    	
	}
	
}


}