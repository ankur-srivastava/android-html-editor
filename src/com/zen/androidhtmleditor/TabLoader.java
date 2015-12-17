package com.zen.androidhtmleditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class TabLoader extends TabActivity {
	
	public static String PREFS_NAME="DEVTOOLS_PREF";
	private TabHost tabHost;
	private TabHost.TabSpec spec;

	private static int connectedTo = -1;
	public int z = 0;
	private String SanTest=null;
	
	public static String  fileName = "";
	

    
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    
	    
	    /*SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = settings.getString("userToken", "");
        if(token.length()==0){
        	Intent signIn = new Intent(MainTabs.this,SignIn.class);
			startActivity(signIn);
        	MainTabs.this.finish();
        }*/
        
      

        	
        	
        	setContentView(R.layout.tab_edit_view);
 

        	
        	//SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        	//String file = settings.getString("fileName", "");
			
        	//addMethod(file);
        	

        	
        	
        	/*Button addBtn = (Button) findViewById(R.id.add_btn);
        	addBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {

        		SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            	String file = settings.getString("fileName", "");
        		
        		
        	  addMethod(file);  // Method which adds the Tab Host
        	}
        	});  	
        */
	    tabHost = getTabHost();  // The activity TabHost
	    
	    
	    Intent openEditor = new Intent(); 
		openEditor.setClass(this, AHEActivity.class);
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		Button tview=new Button(this); 
		tview.setLayoutParams(lp);
		tview.setText("FTP"); 
		tview.setTextColor(Color.WHITE);
		//tview.setBackgroundResource(R.drawable.tab_buttons_on);
	    
	    tabHost = getTabHost();
	    spec = tabHost.newTabSpec("FTP"+"_"+Integer.toString(0))
	    		.setIndicator(tview)
	    		.setContent(openEditor);
	    
	    tabHost.addTab(spec);
	    tabHost.setCurrentTab(0);
		//tabHost.addTab(tabHost.newTabSpec("wot").setIndicator(tview1).setContent(openEditor));
		//Log.d("z",Integer.toString(z));
		//++z;
	    
	    tabHost.setOnTabChangedListener(new OnTabChangeListener(){
			
			public void onTabChanged(String tabId) {
				
				tabHost.refreshDrawableState();
				
				//Log.i("TabFile",tabId);

				int tabCount = tabHost.getTabWidget().getChildCount();
				
				for(int i=0;i<tabCount;i++){
					
					
					Button b = (Button)tabHost.getTabWidget().getChildAt(i);
					//b.setBackgroundResource(R.drawable.tab_buttons_off);
				}
				
				Button b2 = (Button)tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab());
				//b2.setBackgroundResource(R.drawable.tab_buttons_on);
				
			}
			});
	    
	   
	   
	    
        
	}
	
	

		private void deleteMethod() {

		// Since we can't really delete a TAB
		// We hide it

		int position = tabHost.getCurrentTab();
		Log.d("Position",Integer.toString(position));


		// if (position != 0 ) {
		// 
		// tabHost.getCurrentTabView().setVisibility(1);
		// tabHost.setCurrentTab(position-1);
		// 
		// }
		// else if(position== z){
		// tabHost.getCurrentTabView().setVisibility(1);
		// tabHost.setCurrentTab(position+1);
		// }
		Log.d("Z val in delete()",Integer.toString(z));
		if(position >0)
		{
		tabHost.getCurrentTabView().setVisibility(View.GONE);
		tabHost.setCurrentTab(position+1);
		z-=1;
		if(z<0)
		z=0;
		}
		else if(position == 0)
		{
		tabHost.getCurrentTabView().setVisibility(View.GONE);
		tabHost.setCurrentTab(position+1);
		z=0;
		}
		else if(position == z)
		{
		tabHost.getCurrentTabView().setVisibility(View.GONE);
		tabHost.setCurrentTab(z-1);
		Log.d("Z value in final","lol");
		Log.d("Pos",Integer.toString(position));
		Log.d("z pos",Integer.toString(z));


		}
	
		}

		

		
	
}

