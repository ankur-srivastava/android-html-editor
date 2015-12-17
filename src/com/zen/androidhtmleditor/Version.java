package com.zen.androidhtmleditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class Version extends AsyncTask<String, String, String> {
	
	protected String mServer;
	protected String mVersion;
	protected Context mContext;
	
	protected Version(Context context,String version,String server) {
		mServer = server;
		mVersion = version;
		mContext = context;
	}	
	
	@Override
	protected String doInBackground(String... params) {
		try {
			return getFeed(mVersion,mServer);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return "Error";
		
	}

	private String getFeed(String version, String server) throws Exception {
		
		BufferedReader in = null;
		String line = "";
		String versionNumber = "";
		try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(server));
            HttpResponse response = client.execute(request);
            in = new BufferedReader
            (new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            versionNumber = sb.toString();
           
            } finally {
            if (in != null) {
                try {
                    in.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    	
    	
    	
		return versionNumber;

       
    }


	@Override
	protected void onPostExecute(String version) {
		
		Log.i("Version",mVersion+" "+version);
		
		if(Float.parseFloat(mVersion)==Float.parseFloat(version)){
			
		}else{
			
		
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage("A new version is available. Do you want to download it?")
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			              
			        	   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://androidhtmleditor.com/update.php"));
			        	   mContext.startActivity(browserIntent);
			        	   dialog.dismiss();
			        	   
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
		
		
		
	}
	
	
}