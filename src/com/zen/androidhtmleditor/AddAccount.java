package com.zen.androidhtmleditor;

import java.util.List;



import com.google.gson.Gson;
import com.zen.androidhtmleditor.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddAccount extends Activity {
	public static String PREFS_NAME="DEVTOOLS_PREF";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addaccount);
        
       

        
        Spinner sftp = (Spinner)findViewById(R.id.sftp);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            AddAccount.this, R.array.ftpType_array, android.R.layout.simple_spinner_item);
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
	    
	    

	    
        
        
        Button saveServer = (Button)findViewById(R.id.saveServer);
        saveServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
           
            	
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            	
            	String currentServers = settings.getString("Accounts", "");
            	Log.i("Current",currentServers);
            	
            	
            	EditText newServerName = (EditText)findViewById(R.id.accountname);
            	
            	EditText newServer = (EditText)findViewById(R.id.server);
            	
            	EditText newUser = (EditText)findViewById(R.id.username);
            	
            	EditText newPass = (EditText)findViewById(R.id.password);
            	
            	EditText newPort = (EditText)findViewById(R.id.port);
            	
            	Spinner sftp = (Spinner)findViewById(R.id.sftp);
            	long checked = sftp.getSelectedItemId();
            	String portVal = newPort.getText().toString();
            	if(portVal.equals("")){
            		portVal = "21";
            	}
            	String newAccount = "{\"accountName\":\""+newServerName.getText().toString()+"\",\"server\":\""+newServer.getText().toString()+"\",\"user\":\""+newUser.getText().toString()+"\",\"pass\":\""+newPass.getText().toString()+"\",\"sftp\":\""+String.valueOf(checked)+"\",\"port\":\""+portVal+"\"}";
            	
            	String jSonStart = "{\"data\":[";
            	
            	String jSonEnd = "]}";
            	
            	String jSonAccount = "";
            	
	            	if(currentServers.equals("")){
	            		
	            		jSonAccount = jSonStart+newAccount+jSonEnd;
	            		
	            	}else{

	            		Gson gson = new Gson();
	
				        SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);

				        List<Result> results = response.data;
	            	
				        for(Result l : results){
				        	newAccount += ",{\"accountName\":\""+l.accountName+"\",\"server\":\""+l.serverName+"\",\"user\":\""+l.userName+"\",\"pass\":\""+l.passWord+"\",\"sftp\":\""+l.sftp+"\",\"port\":\""+l.port+"\"}";
				        }
				        jSonAccount = jSonStart+newAccount+jSonEnd;
	            	}
            	Log.i("Accounts",newAccount);
            	
    			SharedPreferences.Editor editor = settings.edit();
    			editor.putString("Accounts", jSonAccount); 
    			editor.commit(); 
    			finish();
            }
        });

         
        
	}
	
}