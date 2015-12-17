package com.zen.androidhtmleditor;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.zen.androidhtmleditor.R;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.os.Environment;

import android.view.View;
import android.view.Window;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ImportAccount extends Activity {
	public static String PREFS_NAME="DEVTOOLS_PREF";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.importaccount);
        
       
        
        
        
        
        Button saveServer = (Button)findViewById(R.id.saveServer);
        saveServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
           
            	
            	
            	
            	
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            	
            	String currentServers = settings.getString("Accounts", "");
            	
            	final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_id);
                if (checkBox.isChecked()) {
                    //checkBox.setChecked(false);
                	currentServers = "";
                }
     
                String xml = XMLfunctions.getXML();
                
               
                Document doc = XMLfunctions.XMLfromString(xml);
                
                
              
                
                if(doc!=null){

        		NodeList nodes = doc.getElementsByTagName("Server");

        		if((nodes.getLength() <= 0)){
                	Toast.makeText(ImportAccount.this, "No data in file", Toast.LENGTH_LONG).show();
                }
        		
        		String jSonAccount = "";
        		String jSonStart = "{\"data\":[";
            	String jSonEnd = "]}";
            	String newAccounts = "";
        		for (int i = 0; i < nodes.getLength(); i++) {
        			
        			Element e = (Element)nodes.item(i);
        			String newServerName = XMLfunctions.getValue(e, "Name");
        			String newServer = XMLfunctions.getValue(e, "Host");
        			String newUser = XMLfunctions.getValue(e, "User");
        			String newPass = XMLfunctions.getValue(e, "Pass");
        			String newPort = XMLfunctions.getValue(e, "Port");
        			if(i>0){
        				newAccounts += ",{\"accountName\":\""+newServerName+"\",\"server\":\""+newServer+"\",\"user\":\""+newUser+"\",\"pass\":\""+newPass+"\",\"sftp\":\"0\",\"port\":\""+newPort+"\"}";
        			}else{
        				newAccounts += "{\"accountName\":\""+newServerName+"\",\"server\":\""+newServer+"\",\"user\":\""+newUser+"\",\"pass\":\""+newPass+"\",\"sftp\":\"0\",\"port\":\""+newPort+"\"}";
        			}
        		
        		}	
                
        		if(currentServers.equals("")){
            		
            		jSonAccount = jSonStart+newAccounts+jSonEnd;
            		
            	}else{

            		Gson gson = new Gson();

			        SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);

			        List<Result> results = response.data;
            	
			        for(Result l : results){
			        	
			        	newAccounts += ",{\"accountName\":\""+l.accountName+"\",\"server\":\""+l.serverName+"\",\"user\":\""+l.userName+"\",\"pass\":\""+l.passWord+"\",\"sftp\":\""+l.sftp+"\",\"port\":\""+l.port+"\"}";
			        }
			        jSonAccount = jSonStart+newAccounts+jSonEnd;
            	}
        		
        		
            	
            	
            	
    			SharedPreferences.Editor editor = settings.edit();
    			editor.putString("Accounts", jSonAccount); 
    			editor.commit(); 
    			finish();
                }else{
                	
                	Toast.makeText(ImportAccount.this, "No data found. Please make sure your ahe_import.xml file is located at '"+Environment.getExternalStorageDirectory().getAbsolutePath()+"'", Toast.LENGTH_SHORT).show();
                	
                }
            }
        });
       
        
	}

	
	
	public Document XMLfromString(String xml){

		Document doc = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
		        is.setCharacterStream(new StringReader(xml));
		        doc = db.parse(is); 

			} catch (ParserConfigurationException e) {
				System.out.println("XML parse error: " + e.getMessage());
				return null;
			} catch (SAXException e) {
				System.out.println("Wrong XML file structure: " + e.getMessage());
	            return null;
			} catch (IOException e) {
				System.out.println("I/O exeption: " + e.getMessage());
				return null;
			}

	        return doc;

		}
	
	
}
