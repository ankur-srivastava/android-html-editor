package com.zen.androidhtmleditor;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.zen.androidhtmleditor.R;


import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class Accounts extends ListActivity {
	public static String PREFS_NAME="DEVTOOLS_PREF";
	public static int pos =-1;
	Dialog dialog;
	
	ArrayList<String> servers=null;
	ArrayAdapter<String> r_adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  requestWindowFeature(Window.FEATURE_NO_TITLE);
	  setContentView(R.layout.accounts);
	  
	 
	 
	
     
     
	  
  	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
 	String currentServers = settings.getString("Accounts", "");
 	Log.i("currentServers",currentServers);
 	if(currentServers.equals("")){}else{
	Gson gson = new Gson();
    SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
    List<Result> results = response.data;
    servers = new ArrayList<String>();

    
    for(Result l:results){
    	servers.add(l.serverName);
    }
    r_adapter = new ArrayAdapter<String>(this, R.layout.account_list_item,servers);
    
    
    
    setListAdapter(r_adapter);
 	}
	  
	  
	 

	  ListView lv = getListView();

	  
	  lv.setTextFilterEnabled(true);

	  lv.setOnItemClickListener(new OnItemClickListener() {
	    
		  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			  dialog = new Dialog(Accounts.this);
			  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			  dialog.setContentView(R.layout.accountsdiag);
			  
			  dialog.setCancelable(true);
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		 	String currentServers = settings.getString("Accounts", "");
		 	if(currentServers.equals("")){}else{
			Gson gson = new Gson();
		    SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
		    List<Result> results = response.data;
		    pos = position;
		    Result l = results.get(position);
		    
			
		    EditText server = (EditText)dialog.findViewById(R.id.server);
		    EditText password = (EditText)dialog.findViewById(R.id.password);
		    EditText port = (EditText)dialog.findViewById(R.id.port);
		    EditText username = (EditText)dialog.findViewById(R.id.username);
		    Spinner sftp = (Spinner)dialog.findViewById(R.id.sftp);
		    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		            Accounts.this, R.array.ftpType_array, android.R.layout.simple_spinner_item);
		    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    sftp.setAdapter(adapter);
		    sftp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

		        public void onItemSelected(AdapterView<?> parent,
		            View view, int pos, long id) {
		          
		        }

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
		    });
		    
		    

		    if(l.sftp.equals("0")){
		    	sftp.setSelection(0);
		    }else if(l.sftp.equals("1")){
		    	sftp.setSelection(1);
		    }else if(l.sftp.equals("2")){
		    	sftp.setSelection(2);
		    }else if(l.sftp.equals("3")){
		    	sftp.setSelection(3);
		    }else{
		    	sftp.setSelection(0);
		    }
		    
		    server.setText(l.serverName);
		    username.setText(l.userName);
		    password.setText(l.passWord);
		    port.setText(l.port);
		    
		 	}

		 	
		 	Button closeServer = (Button)dialog.findViewById(R.id.closeServer);
		 	closeServer.setOnClickListener(new OnClickListener() {
		 		public void onClick(View v) {
		 			pos = -1;
		 			dialog.cancel();
		 		}
		 	});
		 	
		 	Button deleteServer = (Button)dialog.findViewById(R.id.deleteServer);
		 	deleteServer.setOnClickListener(new OnClickListener() {
		 		public void onClick(View v) {
		 			
		 			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				 	String currentServers = settings.getString("Accounts", "");
				 	if(currentServers.equals("")){}else{
					Gson gson = new Gson();
				    SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
				    List<Result> results = response.data;
				    String jSonStart = "{\"data\":[";
	            	
	            	String jSonEnd = "]}";
				    
	            	String jSonAccount = "";
	            	String newAccount = "";
	            	int i=0;
	            	for(Result l : results){
			        	if(i==pos){
			        
			        	}else{
			        		newAccount += "{\"server\":\""+l.serverName+"\",\"user\":\""+l.userName+"\",\"pass\":\""+l.passWord+"\",\"sftp\":\""+l.sftp+"\",\"port\":\""+l.port+"\"},";
			        	}
	            		i++;
	            		
	            	}
	            	newAccount = newAccount.substring(0, newAccount.length()-1);
			        jSonAccount = jSonStart+newAccount+jSonEnd;
			        SharedPreferences.Editor editor = settings.edit();
	    			editor.putString("Accounts", jSonAccount); 
	    			editor.commit();
	
	    		    
	    		    servers.remove(pos);

	    		    r_adapter.notifyDataSetChanged();

				 	}
		 			dialog.cancel();
		 			pos = -1;
		 		}
		 	});  
		 	
	
			  Button saveServer = (Button)dialog.findViewById(R.id.saveServer);
			  saveServer.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						
						SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
					 	String currentServers = settings.getString("Accounts", "");
					 	if(currentServers.equals("")){}else{
						Gson gson = new Gson();
					    SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
					    List<Result> results = response.data;
					    
					    EditText server = (EditText)dialog.findViewById(R.id.server);
					    EditText password = (EditText)dialog.findViewById(R.id.password);
					    EditText username = (EditText)dialog.findViewById(R.id.username);
					    EditText port = (EditText)dialog.findViewById(R.id.port);
					    Spinner sftp = (Spinner)dialog.findViewById(R.id.sftp);
					    long checked = sftp.getSelectedItemId();
					    
		            	String sftpField = String.valueOf(checked);
					    String serverField = server.getText().toString();
					    String passwordField = password.getText().toString();
					    String usernameField = username.getText().toString();
					    String portField = port.getText().toString();
					    
					    String jSonStart = "{\"data\":[";
		            	
		            	String jSonEnd = "]}";
					    
		            	String jSonAccount = "";
		            	
		            	String newAccount = "";
		            	int i=0;
		            	for(Result l : results){
				        	if(i==pos){
				        		newAccount += "{\"server\":\""+serverField+"\",\"user\":\""+usernameField+"\",\"pass\":\""+passwordField+"\",\"sftp\":\""+sftpField+"\",\"port\":\""+portField+"\"},";
				        	}else{
				        		newAccount += "{\"server\":\""+l.serverName+"\",\"user\":\""+l.userName+"\",\"pass\":\""+l.passWord+"\",\"sftp\":\""+l.sftp+"\",\"port\":\""+l.port+"\"},";
				        	}
		            		i++;
		            		
		            	}
		            	newAccount = newAccount.substring(0, newAccount.length()-1);
				        jSonAccount = jSonStart+newAccount+jSonEnd;
				        SharedPreferences.Editor editor = settings.edit();
		    			editor.putString("Accounts", jSonAccount); 
		    			editor.commit(); 
		    			dialog.cancel();
					 	}
						
					}
			  });
		 	
		 	
		 	
			  dialog.show();
		 	
		 	
		}
	  });
	  

	  
	}

	
}