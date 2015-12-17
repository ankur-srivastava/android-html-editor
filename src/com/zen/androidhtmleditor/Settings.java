package com.zen.androidhtmleditor;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.zen.androidhtmleditor.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends Activity {
	public static String PREFS_NAME="DEVTOOLS_PREF";
	Dialog dialog;
	public static String customThemes = "";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);

        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        customThemes = settings.getString("Themes", "");
        if(customThemes.equals("")){
     		init(settings);
     	}
        
        int HighlightOn = settings.getInt("highlight", 0);
        int LineNumbersOn = settings.getInt("linenumbers", 0);
        
        int currentFontSize = settings.getInt("fontSize", 14);
        int currentFontStyle = settings.getInt("fontStyle", 0);
        
        TextView setFontSize = (TextView)findViewById(R.id.currentFontSize);
        setFontSize.setText(String.valueOf(currentFontSize)+" px");
        
        TextView setFontStyle = (TextView)findViewById(R.id.currentFontStyle);
        if(currentFontStyle==0){
        	setFontStyle.setText("Default");
        }else if(currentFontStyle==1){
        	setFontStyle.setText("Monospace");
        }else if(currentFontStyle==2){
        	setFontStyle.setText("Sans Serif");
        }else if(currentFontStyle==3){
        	setFontStyle.setText("Serif");
        }else{
        	setFontStyle.setText("Default");        	
        }
        
        TextView currentTheme = (TextView)findViewById(R.id.currentTheme);
        String cTheme = settings.getString("selectedTheme", "");
        currentTheme.setText(cTheme);
        
        
        CheckedText highlight = (CheckedText)findViewById(R.id.syntax);    
        
        if(HighlightOn==1){
        	highlight.setChecked(true);
        }
        highlight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            	SharedPreferences.Editor editor = settings.edit();
            	CheckedText highlight = (CheckedText)v;   
            	if(!highlight.isChecked()){
            		editor.putInt("highlight", 1);
            		highlight.setChecked(true);
            	}else{
            		editor.putInt("highlight", 0);
            		highlight.setChecked(false);
            	}
            	editor.commit(); 
            }
        });  
        
        
        CheckedText lineNumbers = (CheckedText)findViewById(R.id.lineNumbers);
        
        if(LineNumbersOn==1){
        	lineNumbers.setChecked(true);
        }
        lineNumbers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            	SharedPreferences.Editor editor = settings.edit();
            	CheckedText lineNumbers = (CheckedText)v.findViewById(R.id.lineNumbers); 
            	if(!lineNumbers.isChecked()){
            		editor.putInt("linenumbers", 1);
            		lineNumbers.setChecked(true);
            	}else{
            		editor.putInt("linenumbers", 0);
            		lineNumbers.setChecked(false);
            	}
            	
            	
            	editor.commit(); 
            }
        });  
        
        
        TextView rateme = (TextView)findViewById(R.id.rateme);
        rateme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(Intent.ACTION_VIEW);
            	intent.setData(Uri.parse("market://details?id=com.zen.androidhtmleditor"));
            	startActivity(intent);
            	 
	        	   
            }
        });        
        
        TextView setTheme = (TextView)findViewById(R.id.setTheme);
       
        setTheme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent ThemesIntent = new Intent(Settings.this,ThemeList.class);
    			startActivity(ThemesIntent);
            }
        });  
        
        
        TextView addServer = (TextView)findViewById(R.id.addServer);
        
       
        
        addServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent addAccountIntent = new Intent(Settings.this,AddAccount.class);
    			startActivity(addAccountIntent);
            }
        });  
        
        TextView importServer = (TextView)findViewById(R.id.importServer);
        
      
        importServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent importAccountIntent = new Intent(Settings.this,ImportAccount.class);
    			startActivity(importAccountIntent);
            }
        });  
        
        TextView addTheme = (TextView)findViewById(R.id.addTheme);
        
        addTheme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent addThemeIntent = new Intent(Settings.this,Themes.class);
    			startActivity(addThemeIntent);
            }
        });  
        
        TextView accounts = (TextView)findViewById(R.id.accounts);
      
        accounts.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent accountsIntent = new Intent(Settings.this,Accounts.class);
    			startActivity(accountsIntent);
            }
        }); 
        
        TextView fontStyle = (TextView)findViewById(R.id.fontStyle);
       
        fontStyle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	final String[] items = {"Normal", "monospace", "Sans Serif", "Serif"};

            	AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            	builder.setTitle("Select a Font Style");
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            	int selected = settings.getInt("fontStyle", 0);
            	

            	
            	
            	builder.setSingleChoiceItems(items, selected, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
            	    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    	SharedPreferences.Editor editor = settings.edit();
                    	editor.putInt("fontStyle", item);
                    	editor.commit();
                    	dialog.dismiss();
                    	TextView setf = (TextView)findViewById(R.id.currentFontStyle);
                    	setf.setText(String.valueOf(items[item]));
            	    }
            	});
            	AlertDialog alert = builder.create();
            	alert.show();
            	
            }
        });   
        
        TextView fontSize = (TextView)findViewById(R.id.fontSize);
       
        fontSize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	final String[] items = {"10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32"};

            	AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            	builder.setTitle("Select a Font Size");
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            	int selected = settings.getInt("fontSize", 14);
            	int i =0;
            	int sel= -1;
            	for(String s:items){
            		if(Integer.parseInt(s)==selected){
            			sel = i;
            		}
            		i++;
            	}
            	Log.i("fintsize",String.valueOf(sel));
            	builder.setSingleChoiceItems(items, sel, new DialogInterface.OnClickListener() {
            	    public void onClick(DialogInterface dialog, int item) {
            	    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    	SharedPreferences.Editor editor = settings.edit();
                    	editor.putInt("fontSize", Integer.parseInt((String) items[item]));
                    	
                    	editor.commit();
                    	dialog.dismiss();
                    	TextView setf = (TextView)findViewById(R.id.currentFontSize);
                    	setf.setText(String.valueOf(items[item])+"px");
            	    }
            	});
            	AlertDialog alert = builder.create();
            	alert.show();
            	
            }
        });
        
        TextView importTheme = (TextView)findViewById(R.id.importTheme);
        
        importTheme.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				  dialog = new Dialog(Settings.this);
				  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				  dialog.setContentView(R.layout.importdiag);

				  dialog.setCancelable(true);

		    	
				  Button closeServer = (Button)dialog.findViewById(R.id.closeServer);
				 	closeServer.setOnClickListener(new OnClickListener() {
				 		public void onClick(View v) {
				 			
				 			dialog.cancel();
				 		}
				 	});
				 
				 	Button saveServer = (Button)dialog.findViewById(R.id.saveServer);
				 	saveServer.setOnClickListener(new OnClickListener() {
				 		public void onClick(View v) {
				 			
				 			EditText themeUrl = (EditText)dialog.findViewById(R.id.themeLink);
				 			
				 			String url = themeUrl.getText().toString();
				        	  InputStream stream;
				        	    HttpURLConnection connection;
								try {
									connection = (HttpURLConnection)new URL(url).openConnection();
									if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					        	        stream = connection.getInputStream();
					        	        StringWriter writer = new StringWriter();
					        	        IOUtils.copy(stream, writer);
					        	        String jSonString = writer.toString();
					        	        if(jSonString.startsWith("{\"data\": [{")){
					        	        Gson gson = new Gson();
					        	        SearchResponse response = gson.fromJson(jSonString, SearchResponse.class);
					        	        if(response!=null){
					        	        List<Result> results = response.data;
					        	        for(Result l : results){
					        	        	if(l.keywordColor.equals("") || l.variableColor.equals("") || l.stringColor.equals("") || l.commentColor.equals("") || l.backgroundColor.equals("") || l.themeName.equals("") || l.textColor.equals("") ){
					        	        		
					        	        	}else{
					        	        		
					        	        		int Keyword = Color.parseColor("#"+l.keywordColor);
					        	        		int Variable = Color.parseColor("#"+l.variableColor);
					        	        		int String = Color.parseColor("#"+l.stringColor);
					        	        		int Comment = Color.parseColor("#"+l.commentColor);
					        	        		int Background = Color.parseColor("#"+l.backgroundColor);
					        	        		int TextColor = Color.parseColor("#"+l.textColor);
					        	        		String ThemeName = l.themeName;
					        	        		
					        	        	String jSonColor = "{\"themeName\":\""+ThemeName+"\",";
					        	          	  jSonColor += "\"keywordColor\":\""+Keyword+"\",";
					        	          	  jSonColor += "\"variableColor\":\""+Variable+"\",";
					        	          	  jSonColor += "\"commentColor\":\""+Comment+"\",";
					        	          	  jSonColor += "\"stringColor\":\""+String+"\",";
					        	          	  jSonColor += "\"backgroundColor\":\""+Background+"\",";
					        	          	  jSonColor += "\"textColor\":\""+TextColor+"\"";
					        	          	  jSonColor += "}";
					        	          	  
					        	          	String jSonStart = "{\"data\":[";
					        	          	String jSonEnd = "]}";
					        	          	String jSonSaveColor = "";
					        	          	
					        	          	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
					        	          	String customThemes = settings.getString("Themes", "");
					        	          	if(customThemes.equals("")){
					        	          		jSonSaveColor = jSonStart+jSonColor+jSonEnd;
					        	          	}else{
					        	          		
					        	          		
					        	    	        SearchResponse response2 = gson.fromJson(customThemes, SearchResponse.class);
					        	    	        List<Result> results2 = response2.data;
					        	    	        
					        	    	        for(Result l2 : results2){
					        	    	        	
					        	    	        	jSonColor += ",{\"themeName\":\""+l2.themeName+"\",";
					        	    	        	jSonColor += "\"keywordColor\":\""+l2.keywordColor+"\",";
					        	    	        	jSonColor += "\"variableColor\":\""+l2.variableColor+"\",";
					        	    	        	jSonColor += "\"commentColor\":\""+l2.commentColor+"\",";
					        	    	        	jSonColor += "\"stringColor\":\""+l2.stringColor+"\",";
					        	    	        	jSonColor += "\"backgroundColor\":\""+l2.backgroundColor+"\",";
					        	    	        	jSonColor += "\"textColor\":\""+l2.textColor+"\"";
					        	    	        	jSonColor += "}";
					        	    	        }
					        	    	        jSonSaveColor = jSonStart+jSonColor+jSonEnd;
					        	    	        
					        	          	}
					        	          	SharedPreferences.Editor editor = settings.edit();
					        	    		editor.putString("Themes", jSonSaveColor); 
					        	    		editor.commit(); 
					        	    		dialog.dismiss();
					        	        		
					        	        	}
					        	        }
					        	       }
					        	        }else{
					        	        	Toast.makeText(Settings.this, "Invalid Format", Toast.LENGTH_SHORT).show();
					        	        }
					        	        
					        	    }
								} catch (MalformedURLException e) {
									Toast.makeText(Settings.this, "Invalid URL", Toast.LENGTH_SHORT).show();
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				        	    
				        	  
				        	}
				 			
				 		
				 	});
	

		      	dialog.show();
		}
        });
        
	}
	
	public static void init(SharedPreferences settings){
    	
    	//SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String themes = settings.getString("Themes", "");
		Gson gson = new Gson();
        SearchResponse response = gson.fromJson(themes, SearchResponse.class);
        if(response!=null){
        List<Result> results = response.data;
        
        int location = 0;
        for(Result l : results){
        	
        	if(l.themeName.equals("Default Dark Theme") || l.themeName.equals("Default Light Theme")){
        		//results.remove(location);
        	}	
        	location++;
        }
       }
        
		
  	  
    	String jSonColor = "{\"themeName\":\"Default Dark Theme\",";
    	  jSonColor += "\"keywordColor\":\""+String.valueOf(Color.parseColor("#c0c000"))+"\",";
    	  jSonColor += "\"variableColor\":\""+String.valueOf(Color.parseColor("#00c0c0"))+"\",";
    	  jSonColor += "\"commentColor\":\""+String.valueOf(Color.parseColor("#808080"))+"\",";
    	  jSonColor += "\"stringColor\":\""+String.valueOf(Color.parseColor("#ffa0a0"))+"\",";
    	  jSonColor += "\"backgroundColor\":\""+String.valueOf(Color.parseColor("#333333"))+"\",";
    	  jSonColor += "\"textColor\":\""+String.valueOf(Color.parseColor("#ffffff"))+"\",";
    	jSonColor += "\"tagColor\":\""+String.valueOf(Color.parseColor("#00ffff"))+"\",";
  	jSonColor += "\"attributeColor\":\""+String.valueOf(Color.parseColor("#00c000"))+"\"";
    	  jSonColor += "},";
    	  
    	  jSonColor += "{\"themeName\":\"Default Light Theme\",";
    	jSonColor += "\"keywordColor\":\""+String.valueOf(Color.parseColor("#000088"))+"\",";
  	  jSonColor += "\"variableColor\":\""+String.valueOf(Color.parseColor("#000080"))+"\",";
  	  jSonColor += "\"commentColor\":\""+String.valueOf(Color.parseColor("#3F7F5F"))+"\",";
  	  jSonColor += "\"stringColor\":\""+String.valueOf(Color.parseColor("#008800"))+"\",";
  	  jSonColor += "\"backgroundColor\":\""+String.valueOf(Color.parseColor("#ffffff"))+"\",";
  	  jSonColor += "\"textColor\":\""+String.valueOf(Color.parseColor("#000000"))+"\",";
  	jSonColor += "\"tagColor\":\""+String.valueOf(Color.parseColor("#800080"))+"\",";
  	jSonColor += "\"attributeColor\":\""+String.valueOf(Color.parseColor("#FF0000"))+"\"";
  	  jSonColor += "}";
  	  //Log.i("JSON",jSonColor);
  	  
    	String jSonStart = "{\"data\":[";
    	String jSonEnd = "]}";
    	String jSonSaveColor = "";
    	
    	jSonSaveColor = jSonStart+jSonColor+jSonEnd;
    	
    	SharedPreferences.Editor editor = settings.edit();
    	
    	editor.putString("Themes", ""); 
    	editor.commit();
    	
    	editor.putString("Themes", jSonSaveColor); 
    	editor.putString("selectedTheme", "Default Dark Theme");  
    	editor.commit(); 
    	//finish();
  	 
  	  
    	
    }
	
}