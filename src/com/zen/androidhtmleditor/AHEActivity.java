package com.zen.androidhtmleditor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.SocketException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.util.TrustManagerUtils;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zen.androidhtmleditor.R;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.zen.androidhtmleditor.LicenseChecker;
import com.zen.androidhtmleditor.LicenseCheckerCallback;

import com.zen.androidhtmleditor.ServerManagedPolicy;
import com.zen.androidhtmleditor.AESObfuscator;

import android.provider.Settings.Secure;

public class AHEActivity extends TabActivity {
	
	private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
	
    /** Called when the activity is first created. */
	public static String PREFS_NAME="DEVTOOLS_PREF";
	ProgressDialog dialog;
	ListView lstTest;
	ListView lstLocal;
	FetchAdapter arrayAdapter;
	FetchAdapter arrayLocalAdapter;
	private static int connectedTo = -1;
	public static String folderPath = "";
	Dialog ndialog;
	ArrayList<String[]> alrts=null;
	ArrayList<String[]> localalrts=null;
	private static final int BUFFER_SIZE = 1024 * 2;
	private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
	private static final byte[] SALT = new byte[] {
	     -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95,
	     -45, 77, -117, -36, -113, -11, 32, -64, 89
	     };
	
	private Handler mHandler;
	private int tabCount = 0;
	
	private boolean AppIsLicensed = true;
	private String localPath = "/";
	
	 private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1nJ47rsdV6GwVRvCdSpRPvZOe73W+Zwqvi2KEn53xjGIIwKooyEjzyZjP5omJWHl3oL6AgNAs6v9bZQodc/B4re6OWP4GbwkS1/sNW91ZnxKsIkHF1qSP14BweO8ttugkbk28FJEnC+HA4ZHH6F7KsH4MRS02ZoSoiyGGkh0DnfnCRw1+Sz++O/nsNuIpkqzVzOMtogYCkwK4tTF9dokMAHx1zB4n3FP2TcFcfRU4t5m7IBnJt7mOOsUzUPy3wgQv58ltmTCi5uJoDSrTyDRFzoXYjfv9XgdBckE0ZNZ8sRjO3nBzmJ8VHkavzTe6xulNUy2p7iJIPXnqsBFWXlZoQIDAQAB"; 
	
	public static String responseString = "";
	
	private static boolean isFinalized = false;
	private String deviceId; 
	private HorizontalScrollView hsv;
	private TabHost tabHost;
	private TabActivity tabactivity;
	
    @SuppressLint("NewApi") @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.main);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#4acab4"));
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Settings.init(settings);
       //Uncomment this for non Market installs. This will allow version checking.
        //new Version(this,getVersionName(this,DeveloperToolsActivity.class),"http://androidhtmleditor.com/version.php").execute();
        deviceId = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
        getOverflowMenu();
        getActionBar().setIcon(R.drawable.icon_white);
        
        tabactivity = (TabActivity)this;
        tabHost = tabactivity.getTabHost();
        hsv = (HorizontalScrollView)tabactivity.findViewById(R.id.topmenu);
        
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        mChecker = new LicenseChecker(
                this, new ServerManagedPolicy(this,
                    new AESObfuscator(SALT, getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY  // Your public licensing key.
                );
        
        mHandler = new Handler();
       // doCheck();
        
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.l1);
        
     // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle("File(s)");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Server");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        Button button1 = (Button)findViewById(R.id.button1);
        
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	onButtonClickEvent(v);
            }
        });
Button disconnect_button = (Button)findViewById(R.id.disconnect_button);
        
disconnect_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	disconnect();
            }
        });
        
        
        
       /* Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	hsv.setVisibility(View.GONE);
            	v.setVisibility(View.GONE);
            	RelativeLayout rl = (RelativeLayout)v.getParent();
            	Button button1 = (Button)findViewById(R.id.button1);
            	button1.setVisibility(View.VISIBLE);
            	Button button3 = (Button)findViewById(R.id.button3);
            	button3.setVisibility(View.VISIBLE);
            	
            	ImageView logo = (ImageView)findViewById(R.id.logo);
            	logo.setVisibility(View.VISIBLE);
            	
            	TextView slogan = (TextView)findViewById(R.id.appSlogan);
            	slogan.setVisibility(View.VISIBLE);
            	
            	ScrollView frontLayout = (ScrollView)findViewById(R.id.front);
            	frontLayout.setVisibility(View.VISIBLE);
            	
            	TextView appTitle = (TextView)findViewById(R.id.appTitle);
            	appTitle.setVisibility(View.VISIBLE);
            	
            	Button backButton = (Button)rl.findViewById(R.id.backButton);
            	backButton.setVisibility(View.GONE);
            	arrayAdapter.clear();
            	arrayAdapter.notifyDataSetChanged();
            	TextView pathInfo = (TextView)rl.findViewById(R.id.path);
                pathInfo.setText("");
                folderPath = "";
            	//new MyFetchTask("zenstudio.com.au", "zenstudi", ".-x$%Wmd5b#C","folder",folderPath).execute();
                connectedTo = -1;
                
                
                
                
                Toast.makeText(AHEActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });*/
          
        Button button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent SettingsIntent = new Intent(AHEActivity.this,Settings.class);
    			startActivity(SettingsIntent);
            }
        });
        
 
 
        
        /*Button backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String[] pathBits = folderPath.split("/");
            	folderPath = "";
            	for(int i=0;i<pathBits.length-1;i++){
            		folderPath += pathBits[i]+"/";
            	}
            	arrayAdapter.clear();
            	
            	//connectedTo
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
             	String currentServers = settings.getString("Accounts", "");
             	if(currentServers.equals("")){}else{
            	Gson gson = new Gson();
                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
                List<Result> results = response.data;
                Result l = results.get(connectedTo);
                
            	if(l.serverName!="" && l.userName!="" && l.port.trim()!=""){
            	
            		if(l.sftp.equals("0") || l.sftp.equals("1") || l.sftp.equals("2")){
            			new MyFetchTask(l.serverName, l.userName, l.passWord,"folder",folderPath,l.sftp,l.port).execute();
            		}else if(l.sftp.equals("3")){
            			new FetchSSLTask(l.serverName, l.userName, l.passWord,"folder",folderPath,l.sftp,l.port).execute();
            		}
            	
            	}
             	
             	}
            }
        });*/
        
        TextView pathInfo = (TextView)findViewById(R.id.path);
        pathInfo.setText(folderPath);
        
        lstTest = (ListView)findViewById(R.id.list); 
       // lstTest.setDividerHeight(10);
        lstTest.setPadding(0, 5, 0, 5);
        alrts = new ArrayList<String[]>();
        arrayAdapter = new FetchAdapter(AHEActivity.this, R.layout.listitems,alrts);
        lstTest.setAdapter(arrayAdapter);
        
        
        
        
        lstTest.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	  	    public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
	  	    	final View d = v;
	  	    	final CharSequence[] items = {"Delete", "Rename", "Chmod", "Download"};
	  	    	
	  	    	TextView t = (TextView)v.findViewById(R.id.fileFolderName);
	  	    	final String oldName = t.getText().toString();
	  	    	
	  	    	final int position = pos;
	  	    	
	  	    	AlertDialog.Builder builder = new AlertDialog.Builder(AHEActivity.this);
	  	    	builder.setTitle("Choose Action");
	  	    	builder.setItems(items, new DialogInterface.OnClickListener() {
	  	    	    public void onClick(DialogInterface dialog, int item) {
	  	    	    
	  	    	    	if(item==0){
	  	    	    		AlertDialog.Builder dbuilder = new AlertDialog.Builder(AHEActivity.this);
	  	    	    		dbuilder.setMessage("Delete this file?")
	 	  	    	       .setCancelable(false)
	 	  	    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	 	  	    	           public void onClick(DialogInterface dialog, int id) {
	 	  	    	        	   
	 	  	    	        	 deleteFile(d);
	 	  	    	        	 dialog.cancel();
	 	  	    	           }
	 	  	    	       })
	 	  	    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	 	  	    	           public void onClick(DialogInterface dialog, int id) {
	 	  	    	                dialog.cancel();
	 	  	    	             
	 	  	    	           }
	 	  	    	       });
	  	    	    		AlertDialog dalert = dbuilder.create();
	  	  	  	    		dalert.show();
	  	  	  	    		
	  	  	  	    		
	  	    	    	}else if(item==1){
	  	    	    		
	  	    	    		final Dialog renameDialog = new Dialog(AHEActivity.this);
	  	    	    		renameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	  	    	    		renameDialog.setContentView(R.layout.renamediag);

	  	    	    		renameDialog.setCancelable(true);

	  			    	
	  					  Button closeServer = (Button)renameDialog.findViewById(R.id.closeServer);
	  					 	closeServer.setOnClickListener(new OnClickListener() {
	  					 		public void onClick(View v) {
	  					 			
	  					 			renameDialog.cancel();
	  					 		}
	  					 	});
	  					 
	  					 	Button saveServer = (Button)renameDialog.findViewById(R.id.saveServer);
	  					 	saveServer.setOnClickListener(new OnClickListener() {
	  					 		public void onClick(View v) {
	  					 			
	  					 			EditText themeUrl = (EditText)renameDialog.findViewById(R.id.themeLink);
	  					 			String newName = themeUrl.getText().toString();
	  					 		//connectedTo
	  				            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	  				             	String currentServers = settings.getString("Accounts", "");
	  				             	if(currentServers.equals("")){}else{
	  				            	Gson gson = new Gson();
	  				                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
	  				                List<Result> results = response.data;
	  				                Result l = results.get(connectedTo);
	  				                
	  				            	if(l.serverName!="" && l.userName!="" && l.port.trim()!=""){
	  				           
	  				            		new RenameTask(l.serverName, l.userName, l.passWord, oldName, newName, folderPath , l.sftp, l.port, position).execute();
	  				            		
	  				            		renameDialog.cancel();
	  				            	}
	  					 		}
	  					 	}
	  					 	});
	  					 	renameDialog.show();
	  	    	    	}else if(item==2){
	  	    	    		
	  	    	    		final Dialog chmodDialog = new Dialog(AHEActivity.this);
	  	    	    		chmodDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	  	    	    		chmodDialog.setContentView(R.layout.chmoddiag);

	  	    	    		chmodDialog.setCancelable(true);

	  			    	
	  					  Button closeServer = (Button)chmodDialog.findViewById(R.id.closeServer);
	  					 	closeServer.setOnClickListener(new OnClickListener() {
	  					 		public void onClick(View v) {
	  					 			
	  					 			chmodDialog.cancel();
	  					 		}
	  					 	});
	  					 
	  					 	Button saveServer = (Button)chmodDialog.findViewById(R.id.saveServer);
	  					 	saveServer.setOnClickListener(new OnClickListener() {
	  					 		public void onClick(View v) {
	  					 			
	  					 			EditText themeUrl = (EditText)chmodDialog.findViewById(R.id.themeLink);
	  					 			String perms = themeUrl.getText().toString();
	  					 		//connectedTo
	  				            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	  				             	String currentServers = settings.getString("Accounts", "");
	  				             	if(currentServers.equals("")){}else{
	  				            	Gson gson = new Gson();
	  				                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
	  				                List<Result> results = response.data;
	  				                Result l = results.get(connectedTo);
	  				                
	  				            	if(l.serverName!="" && l.userName!="" && l.port.trim()!=""){
	  				            	
	  				            		 if(l.sftp.equals("0") || l.sftp.equals("1") || l.sftp.equals("2")){
	  				            			 new ChmodTask(l.serverName, l.userName, l.passWord, oldName, perms, folderPath , l.sftp, l.port, position).execute();
	  				            		 }else{
	  				            			 Toast.makeText(AHEActivity.this, "CHMOD could not be performed on your server via sftp", Toast.LENGTH_SHORT).show();
	  				            		 }
	  				            		chmodDialog.cancel();
	  				            	}
	  					 		}
	  					 	}
	  					 	});
	  					 	chmodDialog.show();
	  	    	    		
	  	    	    	}else if(item==3){
	  	    	    	
	  	    	    		//Make new class to download a file
	  	    	    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				             	String currentServers = settings.getString("Accounts", "");
				             	if(currentServers.equals("")){}else{
				            	Gson gson = new Gson();
				                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
				                List<Result> results = response.data;
				                Result l = results.get(connectedTo);
				                
				            	if(l.serverName!="" && l.userName!="" && l.port.trim()!=""){
				            	
				            		 if(l.sftp.equals("0") || l.sftp.equals("1") || l.sftp.equals("2")){
				            			 new DlTask(l.serverName, l.userName, l.passWord, oldName,  l.sftp, l.port).execute();
				            			
				            		 }
				            	}
				             	}
		    			}
		    				
	  	    	    
	  	    	    		
	  	    	    	
	  	    	    	
	  	    	    	
	  	    	    	//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	  	    	    
	  	    	    }
	  	    	});
	  	    	AlertDialog alert = builder.create();
	  	    	alert.show();
	  	    		return true;
	  	    	
	  	    }
	  	});
        
        lstTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	  	    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
	  	    	
	  	    	loadFileFolder(v);
	  	    	
	  	    }
        });
        
        
    }
 
    
    private void getOverflowMenu() {

        try {
           ViewConfiguration config = ViewConfiguration.get(this);
           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
           if(menuKeyField != null) {
               menuKeyField.setAccessible(true);
               menuKeyField.setBoolean(config, false);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }   
    private void displayResult(final String result) {
        mHandler.post(new Runnable() {
            public void run() {
                //mStatusText.setText(result);
                //setProgressBarIndeterminateVisibility(false);
                //mCheckLicenseButton.setEnabled(true);
            }
        });
    }
 
    private void doCheck() {
        //mCheckLicenseButton.setEnabled(false);
        //setProgressBarIndeterminateVisibility(true);
        //mStatusText.setText(R.string.checking_license);
        mChecker.checkAccess(mLicenseCheckerCallback);
    }
 
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChecker.onDestroy();
        
    }
    

    
    
   /* Uncomment this for non marketplace purchases
    * public static String getVersionName(Context context, Class cls) {
  	  try {
  	    ComponentName comp = new ComponentName(context, cls);
  	    PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
  	    return pinfo.versionName;
  	  } catch (android.content.pm.PackageManager.NameNotFoundException e) {
  	    return null;
  	  }
  	}  */
    
    public void addNewFile(String[] item){
    	alrts.add(item);
    }
    
    public void refreshView(){
    	arrayAdapter.notifyDataSetChanged();
    }
    
    public void onButtonClickEvent(View sender)
    {
        registerForContextMenu(sender); 
        openContextMenu(sender);
        unregisterForContextMenu(sender);
    }
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
    	super.onCreateContextMenu(menu, v, menuInfo);  
    	menu.setHeaderTitle("Accounts");

     SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
 	
 	String currentServers = settings.getString("Accounts", "");
     if(currentServers.equals("")){
    	 menu.add(0, v.getId(), 0, "No Accounts");	 
     }else{
     Gson gson = new Gson();
     SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
     List<Result> results = response.data;
	int menuCount = 0;
     for(Result l : results){
    	 String accName = l.serverName;
    	 if(l.accountName!=null && l.accountName.length() > 0){
    		 accName = l.accountName;
    	 }
    	 menu.add(menuCount, v.getId(), menuCount, accName);
    	 menuCount++;
     }
     
     }
    //menu.add(0, v.getId(), 0, "zenstudio.com.au");  
     //menu.add(0, v.getId(), 0, "Action 2");  
 }  
    
    @Override  
    public boolean onContextItemSelected(MenuItem item) {  
    	if(AppIsLicensed==false){
    		return false;
    	}
    	
            if(item.getTitle().toString()=="No Accounts"){
            	return false;
            }
  
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
         	String currentServers = settings.getString("Accounts", "");
         	if(currentServers.equals("")){
         		return false;
         	}else{
            Gson gson = new Gson();
            SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
            List<Result> results = response.data;
            
            	Result l = results.get(item.getOrder());
            	
            	
            	if(l.serverName!="" && l.userName!="" && l.port.trim()!=""){
            			connectedTo = item.getOrder();
            			
            			SharedPreferences.Editor editor = settings.edit();
            			editor.putInt("connectedTo", connectedTo);
            			editor.commit(); 
            			
            			if(l.sftp.equals("0") || l.sftp.equals("1") || l.sftp.equals("2")){
            				new MyFetchTask(l.serverName, l.userName, l.passWord,"folder",folderPath,l.sftp,l.port).execute();
            			}else if(l.sftp.equals("3")){
            				new FetchSSLTask(l.serverName, l.userName, l.passWord,"folder",folderPath,l.sftp,l.port).execute();
            			}
            			return true;
            	}else{
            		return false;
            	}
            	
         	}
      
    }   
    public class FetchTask extends AsyncTask<Void, String, FTPFile[]> {

    	protected String mServer;
		protected String mUser;
		protected String mPass;
		protected String mType;
		protected String mPath;
		protected String mFTP;
		protected String mPort;
		
		protected FetchTask(String server, String user, String pass, String type, String path, String sftp, String port) {
			mServer = server;
			mUser = user;
			mPass = pass;
			mType = type;
			mPath = path;
			mFTP = sftp;
			mPort = port;
		}	
		


		@Override
		protected FTPFile[] doInBackground(Void... v) {
			
			return getFeed(mServer, mUser, mPass, mType, mPath, mFTP, mPort);
			
		}
		
		protected void onPostExecute(String set) {
			TextView connectLog = (TextView)findViewById(R.id.connectLog);
			connectLog.setVisibility(View.GONE);
		}
		
		protected void onProgressUpdate(String... values) {
			Button discon = (Button)findViewById(R.id.backButton);
			int vis = discon.getVisibility();
			//if(vis==0){}else{
			TextView connectLog = (TextView)findViewById(R.id.connectLog);
			connectLog.setVisibility(View.VISIBLE);
			String log = connectLog.getText().toString();
			String newLog = values[0]+"\n"+log;
			connectLog.setText(newLog);
			//}
	     }

		private FTPFile[] getFeed(String server, String user, String pass, String type, String path, String sftp, String port) {
			
		String logString = "";
			
			FTPFile[] filesArr = null;
			FTPClient con = new FTPClient();
			con.setControlKeepAliveTimeout(60);
			if(!port.equals("")){
				con.setDefaultPort(Integer.parseInt(port));
			}
			if(sftp.equals("1")){ //Explicit over TSL
				con = new FTPSClient(false);
				con.setControlKeepAliveTimeout(60);
				if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
					logString = con.getReplyString();
					 this.publishProgress(con.getReplyString());
				}
				
			}else if(sftp.equals("2")){ //Implicit over TSL
				
				con = new FTPSClient(true);
				con.setControlKeepAliveTimeout(60);
				if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
					logString = con.getReplyString();
					 this.publishProgress(con.getReplyString());
				}
				con.setDefaultPort(990);
				if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
					logString = con.getReplyString();
					 this.publishProgress(con.getReplyString());
				}
			}
			con.setDefaultTimeout(30000);
			if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
				logString = con.getReplyString();
				 this.publishProgress(con.getReplyString());
			}
			con.setConnectTimeout(30000);
			if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
				logString = con.getReplyString();
				 this.publishProgress(con.getReplyString());
			}
			
	    	try
	    	{
	    	    con.connect(server);
	    	    con.setKeepAlive(true);
	    	    if(sftp.equals("2")){
	    	    	
	
	    	    
	    	    }
	    	    responseString = con.getReplyString();
	    	    if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
	    	    	logString = con.getReplyString();
					 this.publishProgress(con.getReplyString());
				}
	    	    
	    	    
	    	    if (con.login(user, pass))
	    	    {
	    	    	if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
	    	    		logString = con.getReplyString();
						 this.publishProgress(con.getReplyString());
					}
	    	        con.enterLocalPassiveMode(); // important!
	    	        
	    	        filesArr = con.listFiles(path);
	    	        if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
	    	        	logString = con.getReplyString();
						 this.publishProgress(con.getReplyString());
					}
	    	    }else{
	    	    	connectedTo = -1;
	    	    	responseString = con.getReplyString();
	    	    	if(!logString.equals(con.getReplyString()) && con.getReplyString()!=null){
	    	    		logString = con.getReplyString();
						 this.publishProgress(con.getReplyString());
					}
	    	    }
	    	}
	    	catch (Exception e)
	    	{
	    	    e.printStackTrace();
	    	}


	    	try
	    	{
	    	    con.logout();
	    	    con.disconnect();
	    	}
	    	catch (IOException e)
	    	{
	    	    e.printStackTrace();
	    	}
	    	
			
	    	
			return filesArr;
			}
	       
	    
		
	}

    public class DlTask extends AsyncTask<Void, Void, String> {

    	protected String mServer;
		protected String mUser;
		protected String mPass;
		protected String mType;
		protected String mFile;
		protected String mPort;
		protected String mFtp;
		
		
		protected DlTask(String server, String user, String pass, String file,String sftp, String port) {
			mServer = server;
			mUser = user;
			mPass = pass;
			mFile = file;
			mPort = port;
			mFtp = sftp;
		}	
		
		@Override
    	protected void onPreExecute() {
    		dialog = ProgressDialog.show(AHEActivity.this, "", 
                    "Downloading File...", true);
    	}
		
		@Override
    	protected void onPostExecute(String set) {
			Toast.makeText(AHEActivity.this, "The file has been downloaded to you SDCard", Toast.LENGTH_SHORT).show();
    		dialog.cancel();
    		
		}
		
		@Override
		protected String doInBackground(Void... v) {
			return getFeed(mServer, mUser, mPass, mFile, mFtp, mPort);
			
		}

		private String getFeed(String server, String user, String pass, String file, String sftp, String port) {
			
			
			FTPClient con = new FTPClient();
			con.setDefaultPort(Integer.parseInt(port));
			
			if(sftp.equals("1")){ //Explicit over TSL
				con = new FTPSClient(false);
				Log.i("FTPS","Explicit");
			}else if(sftp.equals("2")){ //Implicit over TSL
				
				con = new FTPSClient(true);
				con.setDefaultPort(990);
				Log.i("FTPS","Implicit");
			}
			
			con.setDefaultTimeout(90000);
    		con.setConnectTimeout(90000);
			String total = "";
			try
	    	{
	    	    con.connect(server);
	    	    if (con.login(user, pass))
	    	    {
	    	        con.enterLocalPassiveMode(); // important!
	    	       
	    	        
	    	        InputStream inStream = con.retrieveFileStream(file);
	    	        String fType = "downloadable";
	 
	    	       
	    				
	    				File sdCard = Environment.getExternalStorageDirectory();

	    				String[] fn = file.split("/");
	    				File fname = new File(sdCard, fn[fn.length-1]);

	    				FileOutputStream f = new FileOutputStream(fname);
	    				byte[] buffer = new byte[BUFFER_SIZE];
	    				int n = 0;
	    				try {
	                        while ((n = inStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
	                                f.write(buffer, 0, n);
	                                
	                        }
	                        f.flush();
	                } finally {
	                        try {
	                                f.close();
	                        } catch (IOException e) {
	                               
	                        }
	                        try {
	                        	inStream.close();
	                        } catch (IOException e) {
	                               
	                        }
	                }
	    				file = fn[fn.length-1];
	    			
	    	       
	    	    }
	    	}
	    	catch (Exception e)
	    	{
	    	    e.printStackTrace();
	    	}


	    	try
	    	{
	    	    con.logout();
	    	    con.disconnect();
	    	}
	    	catch (IOException e)
	    	{
	    	    e.printStackTrace();
	    	}
	    	
	    	
	    	
			return total;

	       
	    }	 
		
	}
    
    public static boolean in_array(FTPFile[] haystack, String needle) {
        for(int i=0;i<haystack.length;i++) {
            if(haystack[i].toString().equals(needle)) {
                return true;
            }
        }
        return false;
    }
	///////////////////
	
	private class MyFetchTask extends FetchTask {
    	public MyFetchTask(String url, String username, String token, String type, String path, String sftp, String port) {
    		super(url, username, token,type,path,sftp, port);
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		//dialog = ProgressDialog.show(AHEActivity.this, "", 
            //        "Connecting...", true);
    		TextView connectLog = (TextView)findViewById(R.id.connectLog);
			connectLog.setVisibility(View.VISIBLE);
    	}
    	
    	
    	
    	@Override
    	protected void onPostExecute(FTPFile[] set) {
    		//dialog.cancel();
    		
    		TextView pathInfo = (TextView)findViewById(R.id.path);
            pathInfo.setText(folderPath);
            Button backButton = (Button)findViewById(R.id.backButton);
            hsv.setVisibility(View.VISIBLE);
            
            
            
            if(folderPath==""){
            	backButton.setVisibility(View.GONE);
            }else{
            	backButton.setVisibility(View.VISIBLE);
            }
            
            String disconn[] = {"Disconnect","action"}; 
            String upup[] = {"../","action"};
            alrts.add(disconn);
            alrts.add(upup);
            
    		if(set!=null){
    			
    			for(FTPFile s : set)
				{
    				if(s.isDirectory()){
    					mType = "folder";
    				
    				String[] name = s.toString().split(" ");
					//Log.i(mType, name[(name.length-1)]+","+mType);
    				
					String values[] = {name[(name.length-1)],mType};  	
					if(!name[(name.length-1)].equals(".") && !name[(name.length-1)].equals("..")){
						alrts.add(values);
					}
					}
				}
    			for(FTPFile s : set)
				{
    				if(s.isFile()){
    					mType = "file";
    				
    				String[] name = s.toString().split(" ");
					//Log.i(mType, s.toString());
					String values[] = {name[(name.length-1)],mType,name[(name.length-6)]};  
					if(!name[(name.length-1)].equals(".") && !name[(name.length-1)].equals("..")){
						alrts.add(values);
					}
    				}
				}
    			
    		
    		
    		arrayAdapter.notifyDataSetChanged();
    		
    		
    		if(set.length==0){
    			Toast.makeText(AHEActivity.this, "No Results "+responseString, Toast.LENGTH_LONG).show();
    		}
    		
    		if(mType.equals("folder")){
    			//new MyFetchTask("zenstudio.com.au", "zenstudi", ".-x$%Wmd5b#C","files",folderPath).execute();
    		}
    		}
    		
    		TextView connectLog = (TextView)findViewById(R.id.connectLog);
    		connectLog.setVisibility(View.GONE);
    		if(set!=null){
    			
            	//connectLog.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout));
    			
                //connectLog.setText("");
    		Button button1 = (Button)findViewById(R.id.button1);
        	button1.setVisibility(View.GONE);
        	//Button button2 = (Button)findViewById(R.id.button2);
        	//button2.setVisibility(View.VISIBLE);
        	
        	Button button3 = (Button)findViewById(R.id.button3);
        	button3.setVisibility(View.GONE);
    		
        //	ImageView logo = (ImageView)findViewById(R.id.logo);
        	//logo.setVisibility(View.GONE);

        	
        	ScrollView frontLayout = (ScrollView)findViewById(R.id.front);
        	frontLayout.setVisibility(View.GONE);
        	

    		}
        	if(connectedTo<0){
            	Toast.makeText(AHEActivity.this, responseString, Toast.LENGTH_SHORT).show();
            	disconnect();
            }
        	
    	}
    	
    }


	public void deleteFile(View v){
		View rl=v;
		TextView typeId = (TextView)rl.findViewById(R.id.typeId);
		TextView fileFolderName = (TextView)rl.findViewById(R.id.fileFolderName);
		String name = fileFolderName.getText().toString();
		String type = typeId.getText().toString();
		int i=0;
		int pos=0;
		String file = "";
			for(String[] s:alrts){
				if(s[0].equals(name) && s[1].equals(type)){
					Log.i("List","Removing "+s[0]+"="+name+","+s[1]+"="+type);
					file = s[0];
					pos = i;
					
					break;
					//alrts.remove(i);
				}
				i++;
			
			}
			//Log.i("remove file",folderPath+file);
			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			String currentServers = settings.getString("Accounts", "");
	        int connectedTo = settings.getInt("connectedTo", -1);
	        if(connectedTo!=-1){
	        	if(currentServers.equals("")){}else{
	            	Gson gson = new Gson();
	                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
	                List<Result> results = response.data;
	                Result s = results.get(connectedTo);
	                
	            	if(s.serverName!="" && s.userName!="" && s.port.trim()!=""){
	            		
	            		new DeleteFileFolderTask(s.serverName, s.userName, s.passWord, type, file, s.sftp, s.port, pos).execute();
	            		
	            	}
	        	}
	        }
	        
			//arrayAdapter.notifyDataSetChanged();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        
	    	//Button discon = (Button)findViewById(R.id.backButton);
	    	//int visibility = discon.getVisibility();
	    	
	    	if(folderPath.length() > 1 && connectedTo>-1){
	    		
	    		String[] pathBits = folderPath.split("/");
            	folderPath = "";
            	for(int i=0;i<pathBits.length-1;i++){
            		folderPath += pathBits[i]+"/";
            	}
            	
            	arrayAdapter.clear();
            	
            	//connectedTo
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
             	String currentServers = settings.getString("Accounts", "");
             	if(currentServers.equals("")){}else{
            	Gson gson = new Gson();
                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
                List<Result> results = response.data;
                Result l = results.get(connectedTo);
            	if(l.serverName!="" && l.userName!="" && l.port.trim()!=""){
            		if(l.sftp.equals("0") || l.sftp.equals("1") || l.sftp.equals("2")){
            			new MyFetchTask(l.serverName, l.userName, l.passWord,"folder",folderPath, l.sftp,l.port).execute();
            			
            		}else if(l.sftp.equals("3")){
            			
            			new FetchSSLTask(l.serverName, l.userName, l.passWord,"folder",folderPath, l.sftp,l.port).execute();
            		}
            	
            	}
             	}
	    		
	    	}else if(connectedTo>-1){
		    		disconnect();
	    	}else{
	    		
	    		finish();
	    	}
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void disconnect(){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Disconnect?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		       		//newfile.setEnabled(false);
		       		//newfolder.setEnabled(false);
		       		hsv.setVisibility(View.GONE);
		           	//Button button2 = (Button)findViewById(R.id.button2);
		           	//button2.setVisibility(View.GONE);
		           	
		           	Button button1 = (Button)findViewById(R.id.button1);
		           	button1.setVisibility(View.VISIBLE);
		           	Button button3 = (Button)findViewById(R.id.button3);
		           	button3.setVisibility(View.VISIBLE);
		           	Button disconnect_button = (Button)findViewById(R.id.disconnect_button);
		           	disconnect_button.setVisibility(View.GONE);
		           	
		           //	ImageView logo = (ImageView)findViewById(R.id.logo);
		           //	logo.setVisibility(View.VISIBLE);

		           	ScrollView frontLayout = (ScrollView)findViewById(R.id.front);
		           	frontLayout.setVisibility(View.VISIBLE);
		           	
	
		           	
		           	Button backButton = (Button)findViewById(R.id.backButton);
		           	backButton.setVisibility(View.GONE);
		           	arrayAdapter.clear();
		           	arrayAdapter.notifyDataSetChanged();
		           	TextView pathInfo = (TextView)findViewById(R.id.path);
		               pathInfo.setText("");
		               folderPath = "";
		           	//new MyFetchTask("zenstudio.com.au", "zenstudi", ".-x$%Wmd5b#C","folder",folderPath).execute();
		               connectedTo = -1;
		               SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		               SharedPreferences.Editor editor = settings.edit();
		       		editor.putInt("connectedTo", connectedTo);
		       		editor.commit();
		               
		               Toast.makeText(AHEActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
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
	
	public void loadFileFolder(View v){
		
		//RelativeLayout rl = (RelativeLayout)v.getParent();
		//RelativeLayout rl = (RelativeLayout)v;
		View rl=v;
		TextView typeId = (TextView)rl.findViewById(R.id.typeId);
		TextView fileFolderName = (TextView)rl.findViewById(R.id.fileFolderName);
		
		if(typeId.getText().toString().equals("folder")){
			arrayAdapter.clear();
			folderPath = folderPath+fileFolderName.getText().toString()+"/";
			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
         	String currentServers = settings.getString("Accounts", "");
         	if(currentServers.equals("")){}else{
        	Gson gson = new Gson();
            SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
            List<Result> results = response.data;
            Result l = results.get(connectedTo);
        	if(l.serverName!="" && l.userName!="" && l.port.trim()!=""){
        		if(l.sftp.equals("0") || l.sftp.equals("1") || l.sftp.equals("2")){
        			new MyFetchTask(l.serverName, l.userName, l.passWord,"folder",folderPath,l.sftp,l.port).execute();
        		}else if(l.sftp.equals("3")){
        			new FetchSSLTask(l.serverName, l.userName, l.passWord,"folder",folderPath,l.sftp,l.port).execute();
        		}
        	}
         	}
			//new MyFetchTask("zenstudio.com.au", "zenstudi", ".-x$%Wmd5b#C","folder",folderPath).execute();
		}else{
			
			//tabCount++;
			
			
			
			
			int tabNum = tabHost.getTabWidget().getChildCount();
			
			for(int i=0;i<tabNum;i++){
				
				Button b = (Button)tabHost.getTabWidget().getChildAt(i);
				//b.setBackgroundResource(R.drawable.tab_buttons_off);
			}
			
			
			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			//editor.putString("fileName", folderPath+fileFolderName.getText().toString()); 
			editor.putInt("connectedTo", connectedTo);
			editor.commit(); 
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			Button tview=new Button(this); 
			tview.setLayoutParams(lp);
			tview.setText(fileFolderName.getText().toString());
			tview.setTextColor(Color.WHITE);
			//tview.setBackgroundResource(R.drawable.tab_buttons_on);
			
			Intent editFileIntent = new Intent(AHEActivity.this,EditFile.class);
			editFileIntent.putExtra("fileName", folderPath+fileFolderName.getText().toString());
			
			
			
			
			
			
			
			
			TabHost.TabSpec spec = tabHost.newTabSpec(folderPath+fileFolderName.getText().toString())
		    		.setIndicator(tview)
		    		.setContent(editFileIntent);
		    
		    tabHost.addTab(spec);
		    tabHost.setCurrentTab(tabNum);
		    tabHost.refreshDrawableState();
		   
		    
		    
		    //Log.i("TabCount",String.valueOf(tabCount));
			//Intent editFileIntent = new Intent(AHEActivity.this,EditFile.class);
			//startActivity(editFileIntent);
			//Intent editFileIntent = new Intent(AHEActivity.this,TabLoader.class);
			//startActivity(editFileIntent);
			
		}
		Log.i("clicked",typeId.toString());
	}
    
	public void website(View v){
		
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://androidhtmleditor.com"));
		startActivity(browserIntent);
		
	}
	
	public void uploadFile(){
		
		
		ndialog = new Dialog(AHEActivity.this);
		ndialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ndialog.setContentView(R.layout.uploadfilediag);
		ndialog.setCancelable(true);
		
		Button lb = (Button)ndialog.findViewById(R.id.localBackButton);
  		
		
		
		 lstLocal = (ListView)ndialog.findViewById(R.id.list); 
	       // lstTest.setDividerHeight(10);
		 lstLocal.setPadding(0, 5, 0, 5);
	        
		
		File sdCardRoot = Environment.getExternalStorageDirectory();
		File yourDir = new File(sdCardRoot, localPath);
		localalrts = new ArrayList<String[]>();
		
		for (File f : yourDir.listFiles()) {
		    if (f.isDirectory()){
		    	String values[] = {f.getName(),"folder"};  
		    	localalrts.add(values);
		        //Log.i("localDirectory",f.getName());
		    }
		   
		}	
		
		for (File f : yourDir.listFiles()) {
		    if (f.isFile()){
		        //Log.i("localFile",f.getName());
		    	String values[] = {f.getName(),"file"};  
		    	localalrts.add(values);
		    }
		   
		}

	
		//localPath = localPath+"/"+fn+"/";
		lb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String[] localPathBits = localPath.split("/");
            	localPath = "";
            	for(int i=0;i<localPathBits.length-1;i++){
            		localPath += localPathBits[i]+"/";
            	}
            	TextView lp = (TextView)ndialog.findViewById(R.id.localPath);
  	    		lp.setText(localPath);
            	uploadFileList();
            }
		});
		
        arrayLocalAdapter = new FetchAdapter(AHEActivity.this, R.layout.listitems,localalrts);
        lstLocal.setAdapter(arrayLocalAdapter);
        
        
        
		
        lstLocal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	  	    public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
	  	    	TextView type = (TextView)v.findViewById(R.id.typeId);
	  	    	String t = type.getText().toString();
	  	    	
	  	    	TextView fileFolder = (TextView)v.findViewById(R.id.fileFolderName);
	  	    	String fn = fileFolder.getText().toString();
	  	    	
	  	    	if(t.equals("folder")){
	  	    		
	  	    		localPath = localPath+fn+"/";
	  	    		TextView lp = (TextView)ndialog.findViewById(R.id.localPath);
	  	    		lp.setText(localPath);
	  	    		Button lb = (Button)ndialog.findViewById(R.id.localBackButton);
	  	    		
	  	    		if(localPath.equals("/")){
	  	  			lb.setVisibility(View.GONE);
	  	    		}else{
	  	  			lb.setVisibility(View.VISIBLE);
	  	  			
	  	    		}
	  	    		uploadFileList();
	  	    	}
	  	    	if(t.equals("file")){
	  	    		
	  	    			
	  	    		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		 			String currentServers = settings.getString("Accounts", "");
		 	        int connectedTo = settings.getInt("connectedTo", -1);
		 	        
		 	        if(connectedTo!=-1){
		 	        	if(currentServers.equals("")){}else{
		 	            	Gson gson = new Gson();
		 	                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
		 	                List<Result> results = response.data;
		 	                Result s = results.get(connectedTo);
		 	            	if(s.serverName!="" && s.userName!=""){
		 	            		String local = localPath+fn;
		 	            		String remote = folderPath+fn;
		 	            		new LocalUploadTask(s.serverName, s.userName, s.passWord, local, remote, s.sftp, s.port, 0).execute();
		 	       	  	    	ndialog.dismiss();
		 	       	  	    	
		 	            	}
		 	        	}
		 	        }
	  	    		
	  	    	}
	  	    	//loadFileFolder(v);
	  	    	
	  	    }
        });
        
		ndialog.show();
		
	}
	
	public void uploadFileList(){
		
		arrayLocalAdapter.clear();
		File sdCardRoot = Environment.getExternalStorageDirectory();
		File yourDir = new File(sdCardRoot, localPath);
		
		
		for (File f : yourDir.listFiles()) {
		    if (f.isDirectory()){
		    	String values[] = {f.getName(),"folder"};  
		    	localalrts.add(values);
		        Log.i("localDirectory",f.getName());
		    }
		   
		}	
		
		for (File f : yourDir.listFiles()) {
		    if (f.isFile()){
		        Log.i("localFile",f.getName());
		    	String values[] = {f.getName(),"file"};  
		    	localalrts.add(values);
		    }
		   
		}
		arrayLocalAdapter.notifyDataSetChanged();
	}
	
	public void newFileFolder(int type){
		
		final int fileType = type;
		
		ndialog = new Dialog(AHEActivity.this);
		ndialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ndialog.setContentView(R.layout.newfilediag);

		ndialog.setCancelable(true);

  	
		  Button closeServer = (Button)ndialog.findViewById(R.id.closeServer);
		 	closeServer.setOnClickListener(new OnClickListener() {
		 		public void onClick(View v) {
		 			
		 			ndialog.cancel();
		 		}
		 	});
		
		 	Button saveServer = (Button)ndialog.findViewById(R.id.saveServer);
		 	saveServer.setOnClickListener(new OnClickListener() {
		 		public void onClick(View v) {
		 			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		 			String currentServers = settings.getString("Accounts", "");
		 	        int connectedTo = settings.getInt("connectedTo", -1);
		 	        
		 	        if(connectedTo!=-1){
		 	        	if(currentServers.equals("")){}else{
		 	            	Gson gson = new Gson();
		 	                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
		 	                List<Result> results = response.data;
		 	                Result s = results.get(connectedTo);
		 	            	if(s.serverName!="" && s.userName!=""){
		 	            		new NewFileFolderTask(s.serverName, s.userName, s.passWord, fileType, folderPath, s.sftp, s.port).execute();
		 			
		 	            	}
		 	        	}
		 	        }
		 			
		 			
		 		}
		 	});
		 	ndialog.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    isFinalized = true;
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
	    if (isFinalized);
	    	//Button backButton = (Button)findViewById(R.id.button2);
		 	//int vis = backButton.getVisibility();
		 	
		 	MenuItem newfile = menu.findItem(R.id.newfile);
		 	MenuItem newfolder = menu.findItem(R.id.newfolder);
		 	MenuItem uploadfile = menu.findItem(R.id.uploadfile);
		 	MenuItem terminal = menu.findItem(R.id.terminal);
		 	
		 	if(connectedTo>-1){
		 		newfile.setEnabled(true);
		 		newfolder.setEnabled(true);
		 		uploadfile.setEnabled(true);
		 		terminal.setEnabled(true);
		 	}else{
		 		newfile.setEnabled(false);
		 		newfolder.setEnabled(false);
		 		uploadfile.setEnabled(false);
		 		terminal.setEnabled(false);
		 	}
	    		
		 	return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
	    switch (item.getItemId()) {
	        case R.id.newfile:
	            //newGame();
	         
	    	    
	    	    	newFileFolder(0);
	    	    
	        	
	        	//save(l);
	            return true;
	        case R.id.newfolder:
	        	
	    	    	newFileFolder(1);
	    	    
	        	//saveAs(l);
	            return true;
	        
	        case R.id.uploadfile:
	        
	        	uploadFile();
	        
	        return true;
	        
	        case R.id.terminal:
	        	
	        	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
             	String currentServers = settings.getString("Accounts", "");
             	int ct = settings.getInt("connectedTo", -1);
             	Log.i("connectedTo",String.valueOf(ct));
	        	if(ct>-1){
	        		
	             	if(currentServers.equals("")){}else{
	            	Gson gson = new Gson();
	                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
	                List<Result> results = response.data;
	                Result l = results.get(connectedTo);
	        		if(l.sftp.equals("3")){
	        			Intent TerminalIntent = new Intent(AHEActivity.this,Terminal.class);
    					startActivity(TerminalIntent);
	        		}else{
	        			Toast.makeText(AHEActivity.this, "Not Connected with SFTP", Toast.LENGTH_SHORT).show();
	        		}
	             	}
	             }else{
	        		Toast.makeText(AHEActivity.this, "Not Connected To Server", Toast.LENGTH_SHORT).show();
	        	
	        	}
	       	return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
	
	
	public class FetchSSLTask extends AsyncTask<Void, String, String> {

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
			
			String fullPath = "/"+path;
			
            String disconn[] = {"Disconnect","action"}; 
            String upup[] = {"../","action"};
            alrts.add(disconn);
            alrts.add(upup);
			
			JSch jsch = new JSch();
	        Session session = null;
	        try {
	            session = jsch.getSession(user, server, Integer.parseInt(port));
	            
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.setPassword(pass);
	            session.connect();
	            
	            Channel channel = session.openChannel("sftp");
	           

	            channel.connect();
	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            //sftpChannel.get("remotefile.txt", "localfile.txt");
	            Vector filelist = sftpChannel.ls(fullPath);
	            
	            Iterator itr = filelist.iterator();	  
	            
	            while(itr.hasNext()){
	            	String fn = itr.next().toString();
	            	Log.i("fp",fn);
	            	String[] name = fn.split(" ");
	            	
	            	if(fn.startsWith("d")){
	            	String values[] = {name[(name.length-1)],"folder"};  	
					if(!name[(name.length-1)].equals(".") && !name[(name.length-1)].equals("..")){
						alrts.add(values);
					}
					
	            	}
		            
	            }
	            Iterator itr2 = filelist.iterator();
	    
	            while(itr2.hasNext()){
	            	String fn = itr2.next().toString();
	            	//Log.i("fp",fn);
	            	String[] name = fn.split(" ");
	            	
	            	if(fn.startsWith("-")){
	            	String values[] = {name[(name.length-1)],"file"};  	
					if(!name[(name.length-1)].equals(".") && !name[(name.length-1)].equals("..")){
						alrts.add(values);
					}
					
	            	}
	            }
	            value="con";
	            sftpChannel.exit();
	            session.disconnect();
	       // }
	        } catch (JSchException e) {
	            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	        } catch (SftpException e) {
	            e.printStackTrace();
	        } 
			
	        
			return value;
	
			
			}
	       

		
		protected void onProgressUpdate(String... values) {
			Button discon = (Button)findViewById(R.id.backButton);
			//int vis = discon.getVisibility();
			//if(vis==0){}else{
			TextView connectLog = (TextView)findViewById(R.id.connectLog);
			connectLog.setVisibility(View.VISIBLE);
			String log = connectLog.getText().toString();
			String newLog = values[0]+"\n"+log;
			connectLog.setText(newLog);
			//}
	     }
		
		@Override
    	protected void onPreExecute() {
    		//dialog = ProgressDialog.show(AHEActivity.this, "", 
            //        "Connecting...", true);
			TextView connectLog = (TextView)findViewById(R.id.connectLog);
			connectLog.setVisibility(View.VISIBLE);
			connectLog.setText("connecting...");
    	}
		protected void onPostExecute(String value){
			TextView connectLog = (TextView)findViewById(R.id.connectLog);
			connectLog.setVisibility(View.GONE);
			
			//dialog.cancel();
			if(value.equals("con")){
			TextView pathInfo = (TextView)findViewById(R.id.path);
            pathInfo.setText(folderPath);
            
            //Button backButton = (Button)findViewById(R.id.backButton);
            hsv.setVisibility(View.VISIBLE);
            
           // if(folderPath==""){
            //	backButton.setVisibility(View.GONE);
           // }else{
          // // 	backButton.setVisibility(View.VISIBLE);
           // }
			
			Button button1 = (Button)findViewById(R.id.button1);
        	button1.setVisibility(View.GONE);
        	//Button button2 = (Button)findViewById(R.id.button2);
        	//button2.setVisibility(View.VISIBLE);
        	
        	Button button3 = (Button)findViewById(R.id.button3);
        	button3.setVisibility(View.GONE);
        	Button disconnect_button = (Button)findViewById(R.id.disconnect_button);
        	disconnect_button.setVisibility(View.VISIBLE);
        	
    		
        	//ImageView logo = (ImageView)findViewById(R.id.logo);
        	//logo.setVisibility(View.GONE);
        	
        	ScrollView frontLayout = (ScrollView)findViewById(R.id.front);
        	frontLayout.setVisibility(View.GONE);

        	ListView list = (ListView)findViewById(R.id.list);
        	list.setVisibility(View.VISIBLE);
        	
        
		}
        	arrayAdapter.notifyDataSetChanged();
	    }
		
	}
	
	//async newfile or folder
	public class NewFileFolderTask extends AsyncTask<Void, Void, String[]> {

    	protected String mServer;
		protected String mUser;
		protected String mPass;
		protected int mType;
		protected String mPath;
		protected String mFTP;
		protected String mPort;
		
		protected NewFileFolderTask(String server, String user, String pass, int type, String path, String sftp, String port) {
			mServer = server;
			mUser = user;
			mPass = pass;
			mType = type;
			mPath = path;
			mFTP = sftp;
			mPort = port;
		}	
		


		@Override
		protected String[] doInBackground(Void... v) {
			
			return getFeed(mServer, mUser, mPass, mType, mPath, mFTP, mPort);
			
		}

		private String[] getFeed(String server, String user, String pass, int type, String path, String sftp, String port) {
			String[] result = new String[2];
			int fileType = type;
			
			EditText themeUrl = (EditText)ndialog.findViewById(R.id.themeLink);
		 			
		 			String file = themeUrl.getText().toString();
		 			
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String currentServers = settings.getString("Accounts", "");
        int connectedTo = settings.getInt("connectedTo", -1);
        boolean exists = false;
        if(connectedTo!=-1){
        	if(currentServers.equals("")){}else{
            	Gson gson = new Gson();
                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
                List<Result> results = response.data;
                Result s = results.get(connectedTo);
            	if(s.serverName!="" && s.userName!=""){
            		
            		if(s.sftp.equals("3")){
	            		
            			JSch jsch = new JSch();
            	        Session session = null;
            	        try {
            	            session = jsch.getSession(s.userName, s.serverName, Integer.parseInt(s.port));
            	            session.setConfig("StrictHostKeyChecking", "no");
            	            session.setPassword(s.passWord);
            	            session.connect();

            	            Channel channel = session.openChannel("sftp");
            	            channel.connect();
            	            ChannelSftp sftpChannel = (ChannelSftp) channel;
            	            if(fileType==1){
            	            	Vector filelist = sftpChannel.ls("/"+folderPath.substring(0, folderPath.length()-1));
            	            	Iterator itr = filelist.iterator();	            
            	 	            while(itr.hasNext()){
            	 	            	String fn = itr.next().toString();
            	 	            	
            	 	            	String[] name = fn.split(" ");
            	 	            	
            	 	            	if(fn.startsWith("d")){
            	 	            		if(name[(name.length-1)].equals(file)){
            	 	            			
            	 	            		exists = true;
            	 	            		}
            	 	            		
            	 	            	
            	 	            	}
            	 	            }
								if(exists){
								
								result[0] = "folderError";
       	    		        	result[1] = file;
								}
            	            	if(!exists){
            	            		//Toast.makeText(AHEActivity.this, "Folder Created", Toast.LENGTH_SHORT).show();
    	 	            			sftpChannel.mkdir("/"+folderPath+file);
    	 	            			String[] newfile = {file,"folder"};
	       	    		        	alrts.add(newfile);
									
									result[0] = "folderTrue";
	       	    		        	result[1] = file;
            	            	}
            	            	
            	           
            	            
            	            
            	            }else if(fileType==0){
            	            	
            	            	 Vector filelist = sftpChannel.ls("/"+folderPath.substring(0, folderPath.length()-1));
            	 	            
            	 	            Iterator itr = filelist.iterator();	            
            	 	            while(itr.hasNext()){
            	 	            	String fn = itr.next().toString();
            	 	            	
            	 	            	String[] name = fn.split(" ");
            	 	            	
            	 	            	if(fn.startsWith("-")){
            	 	            		if(name[(name.length-1)].equals(file)){
 
    	    	    	    				exists = true;
            	 	            		}
            	 	            		
            	 	            	
            	 	            	}
            	 	            }
            	 	            if(exists){
									
									result[0] = "fileError";
	       	    		        	result[1] = file;
								}
            	 	           if(!exists){
       	    		        	String text = " "; //blank file
       	    		        	InputStream inputStream;
								try {
									inputStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
									sftpChannel.put(inputStream, "/"+folderPath+file);
									String[] newfile = {file,"file"};
	       	    		        	alrts.add(newfile);
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
       	    		        	result[0] = "fileTrue";
       	    		        	result[1] = file;
       	    	                
       	    		        }
            	 	            
            	            	
            	            }
            	            
            	            sftpChannel.exit();
            	            session.disconnect();
            	        } catch (JSchException e) {
            	            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            	        } catch (SftpException e) {
            	            e.printStackTrace();
            	        }
            			
            		}else{
            		
            		FTPClient con = new FTPClient();
            		con.setDefaultTimeout(90000);
            		con.setConnectTimeout(90000);
            		
            		try {
        	        	//boolean transType = con.setFileType(FTP.ASCII_FILE_TYPE);
    					con.connect(s.serverName);
    				    if (con.login(s.userName, s.passWord))
    	    		    {
    	    		        con.enterLocalPassiveMode(); // important!
    	    		        
    	    		       
    	    		        if(fileType==1){
    	    		        	
    	    		        	if(con.makeDirectory(folderPath+file)){
    	    		        		
    	    		        		String[] newfile = {file,"folder"};
        	    		        	alrts.add(newfile);
									result[0] = "folderTrue";
									result[1] = file;
    	    		        	}else{
    	    		        		result[0] = "folderError";
    	    		        		result[1] = file;
    	    		        	}
    	    		        	
    	    		        	
    	    		        }else if(fileType==0){
    	    		        	
    	    		        	FTPFile[] filesArr;
    	    		        	filesArr = con.listFiles(folderPath);
    	    		        	for(FTPFile serverFile : filesArr)
    	    					{
    	    		        		String[] name = serverFile.toString().split(" ");
    	    		        		//Log.i("filename",name[name.length-1].toString()+" "+file);
    	    		        		if(name[name.length-1].toString().equals(file)){
	    	    	    				if(serverFile.isFile()){	
	    	    	    				
	    	    	    				exists = true;
	    	    	    				}
    	    		        		}
    	    					}  	    		        	
    	    		        	
								if(!exists){
									result[0] = "fileError";
									result[1] = file;
								}
								
    	    		        	if(!exists){
    	    		        	String text = " "; //blank file
    	    		        	InputStream inputStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
    	    	                OutputStream outputStream = con.storeFileStream(folderPath+file);
    	    	                byte[] buffer = new byte[4096];
    	    	                int lf;
    	    	                while((lf = inputStream.read(buffer))!=-1)
    	    	                     {
    	    	                      outputStream.write(buffer, 0, lf);
    	    	                  }
    	    	                inputStream.close();
    	    	                outputStream.flush();
    	    	                outputStream.close();
    	    	                String[] newfile = {file,"file"};
    	    		        	alrts.add(newfile);
								result[0] = "fileTrue";
								result[1] = file;
    	    		        }
    	    		        }
    	    		        
    	    		    }
    					
    				} catch (SocketException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            		}
            	}
        	}
        }
        
        
        
			
	
			return result;
			
			}
	       
		@Override
    	protected void onPreExecute() {
    		dialog = ProgressDialog.show(AHEActivity.this, "", 
                    "Connecting...", true);
    		
    	}
		protected void onPostExecute(String[] result){
			dialog.cancel();
			ndialog.cancel();
			if(result[0].equals("folderError")){
				Toast.makeText(AHEActivity.this, "ERROR: Could not create folder. Check Server Log.", Toast.LENGTH_SHORT).show();
			}else if(result[0].equals("folderTrue")){
				Toast.makeText(AHEActivity.this, "Folder Created", Toast.LENGTH_SHORT).show();
			}else if(result[0].equals("fileError")){
				Toast.makeText(AHEActivity.this, "File \""+result[1]+"\" already exists", Toast.LENGTH_SHORT).show();
			}else if(result[0].equals("fileTrue")){
				Toast.makeText(AHEActivity.this, "File Created", Toast.LENGTH_SHORT).show();
			}else{
			
			
			}
			
			arrayAdapter.notifyDataSetChanged();
	    }
		
	}
	
	
	public class LocalUploadTask extends AsyncTask<Void, Void, String[]> {

    	protected String mServer;
		protected String mUser;
		protected String mPass;
		protected String mLocal;
		protected String mRemote;
		protected String mFTP;
		protected String mPort;
		protected int mPos;
		
		protected LocalUploadTask(String server, String user, String pass, String local, String remote, String sftp, String port, int pos) {
			mServer = server;
			mUser = user;
			mPass = pass;
			mLocal = local;
			mRemote = remote;
			mFTP = sftp;
			mPort = port;
			mPos = pos;
		}	
		


		@Override
		protected String[] doInBackground(Void... v) {
			
			return getFeed(mServer, mUser, mPass, mLocal, mRemote, mFTP, mPort,mPos);
			
		}

		private String[] getFeed(String server, String user, String pass, String local, String remote, String sftp, String port, int pos) {
			

			String[] result = new String[2];
			 result[0] = "Error";
	         result[1] = "Error";			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			String currentServers = settings.getString("Accounts", "");
	        int connectedTo = settings.getInt("connectedTo", -1);
	       
	        if(connectedTo!=-1){
	        	if(currentServers.equals("")){}else{
	            	Gson gson = new Gson();
	                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
	                List<Result> results = response.data;
	                Result s = results.get(connectedTo);
	            	if(s.serverName!="" && s.userName!=""){
			if(s.sftp.equals("3")){
	            		
	            			JSch jsch = new JSch();
	            	        Session session = null;
	            	        try {
	            	            session = jsch.getSession(s.userName, s.serverName, Integer.parseInt(s.port));
	            	            session.setConfig("StrictHostKeyChecking", "no");
	            	            session.setPassword(s.passWord);
	            	            session.connect();

	            	            Channel channel = session.openChannel("sftp");
	            	            channel.connect();
	            	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            	            
	            	            File sdCardRoot = Environment.getExternalStorageDirectory();
	            	            File f = new File(sdCardRoot+local);
	            	            InputStream src;
								try {
									src = new FileInputStream(f);
									sftpChannel.put(src, "/"+remote);
									 result[0] = "OK";
			    	    		        result[1] = local;
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
 		        
	    	    		       
	    	    		        
	            	            sftpChannel.exit();
	            	            session.disconnect();
	            	        } catch (JSchException e) {
	            	            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	            	        } catch (SftpException e) {
	            	            e.printStackTrace();
	            	        }
	            			
	            		}else{
	            		FTPClient con = new FTPClient();
			
	            		try {
	        	        	//boolean transType = con.setFileType(FTP.ASCII_FILE_TYPE);
	    					con.connect(s.serverName);
	    				    if (con.login(s.userName, s.passWord))
	    	    		    {
	    	    		        con.enterLocalPassiveMode(); // important!
	    	    		        
	    	    		       
	    	    		        File sdCardRoot = Environment.getExternalStorageDirectory();
	    	    		        InputStream input;
	    	    		        OutputStream output;
	    	    		        File f = new File(sdCardRoot+local);
	    	    		        double bytes = f.length();
	    	    		        if(bytes<1048576){
	    	    		        try {
									String dataType = TestAscii.main(f);
									if(dataType.equals("ASCII")){
										con.setFileType(FTPClient.ASCII_FILE_TYPE);
										
									}else{
										con.setFileType(FTPClient.BINARY_FILE_TYPE);
										
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	    	    		        }else{
	    	    		        	con.setFileType(FTPClient.BINARY_FILE_TYPE);
	    	    		        	
	    	    		        }
	    	    		        
	    	    		        
	    	    		        
	    	    		       
	    	    		        input  = new FileInputStream(sdCardRoot+local);
	    	    		        output = con.storeFileStream(remote);
	    	    		        /*if(!FTPReply.isPositiveIntermediate(con.getReplyCode())) {
	    	    		            input.close();
	    	    		            output.close();
	    	    		            con.logout();
	    	    		            con.disconnect();
	    	    		            System.err.println("File transfer failed.");
	    	    		            Log.i("ecode",String.valueOf(con.getReplyCode()));
	    	    		            //System.exit(1);
	    	    		            result[0] = "Error";
		    	    		         result[1] = "Error";
	    	    		        }*/
	    	    		        Util.copyStream(input, output);
	    	    		        result[0] = "OK";
	    	    		        result[1] = local;
	    	    		        input.close();
	    	    		        output.close();
	    	    		        

	    	    		        
	    	    		       /* try {
	    	    		        	
	    	    		        	File sdCardRoot = Environment.getExternalStorageDirectory();
		    	    		        File file = new File(sdCardRoot+local);
		    	    		        in = new FileInputStream(file);
	    	    		        }
	    	    		         finally {
	    	    		          if (in != null) {
	    	    		            in.close();
	    	    		          }
	    	    		        }
	    	    		         if(in!=null){
	    	    		        	 
	    	    		        	 
	    	    		         try{
	    	    		        	 con.storeFile(remote, in);
	    	    		        	 
	    	    		         }catch(IOException e){
	    	    		        	 
	    	    		        	 e.printStackTrace();
	    	    		         }
	    	    		        
	    	    		         result[0] = "OK";
	    	    		         result[1] = "OK";
	    	    		         }else{
	    	    		        	 result[0] = "Error";
		    	    		         result[1] = "Error";
	    	    		         }*/
	    	    		        
	    	    		      
	    	    		        
	    	    		    }
	    					
	    				} catch (SocketException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				} catch (IOException e) {
	    					
	    					// TODO Auto-generated catch block
	    					//e.printStackTrace();
	    				}
	            	}
	            	}
	        	}
	        }
	
			return result;
			
			}
	       
		@Override
    	protected void onPreExecute() {
    		dialog = ProgressDialog.show(AHEActivity.this, "", 
                    "Uploading...", true);
    		
    	}
		protected void onPostExecute(String[] result){
			dialog.cancel();
			//ndialog.cancel();
			if(result[0].equals("Error")){
				Toast.makeText(AHEActivity.this, "Upload Fail", Toast.LENGTH_SHORT).show();
			}else if(result[0].equals("OK")){
				Toast.makeText(AHEActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
		        String[] uploadedFileNameParts = result[1].split("/");
		        String uploadedFileName = uploadedFileNameParts[uploadedFileNameParts.length-1];
		        String values[] = {uploadedFileName,"file"};  
		        
		        alrts.add(values);
		        arrayAdapter.notifyDataSetChanged();
			}else{
			
			
			}
			
			
	    }
		
	}
	
	public class DeleteFileFolderTask extends AsyncTask<Void, Void, String[]> {

    	protected String mServer;
		protected String mUser;
		protected String mPass;
		protected String mType;
		protected String mPath;
		protected String mFTP;
		protected String mPort;
		protected int mPos;
		
		protected DeleteFileFolderTask(String server, String user, String pass, String type, String path, String sftp, String port, int pos) {
			mServer = server;
			mUser = user;
			mPass = pass;
			mType = type;
			mPath = path;
			mFTP = sftp;
			mPort = port;
			mPos = pos;
		}	
		


		@Override
		protected String[] doInBackground(Void... v) {
			
			return getFeed(mServer, mUser, mPass, mType, mPath, mFTP, mPort,mPos);
			
		}

		private String[] getFeed(String server, String user, String pass, String type, String path, String sftp, String port, int pos) {
			String[] result = new String[2];
			String file = path;
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			String currentServers = settings.getString("Accounts", "");
	        int connectedTo = settings.getInt("connectedTo", -1);
	        boolean exists = false;
	        if(connectedTo!=-1){
	        	if(currentServers.equals("")){}else{
	            	Gson gson = new Gson();
	                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
	                List<Result> results = response.data;
	                Result s = results.get(connectedTo);
	            	if(s.serverName!="" && s.userName!=""){
			if(s.sftp.equals("3")){
	            		
	            			JSch jsch = new JSch();
	            	        Session session = null;
	            	        try {
	            	            session = jsch.getSession(s.userName, s.serverName, Integer.parseInt(s.port));
	            	            session.setConfig("StrictHostKeyChecking", "no");
	            	            session.setPassword(s.passWord);
	            	            session.connect();

	            	            Channel channel = session.openChannel("sftp");
	            	            channel.connect();
	            	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            	            
	            	            if(type.equals("file")){
	            	            	sftpChannel.rm("/"+folderPath+file);
	    	    		        	//Toast.makeText(AHEActivity.this, file+" deleted", Toast.LENGTH_SHORT).show();
	    	    		        	result[0] = "fileTrue";
										result[1] = file;
									alrts.remove(pos);
	    	    		        }else if(type.equals("folder")){
	    	    		        	sftpChannel.rmdir("/"+folderPath+file);
	    	    		        		//Toast.makeText(AHEActivity.this, file+" deleted", Toast.LENGTH_SHORT).show();
	    	    		        		result[0] = "folderTrue";
										result[1] = file;
										alrts.remove(pos);
	    	    		        	
	    	    		        }
	            	            
	            	            sftpChannel.exit();
	            	            session.disconnect();
	            	        } catch (JSchException e) {
	            	            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	            	        } catch (SftpException e) {
	            	            e.printStackTrace();
	            	        }
	            			
	            		}else{
	            		FTPClient con = new FTPClient();
			
	            		try {
	        	        	//boolean transType = con.setFileType(FTP.ASCII_FILE_TYPE);
	    					con.connect(s.serverName);
	    				    if (con.login(s.userName, s.passWord))
	    	    		    {
	    	    		        con.enterLocalPassiveMode(); // important!
	    	    		        
	    	    		        
	    	    		        if(type.equals("file")){
	    	    		        	con.deleteFile(folderPath+file);
	    	    		        	//Toast.makeText(AHEActivity.this, file+" deleted", Toast.LENGTH_SHORT).show();
	    	    		        	result[0] = "fileTrue";
									result[1] = file;
									alrts.remove(pos);
	    	    		        }else if(type.equals("folder")){
	    	    		        	
	    	    		        	if(con.removeDirectory(folderPath+file)){
	    	    		        		//Toast.makeText(AHEActivity.this, file+" deleted", Toast.LENGTH_SHORT).show();
	    	    		        		result[0] = "folderTrue";
										result[1] = file;
										alrts.remove(pos);
	    	    		        	}else{
									result[0] = "folderError";
									result[1] = file;
	    	    		        		//Toast.makeText(AHEActivity.this, "ERROR: Folder not deleted. Make sure it is empty.", Toast.LENGTH_SHORT).show();
	    	    		        	}
	    	    		        
	    	    		        }
	    	    		      
	    	    		        
	    	    		    }
	    					
	    				} catch (SocketException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	            	}
	            	}
	        	}
	        }
	
			return result;
			
			}
	       
		@Override
    	protected void onPreExecute() {
    		dialog = ProgressDialog.show(AHEActivity.this, "", 
                    "Connecting...", true);
    		
    	}
		protected void onPostExecute(String[] result){
			dialog.cancel();
			//ndialog.cancel();
			if(result[0].equals("folderError")){
				Toast.makeText(AHEActivity.this, "Folder could not be deleted", Toast.LENGTH_SHORT).show();
			}else if(result[0].equals("folderTrue")){
				Toast.makeText(AHEActivity.this, "Folder Deleted", Toast.LENGTH_SHORT).show();
			}else if(result[0].equals("fileError")){
				Toast.makeText(AHEActivity.this, "File could not be deleted", Toast.LENGTH_SHORT).show();
			}else if(result[0].equals("fileTrue")){
				Toast.makeText(AHEActivity.this, "File Deleted", Toast.LENGTH_SHORT).show();
			}else{
			
			
			}
			
			arrayAdapter.notifyDataSetChanged();
	    }
		
	}
	
	public class RenameTask extends AsyncTask<Void, Void, String[]> {

    	protected String mServer;
		protected String mUser;
		protected String mPass;
		protected String mOrigName;
		protected String mNewName;
		protected String mPath;
		protected String mFTP;
		protected String mPort;
		protected int mPos;
		
		protected RenameTask(String server, String user, String pass, String origName, String newName,String path, String sftp, String port, int pos) {
			mServer = server;
			mUser = user;
			mPass = pass;
			mOrigName = origName;
			mNewName = newName;
			mPath = path;
			mFTP = sftp;
			mPort = port;
			mPos = pos;
		}	
		


		@Override
		protected String[] doInBackground(Void... v) {
			
			return getFeed(mServer, mUser, mPass, mOrigName, mNewName,mPath, mFTP, mPort,mPos);
			
		}

		private String[] getFeed(String server, String user, String pass, String origName, String newName,String path, String sftp, String port, int pos) {
			String[] result = new String[2];
			//String oldName = path;
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			String currentServers = settings.getString("Accounts", "");
	        int connectedTo = settings.getInt("connectedTo", -1);
	        Log.i("connectedTo",String.valueOf(connectedTo));
	        boolean exists = false;
	        if(connectedTo!=-1){
	        	if(currentServers.equals("")){}else{
	            	Gson gson = new Gson();
	                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
	                List<Result> results = response.data;
	                Result s = results.get(connectedTo);
	            	if(s.serverName!="" && s.userName!=""){
			if(s.sftp.equals("3")){
	            		
	            			JSch jsch = new JSch();
	            	        Session session = null;
	            	        try {
	            	            session = jsch.getSession(s.userName, s.serverName, Integer.parseInt(s.port));
	            	            session.setConfig("StrictHostKeyChecking", "no");
	            	            session.setPassword(s.passWord);
	            	            session.connect();

	            	            Channel channel = session.openChannel("sftp");
	            	            channel.connect();
	            	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            	            
	            	           
	            	            	sftpChannel.rename("/"+path+"/"+origName, "/"+path+"/"+newName);
	            	            	
	            	            	String[] item = alrts.get(pos);
		    	    		        
			    	    		       alrts.remove(pos);
			    	    		       String[] newItem = {newName,item[1]};
			    	    		       alrts.add(pos, newItem);
	    	    		       
	            	            
	            	            sftpChannel.exit();
	            	            session.disconnect();
	            	        } catch (JSchException e) {
	            	            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	            	        } catch (SftpException e) {
	            	            e.printStackTrace();
	            	        }
	            			
	            		}else{
	            		FTPClient con = new FTPClient();
			
	            		try {
	        	        	//boolean transType = con.setFileType(FTP.ASCII_FILE_TYPE);
	    					con.connect(s.serverName);
	    				    if (con.login(s.userName, s.passWord))
	    	    		    {
	    	    		        con.enterLocalPassiveMode(); // important!
	    	    		        
	    	    		        
	    	    		        if(con.rename(path+"/"+origName, path+"/"+newName)){
	    	    		        
		    	    		       String[] item = alrts.get(pos);
		    	    		        
		    	    		       alrts.remove(pos);
		    	    		       String[] newItem = {newName,item[1]};
		    	    		       alrts.add(pos, newItem);
	    	    		        }
	    	    		    }
	    					
	    				} catch (SocketException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	            	}
	            	}
	        	}
	        }
	
			return result;
			
			}
	       
		@Override
    	protected void onPreExecute() {
    		dialog = ProgressDialog.show(AHEActivity.this, "", 
                    "Connecting...", true);
    		
    	}
		protected void onPostExecute(String[] result){
			dialog.cancel();
			//ndialog.cancel();
			
			
			arrayAdapter.notifyDataSetChanged();
	    }
		
	}
	
	
	
	public class ChmodTask extends AsyncTask<Void, Void, String[]> {

    	protected String mServer;
		protected String mUser;
		protected String mPass;
		protected String mOrigName;
		protected String mPerms;
		protected String mPath;
		protected String mFTP;
		protected String mPort;
		protected int mPos;
		
		protected ChmodTask(String server, String user, String pass, String origName,String perms, String path, String sftp, String port, int pos) {
			mServer = server;
			mUser = user;
			mPass = pass;
			mOrigName = origName;
			mPerms = perms;
			mPath = path;
			mFTP = sftp;
			mPort = port;
			mPos = pos;
		}	
		


		@Override
		protected String[] doInBackground(Void... v) {
			
			return getFeed(mServer, mUser, mPass, mOrigName, mPerms, mPath, mFTP, mPort,mPos);
			
		}

		private String[] getFeed(String server, String user, String pass, String origName, String perms, String path, String sftp, String port, int pos) {
			String[] result = new String[2];
			//String oldName = path;
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			String currentServers = settings.getString("Accounts", "");
	        int connectedTo = settings.getInt("connectedTo", -1);
	        boolean exists = false;
	        if(connectedTo!=-1){
	        	if(currentServers.equals("")){}else{
	            	Gson gson = new Gson();
	                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
	                List<Result> results = response.data;
	                Result s = results.get(connectedTo);
	            	if(s.serverName!="" && s.userName!=""){
			if(s.sftp.equals("3")){
	            		
	            			/*JSch jsch = new JSch();
	            	        Session session = null;
	            	        
	            	        
	            	        
	            	        
	            	        try {
	            	            session = jsch.getSession(s.userName, s.serverName, Integer.parseInt(s.port));
	            	            session.setConfig("StrictHostKeyChecking", "no");
	            	            session.setPassword(s.passWord);
	            	            session.connect();

	            	            Channel channel = session.openChannel("sftp");
	            	            channel.connect();
	            	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            	            
	            	            
	            	            
	            	            	//sftpChannel.rename("/"+path+"/"+origName, "/"+path+"/"+newName);
	            	            try {
	            	            	
	            	            	//sftpChannel.chmod(, "/"+path+"/"+origName);
	            	            	//sftpChannel.chmod(644, "/"+path+"/"+origName);
									//int permission = Integer.parseInt(perms);
									//Log.i("perms",String.valueOf(permission));
									//if(perms.startsWith("0")){
										//sftpChannel.chmod(, "/"+path+"/"+origName);
									//}else{
										//sftpChannel.chmod(permission, "/"+path+"/"+origName);
								//	}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	         
	    	    		       
	            	            
	            	            sftpChannel.exit();
	            	            session.disconnect();
	            	        } catch (JSchException e) {
	            	            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
	            	        }*/
	            			
	            		}else{
	            		FTPClient con = new FTPClient();
			
	            		try {
	        	        	//boolean transType = con.setFileType(FTP.ASCII_FILE_TYPE);
	    					con.connect(s.serverName);
	    				    if (con.login(s.userName, s.passWord))
	    	    		    {
	    	    		        con.enterLocalPassiveMode(); // important!
	    	    		        
	    	    		       // Log.i("perms",perms);
	    	    		        con.sendCommand("chmod "+perms+" "+path+"/"+origName);
	    	    		        
	    	    		    }
	    	    		    
	    					
	    				} catch (SocketException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				} catch (IOException e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	            	}
	            	}
	        	}
	        }
	
			return result;
			
			}
	       
		@Override
    	protected void onPreExecute() {
    		dialog = ProgressDialog.show(AHEActivity.this, "", 
                    "Connecting...", true);
    		
    	}
		protected void onPostExecute(String[] result){
			dialog.cancel();
			//ndialog.cancel();
			
			
			arrayAdapter.notifyDataSetChanged();
	    }
		
	}
	
	
	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
	    public void allow(int reason) {
	        if (isFinishing()) {
	            // Don't update UI if Activity is finishing.
	            return;
	        }
	        // Should allow user access.
	        //displayResult(getString(R.string.allow));
	        AppIsLicensed = true;
	        Log.i("License",String.valueOf(reason));
	        //Toast.makeText(AHEActivity.this, "ALLOW", Toast.LENGTH_SHORT).show();
	    }

	    public void dontAllow(int reason) {
	        if (isFinishing()) {
	            // Don't update UI if Activity is finishing.
	            return;
	        }
	        //displayResult(getString(R.string.dont_allow));
	        Log.i("FLicense",String.valueOf(reason));
	        if (reason == Policy.RETRY) {
	            // If the reason received from the policy is RETRY, it was probably
	            // due to a loss of connection with the service, so we should give the
	            // user a chance to retry. So show a dialog to retry.
	           // showDialog(DIALOG_RETRY);
	        	//Toast.makeText(AHEActivity.this, "RETRY", Toast.LENGTH_SHORT).show();
	        	AlertDialog.Builder builder = new AlertDialog.Builder(AHEActivity.this);
	        	builder.setMessage("There was a problem with your connection. Please try again")
	        	       .setCancelable(false)
	        	       
	        	       .setNegativeButton("OK", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                dialog.cancel();
	        	           }
	        	       });
	        	AlertDialog alert = builder.create();
	        	alert.show();
	        } else {
	            // Otherwise, the user is not licensed to use this app.
	            // Your response should always inform the user that the application
	            // is not licensed, but your behavior at that point can vary. You might
	            // provide the user a limited access version of your app or you can
	            // take them to Google Play to purchase the app.
	           // showDialog(DIALOG_GOTOMARKET);
	        	//Toast.makeText(AHEActivity.this, "", Toast.LENGTH_LONG).show();
	        	AlertDialog.Builder builder = new AlertDialog.Builder(AHEActivity.this);
	        	builder.setMessage("Can't find app license. Go to Google Play Market?")
	        	       .setCancelable(false)
	        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.zen.androidhtmleditor"));
	        	        	   startActivity(browserIntent);
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

		public void applicationError(int errorCode) {
			// TODO Auto-generated method stub
			Log.i("LicenseError",String.valueOf(errorCode));
			
			 //String result = String.format(getString(R.string.application_error), errorCode);
			 //Log.i("LICENSE", result);
			//Toast.makeText(AHEActivity.this, "Error on Google Licensing Server. Please try again.", Toast.LENGTH_SHORT).show();
		}
	}
	
}