package com.zen.androidhtmleditor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zen.androidhtmleditor.AHEActivity.FetchSSLTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class Terminal extends Activity {
	
	private static int connectedTo = -1;
	public static String PREFS_NAME="DEVTOOLS_PREF";
	ProgressDialog dialog;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.terminal);
        
        
        Button send = (Button)findViewById(R.id.sendButton);
        
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	EditText command = (EditText)findViewById(R.id.send);
                String sendCommand = command.getText().toString();
            	
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
             	String currentServers = settings.getString("Accounts", "");
             	int connectedTo = settings.getInt("connectedTo", 0);
             	if(currentServers.equals("")){}else{
             		Gson gson = new Gson();
                    SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
                    List<Result> results = response.data;
                    Result l = results.get(connectedTo);
                    
                	if(l.serverName!="" && l.userName!="" && l.port!=""){
            	new FetchSSLTask(l.serverName, l.userName, l.passWord,"folder",sendCommand,l.sftp,l.port).execute();
             	}
             	}
             	command.setText("");
            }
        });
        
        
        
        
	}
	
	public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:

            	EditText command = (EditText)findViewById(R.id.send);
                String sendCommand = command.getText().toString();
            	
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
             	String currentServers = settings.getString("Accounts", "");
             	int connectedTo = settings.getInt("connectedTo", 0);
             	if(currentServers.equals("")){}else{
             		Gson gson = new Gson();
                    SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
                    List<Result> results = response.data;
                    Result l = results.get(connectedTo);
                    
                	if(l.serverName!="" && l.userName!="" && l.port!=""){
            	new FetchSSLTask(l.serverName, l.userName, l.passWord,"folder",sendCommand,l.sftp,l.port).execute();
             	}
             	}
            	
          return true;
        }
        return false;
    }
	public class FetchSSLTask extends AsyncTask<Void, Void, String> {

    	protected String mServer;
		protected String mUser;
		protected String mPass;
		protected String mType;
		protected String mPath;
		protected String mFTP;
		protected String mPort;
		
		protected FetchSSLTask(String server, String user, String pass, String type, String path, String sftp, String port) {
			mServer = server;
			mUser = user;
			mPass = pass;
			mType = type;
			mPath = path;
			mFTP = sftp;
			mPort = port;
		}	
		

		

		@Override
		protected String doInBackground(Void... v) {
			
			return getFeed(mServer, mUser, mPass, mType, mPath, mFTP, mPort);
			
		}

		private String getFeed(String server, String user, String pass, String type, String path, String sftp, String port) {
			
			String value = "";
			
			String aCommand = path;
			
			JSch jsch = new JSch();
	        Session session = null;
	        
	        InputStream in = null;
	        OutputStream out = null;
	        StringBuilder commandOut = new StringBuilder();
	        
	        try {
	            session = jsch.getSession(user, server, Integer.parseInt(port));
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.setPassword(pass);
	            session.connect();

	            Channel channel = session.openChannel("exec");
	            //System.out.println("Sending command: " + aCommand);
	            ((ChannelExec) channel).setCommand(aCommand);
	            channel.setInputStream(null);
	            ((ChannelExec) channel).setErrStream(System.err);
	            //commandOut.append(System.err.toString());
	            in = channel.getInputStream();
	            
	            channel.connect();
	            //ChannelSftp sftpChannel = (ChannelSftp) channel;
	            
	            byte[] tmp = new byte[1024];
	            while (true) {
	                while (in.available() > 0) {
	                    int i = in.read(tmp, 0, 1024);
	                    if (i < 0)break;
	                    //System.out.print(new String(tmp, 0, i));
	                    //System.out.println(channel.getInputStream().toString());
	                    commandOut.append(new String(tmp, 0, i));

	                    //setChanged();
	                    //notifyObservers(System.err.toString() + "\n");
	                }
	                if (channel.isClosed()) {
	                    System.out.println("exit-status: "
	                    + channel.getExitStatus());
	                    break;
	                }
	                try {
	                    Thread.sleep(1000);
	                } catch (Exception ee) {
	                    throw new JSchException("Cannot execute remote command: " + aCommand + " : " + ee.getMessage());
	                }
	            }
	            
	            
	            //sftpChannel.sendSignal(command);
	
	           
	            //sftpChannel.exit();
	            session.disconnect();
	        } catch (JSchException e) {
	            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
	        
	        return commandOut.toString();
	
			
			}
	       
		@Override
    	protected void onPreExecute() {
    		dialog = ProgressDialog.show(Terminal.this, "", 
                    "Connecting...", true);
    		
    	}
		protected void onPostExecute(String value){
			dialog.cancel();
			TextView result = (TextView)findViewById(R.id.terminalOut);
			String text = result.getText().toString();
			String publish = text+"\n"+value;
			result.setText(publish);
	    }
		
	}
	
	
}