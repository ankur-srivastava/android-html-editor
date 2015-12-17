package com.zen.androidhtmleditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import com.zen.androidhtmleditor.highlight.Highlight;
import com.zen.androidhtmleditor.util.ColorScheme;
import com.zen.androidhtmleditor.util.Result;
import com.zen.androidhtmleditor.util.SearchResponse;
import com.zen.androidhtmleditor.R;



import com.zen.androidhtmleditor.JecEditText;





import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.Touch;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class EditFile extends Activity{
    /** Called when the activity is first created. */
	
	Dialog dialog;
	public static String PREFS_NAME="DEVTOOLS_PREF";
	public static Paint mLineNumberPaint;
	public static int mLineNumberWidth;
	public static int mLineNumber;
	public static int mLineNumberLength;
	public static boolean mShowLineNum;
	public static boolean running = false;
	boolean isUpdatingSyntaxHighlighting = false;
    
	ArrayList<Integer[]> findReplaceGroup=null;
	private static Handler mHandler = new Handler();
	
	
	private static int selection = 0;
	private static SharedPreferences settings;
	private static int singleLineOffsetStart = -1;
	private static int singleLineOffsetEnd = -1;
	
	private int settings_linenumbers = 0;
	private int settings_fontSize = 14;
	private int settings_fontStyle = 0;
	final String[] fontstyleitems = {"Normal", "monospace", "Sans Serif", "Serif"};
	private int settings_highlight = 0;
	
	public static String ext;
	private static String fType = null;
	private static String imageName = null;
	private static String fileName = null;
	private static final int BUFFER_SIZE = 1024 * 2;
	private static String fileContents = null;
	private static com.zen.androidhtmleditor.JecEditText editFile;
    
	static final String[] quickInput = new String[] { "{","}","(",")","[","]","<",">","/","\\","|","#",":",";",",",".","=","\n","\t","%","\"","$","==","===","!="};
	
	
	private String servername;
	
	Dialog pdialog;
	
	Dialog input;
	
	
	public int MAX_HIGHLIGHT_FILESIZE = 400;
	private SharedPreferences mPref;
	public static String TEMP_PATH = "";
	private final static String SYNTAX_SIGN = "18";
	private Highlight mHighlight;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit);
        
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings_linenumbers = settings.getInt("linenumbers", 0);
        settings_fontSize = settings.getInt("fontSize", 14);
        settings_fontStyle = settings.getInt("fontStyle", 0);
        settings_highlight = settings.getInt("highlight", 0);
        
        
        
        
        
        
        
        
        
        
        ext = null;
        if(settings_highlight==1){
        	//mHighlight = new Highlight();
        }
        //fileName = settings.getString("fileName", null);
        Intent intent= getIntent();
        fileName = intent.getStringExtra("fileName");
        
        if(fileName!=null){
	        String imageFiles[] = {"jpg","jpeg","gif","png"};
			String downloadableFiles[] = {"zip","tar","tgz","taz","z","gz","rar"};
			String recognizedFiles[] = {"sh","txt","asp","php","php3","php4","css","html","htm","xhtml","js","pl","phtml","rb","rhtml","xml","rss","svg","java","cfm","py"};
			
			String[] fileExtArray = fileName.split("\\.");
			
			String fileExt = fileExtArray[fileExtArray.length-1];
			
			//Check if file is an image, if so open with image browser.
			if(ext==null){
				for(String s:recognizedFiles){
					if(fileExt.equals(s)){
						ext = fileExt;
						fType = "recognized";
					}
				}
			}
			if(ext==null){
				for(String s:downloadableFiles){
					if(fileExt.equals(s)){
						ext = fileExt;
						fType = "downloadable";
					}
				}
			}
			if(ext==null){
				for(String s:imageFiles){
					if(fileExt.equals(s)){
						ext = fileExt;
						fType = "images";
					}
				}
			}
			
			if(fType==null){
				fType = "unknown";
			}
			
			if(ext!=null){
				//Highlight.setExt(ext);
			}
			
        }
        
        
        com.zen.androidhtmleditor.JecEditText myEditText = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
        myEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String customThemes = settings.getString("Themes", "");
        String selectedTheme = settings.getString("selectedTheme", "Default Light Theme");
        Log.i("selected theme",selectedTheme);
        if(selectedTheme.length()>0){
      		
      		Gson gson = new Gson();
	        SearchResponse response = gson.fromJson(customThemes, SearchResponse.class);
	        List<Result> results = response.data;
	        
	        for(Result l : results){
	        	
	        	if(selectedTheme.equals(l.themeName)){
	        		Log.i("stringColor","."+l.stringColor);
	        		Log.i("textColor","."+l.textColor);
	        		Log.i("commentColor","."+l.commentColor);
	        		Log.i("keywordColor","."+l.keywordColor);
	        		Log.i("tagColor","."+l.tagColor);
	        		Log.i("attributeColor","."+l.attributeColor);
	        		Log.i("variableColor","."+l.variableColor);
	        		Log.i("backgroundColor","."+l.backgroundColor);
	        		
	        		ColorScheme.color_string = Integer.valueOf(l.stringColor);
	                ColorScheme.color_font= Integer.valueOf(l.textColor);
	                ColorScheme.color_comment = Integer.valueOf(l.commentColor);
	                ColorScheme.color_keyword = Integer.valueOf(l.keywordColor);
	                ColorScheme.color_tag = Integer.valueOf(l.tagColor);
	                ColorScheme.color_attr_name = Integer.valueOf(l.attributeColor);
	                ColorScheme.color_function = Integer.valueOf(l.variableColor);
	                ColorScheme.color_backgroup = Integer.valueOf(l.backgroundColor);
	        		
	        	}
                
	        }
            
        }
        
        
        myEditText.setBackgroundColor(ColorScheme.color_backgroup);
        myEditText.setTextColor(ColorScheme.color_font);
        
        if(settings_highlight==1){
        	com.zen.androidhtmleditor.JecEditText.doHighlight = true;
        }else{
        	com.zen.androidhtmleditor.JecEditText.doHighlight = false;
        }
        
        myEditText.setTextSize(settings_fontSize);
        
        if(settings_linenumbers==0){
        	com.zen.androidhtmleditor.JecEditText.mShowLineNum = false;
        }else{
        	com.zen.androidhtmleditor.JecEditText.mShowLineNum = true;
        }
        
        if(settings_fontStyle==0){
        	myEditText.setTypeface(Typeface.DEFAULT);
        }else if(settings_fontStyle==1){
        	myEditText.setTypeface(Typeface.MONOSPACE);
        }else if(settings_fontStyle==2){
        	myEditText.setTypeface(Typeface.SANS_SERIF);
        }else if(settings_fontStyle==3){
        	myEditText.setTypeface(Typeface.SERIF);
        }else{
        	myEditText.setTypeface(Typeface.DEFAULT);
        }
        
        
        
        
       
      	
        
        
        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
            	
            	com.zen.androidhtmleditor.JecEditText myEditText = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
            	
            	
            	/*int selectionStart = Selection.getSelectionStart(myEditText.getText());
                 Layout layout = myEditText.getLayout();
                 int line = layout.getLineForOffset(selectionStart);
                 singleLineOffsetStart = layout.getLineStart(line);
                 singleLineOffsetEnd = layout.getLineEnd(line);
                 
                 mHandler.removeCallbacks(updateEditText);
                 mHandler.postDelayed(updateEditText, 500);
                 */
            	
            	
    		    
            }
            
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            	
            }
            
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	if(settings_highlight==1){
                    //mHighlight.redraw();
            	}
                
            }
            
            
        };
        myEditText.addTextChangedListener(fieldValidatorTextWatcher);
        
        
        
        
        
        int connectedTo = settings.getInt("connectedTo", -1);
        
        if(fileName!=null && connectedTo!=-1){
        	
        	
         	String currentServers = settings.getString("Accounts", "");
         	if(currentServers.equals("")){}else{
                Gson gson = new Gson();
                SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
                List<Result> results = response.data;
                Result l = results.get(connectedTo);
                if(l.serverName!="" && l.userName!="" && l.port!=""){
                    servername = l.serverName;
                    if(l.sftp.equals("0") || l.sftp.equals("1") || l.sftp.equals("2")){
                        new MyEditTask(l.serverName, l.userName, l.passWord,fileName,l.sftp, l.port).execute();
                    }else if(l.sftp.equals("3")){
                        new EditSSLTask(l.serverName, l.userName, l.passWord, fileName, l.port).execute();
                    }
                    
                }
         	}
        	
        	
        }
        
        
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
        {
            TEMP_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/.ahe";
        }else
        {
            TEMP_PATH = getFilesDir().getAbsolutePath() + "/.ahe";
        }
        
        File temp = new File(TEMP_PATH);
        if(!temp.isDirectory() && !temp.mkdir())
        {
            
            // return;
        }
        // Ã¨Â§Â£Ã¥Å½â€¹Ã¨Â¯Â­Ã¦Â³â€¢Ã¦â€“â€¡Ã¤Â»Â¶
        String synfilestr = TEMP_PATH + "/version";
        File synsignfile = new File(synfilestr);
        if(!synsignfile.isFile())
        {
            if(!unpackSyntax())
            {
                
                // return;
            }else
            {
                writeFile(synfilestr, SYNTAX_SIGN, "utf-8", false);
            }
        }else
        {
            if(!SYNTAX_SIGN.equals(Highlight.readFile(synfilestr, "utf-8")))
            {
                if(!unpackSyntax())
                {
                    
                    // return;
                }else
                {
                    writeFile(synfilestr, SYNTAX_SIGN, "utf-8", false);
                }
            }
        }
        
        Highlight.init();
        init_highlight();
        
        
        
        
        
        //Buttons
        
        String quickTags[] = {"<? ?>","<?php ?>","<% %>","<?xml ?>","<script></script>"};
        
        Button quickInputButton = (Button)findViewById(R.id.quickinputbutton);
        quickInputButton.setOnClickListener(new OnClickListener(){
            
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
                final Dialog dialog = new Dialog(EditFile.this);
                dialog.setContentView(R.layout.dialog_grid);
                dialog.setTitle(R.string.quick_input_title);
                GridView gridview = (GridView) dialog.findViewById(R.id.gridview);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFile.this,
                                                                        R.layout.gridlistitem, quickInput);
                
                gridview.setAdapter(adapter);
                
                gridview.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        
                        com.zen.androidhtmleditor.JecEditText myEditText = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
                        int start = myEditText.getSelectionStart();
                        myEditText.getText().insert(start, ((TextView) v).getText());
                        dialog.dismiss();
                    }
                });
                
                
                dialog.show();
				
			}
        	
        });
        
        
        
        Button closeButton = (Button)findViewById(R.id.closebutton);
        closeButton.setOnClickListener(new OnClickListener(){
            
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TabActivity t = (TabActivity)getParent();
				TabHost tabHost = t.getTabHost();
		    	String fileToSave = tabHost.getCurrentTabTag();
		    	close();
			}
        	
        });
        
        Button saveButton = (Button)findViewById(R.id.savebutton);
        saveButton.setOnClickListener(new OnClickListener(){
            
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RelativeLayout l = (RelativeLayout)findViewById(R.id.editLayout);
				TabActivity t = (TabActivity)getParent();
				TabHost tabHost = t.getTabHost();
		    	String fileToSave = tabHost.getCurrentTabTag();
		    	save(l,fileToSave);
			}
        	
        });
        
        Button saveasButton = (Button)findViewById(R.id.saveasbutton);
        saveasButton.setOnClickListener(new OnClickListener(){
            
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RelativeLayout l = (RelativeLayout)findViewById(R.id.editLayout);
				TabActivity t = (TabActivity)getParent();
				TabHost tabHost = t.getTabHost();
		    	String fileToSave = tabHost.getCurrentTabTag();
		    	saveAs(l,fileToSave);
			}
        	
        });
        
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getParent().getMenuInflater();
	    inflater.inflate(R.menu.editmenu, menu);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		RelativeLayout l = (RelativeLayout)findViewById(R.id.editLayout);
		
		TabActivity t = (TabActivity)this.getParent();
		TabHost tabHost = t.getTabHost();
    	String fileToSave = tabHost.getCurrentTabTag();
		
	    switch (item.getItemId()) {
	        case R.id.filesave:
                
	        	
	        	save(l,fileToSave);
	            return true;
	        case R.id.filesaveas:
	        	
	        	saveAs(l,fileToSave);
	            return true;
                
	        case R.id.fileclose:
	        	
	        	
	        	
	        	//Log.i("WhatFile",fileToSave);
	        	close();
                return true;
                
	        case R.id.filesearch:
	        	
	        	findReplace();
	        	
	        	return true;
                
	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
    private Runnable updateEditText = new Runnable() {
    public void run() {
    
    
    
    com.zen.androidhtmleditor.JecEditText editFile = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
    
    Editable str = editFile.getEditableText();
    Log.i("single","true");
    if(settings_highlight==1){
        //mHighlight.redraw();
    }
    //Highlight.singleLineDraw(str,singleLineOffsetStart,singleLineOffsetEnd);
    
    
}
};


public void findReplace(){

pdialog = new Dialog(EditFile.this);
pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
pdialog.setContentView(R.layout.finddiag);

pdialog.setCancelable(true);


Button closeServer = (Button)pdialog.findViewById(R.id.closeServer);
closeServer.setOnClickListener(new OnClickListener() {
public void onClick(View v) {

pdialog.cancel();
}
});

Button replaceServer = (Button)pdialog.findViewById(R.id.replaceServer);
replaceServer.setOnClickListener(new OnClickListener() {
public void onClick(View v) {
EditText findText = (EditText)pdialog.findViewById(R.id.findText);
String find = findText.getText().toString();

EditText replaceText = (EditText)pdialog.findViewById(R.id.replaceText);
String replace = replaceText.getText().toString();


find = find.replace("\\", "\\\\");
find = find.replace("$", "\\$");
find = find.replace("{", "\\{");
find = find.replace("}", "\\}");
find = find.replace("]", "\\]");
find = find.replace("[", "\\[");
find = find.replace("*", "\\*");
find = find.replace("^", "\\^");
find = find.replace("@", "\\@");
find = find.replace("!", "\\!");
find = find.replace("#", "\\#");
find = find.replace("&", "\\&");
find = find.replace("(", "\\(");
find = find.replace(")", "\\)");
find = find.replace("-", "\\-");
find = find.replace("+", "\\+");
find = find.replace("=", "\\=");
find = find.replace("|", "\\|");
find = find.replace("/", "\\/");




com.zen.androidhtmleditor.JecEditText code = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
String replaceCode = code.getText().toString();


CheckBox checkBox = (CheckBox)pdialog.findViewById(R.id.replaceAllCheck);






findReplaceGroup = new ArrayList<Integer[]>();
Pattern keywords = Pattern.compile("(?<!\\w>)("+find+")(?!\\w|=\")");
Matcher matcher = keywords.matcher(replaceCode);
StringBuffer sb = new StringBuffer(replaceCode.length());
int matchDone = 0;
int cursorpos = code.getSelectionStart();
while (matcher.find()) {
//String text = matcher.group(1);
if (checkBox.isChecked()) {

matcher.appendReplacement(sb, Matcher.quoteReplacement(replace));

}else{

if(matcher.start() >= cursorpos && matchDone == 0){
matcher.appendReplacement(sb, Matcher.quoteReplacement(replace));
matchDone = 1;
}

}
}
matcher.appendTail(sb);
replaceCode = sb.toString();
code.setText(replaceCode);



pdialog.dismiss();
}
});

Button saveServer = (Button)pdialog.findViewById(R.id.saveServer);
saveServer.setOnClickListener(new OnClickListener() {

public void onClick(View v) {

EditText findText = (EditText)pdialog.findViewById(R.id.findText);
String find = findText.getText().toString();




find = find.replace("\\", "\\\\");
find = find.replace("$", "\\$");
find = find.replace("{", "\\{");
find = find.replace("}", "\\}");
find = find.replace("]", "\\]");
find = find.replace("[", "\\[");
find = find.replace("*", "\\*");
find = find.replace("^", "\\^");
find = find.replace("@", "\\@");
find = find.replace("!", "\\!");
find = find.replace("#", "\\#");
find = find.replace("&", "\\&");
find = find.replace("(", "\\(");
find = find.replace(")", "\\)");
find = find.replace("-", "\\-");
find = find.replace("+", "\\+");
find = find.replace("=", "\\=");
find = find.replace("|", "\\|");
find = find.replace("/", "\\/");


com.zen.androidhtmleditor.JecEditText code = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
String replaceCode = code.getText().toString();





Pattern keywords = Pattern.compile("(?<!\\w>)("+find+")(?!\\w|=\")");
Matcher matcher = keywords.matcher(replaceCode);
int cursorpos = code.getSelectionStart();
int matchDone = 0;
while (matcher.find()) {
if(matcher.start() >= cursorpos && matchDone == 0){
code.setSelection(matcher.start(), matcher.end());
matchDone = 1;

}

}


pdialog.dismiss();
}

});

/*
 * 		        	 findReplaceGroup = new ArrayList<Integer[]>();
 
 while (matcher.find()) {
 Integer[] matched = {matcher.start(),matcher.end()};
 findReplaceGroup.add(matched);
 
 //str.setSpan(new ForegroundColorSpan(mKeywords), start+matcher.start(), start+matcher.start()+matcher.group(0).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
 
 
 }
 */



pdialog.show();
}

@Override
public void onResume() {
super.onResume();  // Always call the superclass method first
settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
settings_linenumbers = settings.getInt("linenumbers", 0);
settings_fontSize = settings.getInt("fontSize", 14);
settings_fontStyle = settings.getInt("fontStyle", 0);
settings_highlight = settings.getInt("highlight", 0);
com.zen.androidhtmleditor.JecEditText myEditText = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
myEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

if(settings_highlight==1){
com.zen.androidhtmleditor.JecEditText.doHighlight = true;
}else{
com.zen.androidhtmleditor.JecEditText.doHighlight = false;
}

myEditText.setTextSize(settings_fontSize);

if(settings_linenumbers==0){
com.zen.androidhtmleditor.JecEditText.mShowLineNum = false;

}else{
com.zen.androidhtmleditor.JecEditText.mShowLineNum = true;
}

if(settings_fontStyle==0){
myEditText.setTypeface(Typeface.DEFAULT);
}else if(settings_fontStyle==1){
myEditText.setTypeface(Typeface.MONOSPACE);
}else if(settings_fontStyle==2){
myEditText.setTypeface(Typeface.SANS_SERIF);
}else if(settings_fontStyle==3){
myEditText.setTypeface(Typeface.SERIF);
}else{
myEditText.setTypeface(Typeface.DEFAULT);
}

}

public void save(RelativeLayout l,String fileNames){

com.zen.androidhtmleditor.JecEditText editFile = (com.zen.androidhtmleditor.JecEditText)l.findViewById(R.id.text_content);

//SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
//String fileName = settings.getString("fileName", null);
Log.i("filename",fileName);

String currentServers = settings.getString("Accounts", "");
int connectedTo = settings.getInt("connectedTo", -1);

if(connectedTo!=-1){
if(currentServers.equals("")){}else{
Gson gson = new Gson();
SearchResponse response = gson.fromJson(currentServers, SearchResponse.class);
List<Result> results = response.data;
Result s = results.get(connectedTo);
if(s.serverName!="" && s.userName!=""){
String text = editFile.getText().toString();
if(s.sftp.equals("3")){


new SaveSSLTask(s.serverName, s.userName, s.passWord, fileName, text, s.sftp, s.port).execute();
}else{

new SaveTask(s.serverName, s.userName, s.passWord, fileName, text, s.sftp, s.port).execute();


}
}
}
}


}


public void saveAs(RelativeLayout l,String fileNameSave){

final com.zen.androidhtmleditor.JecEditText editFileContents = (com.zen.androidhtmleditor.JecEditText)l.findViewById(R.id.text_content);

//final SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
final String fileName = fileNameSave;

input = new Dialog(EditFile.this);
input.requestWindowFeature(Window.FEATURE_NO_TITLE);
input.setContentView(R.layout.saveasdiag);
input.setCancelable(true);
Button closeServer = (Button)input.findViewById(R.id.closeServer);
closeServer.setOnClickListener(new OnClickListener() {
public void onClick(View v) {

input.cancel();
}
});

Button saveServer = (Button)input.findViewById(R.id.saveServer);
saveServer.setOnClickListener(new OnClickListener() {
public void onClick(View v) {

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
String text = editFileContents.getText().toString();
EditText newFileName = (EditText)input.findViewById(R.id.themeLink);
String nfn = newFileName.getText().toString();

String[] pathParts = fileName.split("/");
String path = "";
for(int i=0;i<(pathParts.length-1);i++){
path += pathParts[i]+"/";
}
path += nfn;
new SaveSSLTask(s.serverName, s.userName, s.passWord, path, text, s.sftp, s.port).execute();
input.dismiss();

}else{

EditText newFileName = (EditText)input.findViewById(R.id.themeLink);
String nfn = newFileName.getText().toString();

String[] pathParts = fileName.split("/");
String path = "";
for(int i=0;i<(pathParts.length-1);i++){
path += pathParts[i]+"/";
}
path += nfn;
String text = editFileContents.getText().toString();
new SaveTask(s.serverName, s.userName, s.passWord, path, text, s.sftp, s.port).execute();
/*FTPClient con = new FTPClient();
 con.setDefaultTimeout(90000);
 con.setConnectTimeout(90000);
 try {
 
 con.connect(s.serverName);
 if (con.login(s.userName, s.passWord))
 {
 con.enterLocalPassiveMode(); // important!
 
 
 try {
 String text = editFileContents.getText().toString();
 InputStream inputStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
 OutputStream outputStream = con.storeFileStream(path);
 
 byte[] buffer = new byte[4096];
 int lf;
 while((lf = inputStream.read(buffer))!=-1)
 {
 outputStream.write(buffer, 0, lf);
 }
 inputStream.close();
 outputStream.flush();
 outputStream.close();
 Toast.makeText(EditFile.this, "File saved", Toast.LENGTH_SHORT).show();
 
 
 } catch (UnsupportedEncodingException e) {
 e.printStackTrace();
 }
 
 
 
 input.dismiss();
 //finish();
 }
 
 } catch (SocketException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 } catch (IOException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 }*/



}
}
}
}
}

});
input.show();
}




public int getCurrentCursorLine(EditText editText)
{
int selectionStart = Selection.getSelectionStart(editText.getText());
Layout layout = editText.getLayout();

if (!(selectionStart == -1)) {
return layout.getLineForOffset(selectionStart);
}

return -1;
}




private void close() {

// Since we can't really delete a TAB
// We hide it

TabActivity t = (TabActivity)this.getParent();
TabHost tabHost = t.getTabHost();



if(tabHost.getChildCount()==1){
tabHost.clearAllTabs();
}else{
int position = tabHost.getCurrentTab();

tabHost.getCurrentTabView().setVisibility(View.GONE);
boolean allgone = true;
for(int i=0;i<=tabHost.getChildCount();i++){
//Log.i("TabNum",String.valueOf(i));
//Log.i("Tabs",String.valueOf(i)+" "+String.valueOf(tabHost.getTabWidget().getChildAt(i).getVisibility()));
if( tabHost.getTabWidget().getChildAt(i).getVisibility() == 0 ){
tabHost.setCurrentTab(i);
//Log.i("TabNum",String.valueOf(i));
allgone = false;
//break;
}


}
if(allgone){
tabHost.clearAllTabs();
}
}
/*if(position >0)
 {
 tabHost.getCurrentTabView().setVisibility(View.GONE);
 tabHost.setCurrentTab(position+1);
 //MainActivity.activeTabs-=1;
 //		if(MainActivity.activeTabs<0){
 //			MainActivity.activeTabs=0;
 //		}
 }
 else if(position == 0)
 {
 tabHost.getCurrentTabView().setVisibility(View.GONE);
 tabHost.setCurrentTab(position+1);
 //MainActivity.activeTabs=0;
 }
 else if(position == MainActivity.activeTabs)
 {
 tabHost.getCurrentTabView().setVisibility(View.GONE);
 tabHost.setCurrentTab(MainActivity.activeTabs-1);
 //MainActivity.activeTabs-=1;
 }*/



//Toast.makeText(EditFile.this, String.valueOf(MainActivity.activeTabs), Toast.LENGTH_SHORT).show();
}


public class EditTask extends AsyncTask<Void, Void, String> {
    
    protected String mServer;
    protected String mUser;
    protected String mPass;
    protected String mType;
    protected String mFile;
    protected String mPort;
    protected String mFtp;
    
    protected EditTask(String server, String user, String pass, String file,String sftp, String port) {
        mServer = server;
        mUser = user;
        mPass = pass;
        mFile = file;
        mPort = port;
        mFtp = sftp;
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
                
                
                if( fType.equals("recognized") ){
                    total = IOUtils.toString(inStream);
                }else if( fType.equals("images") ){
	    	        
                    File sdCard = Environment.getExternalStorageDirectory();
                    
                    String[] fn = fileName.split("/");
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
                    imageName = fn[fn.length-1];
                }else if(fType.equals("downloadable")){
                    
                    File sdCard = Environment.getExternalStorageDirectory();
                    
                    String[] fn = fileName.split("/");
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
                    fileName = fn[fn.length-1];
                }else{
                    total = IOUtils.toString(inStream);
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
        
        
        
        return total;
        
        
    }
    
}


///////////////////

private class MyEditTask extends EditTask {
    public MyEditTask(String url, String username, String token, String file, String sftp, String port) {
        super(url, username, token, file, sftp, port);
    }
    
    @Override
    protected void onPreExecute() {
        
        dialog = ProgressDialog.show(EditFile.this, "Please Wait",
                                     "Connecting to "+servername, true);
        
        
        
        
        
        
        
        
        
        
        
    }
    
    @Override
    protected void onPostExecute(String set) {
        dialog.cancel();
        
        if(set!=null){
            
            fileContents = set;
            editFile = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
            
            if( fType.equals("recognized") ){
                
                editFile.setText(fileContents, TextView.BufferType.SPANNABLE);
                
            }else if( fType.equals("images") ){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + "/sdcard/"+imageName), "image/*");
                startActivity(intent);
                finish();
            }else if( fType.equals("downloadable") ){
                
                Toast.makeText(EditFile.this, "The file \""+fileName+"\" has been downloaded to you SDCard", Toast.LENGTH_SHORT).show();
                finish();
                
            }else{
                
                AlertDialog.Builder builder = new AlertDialog.Builder(EditFile.this);
                builder.setMessage("Are you sure you want to open this file type in a text editor?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();
                        
                        editFile.setText(fileContents, TextView.BufferType.SPANNABLE);
                        //editFile.setText(fileContents, TextView.BufferType.SPANNABLE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditFile.this.finish();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                
            }
            
            
            
            
        }
        ext = null;
        fType = null;
    }
    
}




/**
 * Defines a custom EditText View that draws lines between each line of text that is displayed.
 */
public static class JecEditText extends EditText {
    private Rect mRect;
    private Paint mPaint;
    private Paint mBox;
    private Layout mLayout;
    private Editable mText = null;
    //private FastScroller mFastScroller;
    private static int mCurrentLine = 0;
    private FlingRunnable mFlingRunnable;
    private VelocityTracker mVelocityTracker;
    // This constructor is used by LayoutInflater
    public JecEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        
        mRect = new Rect();
        mPaint = new Paint();
        mBox = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.rgb(153, 153, 153));
        mBox.setColor(Color.rgb(51, 51, 51));
        //mFastScroller = new FastScroller(getContext(), this);
    }
    
    
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        
        int selectionStart = Selection.getSelectionStart(this.getText());
        selection = selectionStart;
        
    }
    
    
    
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        mLayout = getLayout();
        mText = (Editable) getText();
        super.onDraw(canvas);
        
        drawView(canvas);
        
        //if(mFastScroller != null)
        // {
        //    mFastScroller.draw(canvas);
        //}
        
    }
    
    /**
     * This is called to draw the JecEditText object
     * @param canvas The canvas on which the background is drawn.
     */
    protected void drawView(Canvas canvas) {
        
        if(mLayout == null)
            return;
        
        
        
        Rect r = mRect;
        Paint paint = mPaint;
        paint.setFilterBitmap(true);
        int lineCount = mLayout.getLineCount();
        
        int lineNumbers = settings.getInt("linenumbers", 0);
        if(lineNumbers>0){
            for (int i = 0; i < lineCount; i++) {
                int baseline = getLineBounds(i, r);
                canvas.drawLine(r.left-5, r.top-2,r.left-5, baseline-2, paint);
                canvas.drawText(String.valueOf(i+1), r.left-35, baseline, mPaint);
            }
        }
        
        
        
        
        
        int currentLine = mLayout.getLineForOffset(selection);
        
        
        
        if(mCurrentLine == currentLine && mCurrentLine !=0){
            //Highlight.stop();
        }else{
            //Highlight.redraw();
        }
        
        
        mCurrentLine = currentLine;
        
        
        int first = currentLine-10;
        int last = currentLine+10;
        
        
        
        int height = this.getHeight();
        int lineHeight = this.getLineHeight();
        int visibleLines = height/lineHeight;
        int maxLine = visibleLines/2;
        
        int previousLineEnd2 = mLayout.getLineStart(first >= maxLine ? first-maxLine : 0);
        
        int nextEnd = mLayout.getLineStart(last+maxLine > lineCount ? lineCount : last+maxLine);
        
        if(nextEnd > 0){
            nextEnd = nextEnd-1;
        }
        
        // Highlight.highlight(mText, previousLineEnd2, nextEnd);
        
    }
    
    
    public boolean onTouchEvent(MotionEvent event)
    {
        if(mVelocityTracker == null)
        {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        
        /* if(mFastScroller != null)
         {
         boolean intercepted;
         intercepted = mFastScroller.onTouchEvent(event);
         
         if(intercepted)
         {
         return true;
         }
         intercepted = mFastScroller.onInterceptTouchEvent(event);
         //Log.v(TAG, "intercepted1:"+intercepted);
         if(intercepted)
         {
         return true;
         }
         
         }*/
        
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(mFlingRunnable != null)
                {
                    mFlingRunnable.endFling();
                    cancelLongPress();
                }
                
                break;
            case MotionEvent.ACTION_UP:
                
                int mMinimumVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
                int mMaximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                final int initialVelocity = (int) mVelocityTracker.getYVelocity();
                
                if(Math.abs(initialVelocity) > mMinimumVelocity)
                {
                    if(mFlingRunnable == null)
                    {
                        mFlingRunnable = new FlingRunnable(getContext());
                    }
                    //Highlight.stop();
                    mFlingRunnable.start(this, -initialVelocity);
                }else
                {
                    moveCursorToVisibleOffset();
                }
                
                if(mVelocityTracker != null)
                {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                
                break;
        }
        
        return super.onTouchEvent(event);
    }
    
    /**
     * Responsible for fling behavior. Use {@link #start(int)} to initiate a
     * fling. Each frame of the fling is handled in {@link #run()}. A
     * FlingRunnable will keep re-posting itself until the fling is done.
     *
     */
    private static class FlingRunnable implements Runnable
    {
        
        static final int TOUCH_MODE_REST = -1;
        static final int TOUCH_MODE_FLING = 3;
        
        int mTouchMode = TOUCH_MODE_REST;
        
        /**
         * Tracks the decay of a fling scroll
         */
        private final Scroller mScroller;
        
        /**
         * Y value reported by mScroller on the previous fling
         */
        private int mLastFlingY;
        
        private JecEditText mWidget = null;
        
        FlingRunnable(Context context)
        {
            mScroller = new Scroller(context);
        }
        
        void start(JecEditText parent, int initialVelocity)
        {
            mWidget = parent;
            int initialX = parent.getScrollX(); // initialVelocity < 0 ?
            // Integer.MAX_VALUE : 0;
            int initialY = parent.getScrollY(); // initialVelocity < 0 ?
            // Integer.MAX_VALUE : 0;
            mLastFlingY = initialY;
            mScroller.fling(initialX, initialY, 0, initialVelocity, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            mTouchMode = TOUCH_MODE_FLING;
            
            mWidget.post(this);
            
        }
        
        private void endFling()
        {
            mTouchMode = TOUCH_MODE_REST;
            
            if(mWidget != null)
            {
                mWidget.removeCallbacks(this);
                mWidget.moveCursorToVisibleOffset();
                
                mWidget = null;
            }
            
        }
        
        public void run()
        {
            switch(mTouchMode)
            {
                default:
                    return;
                    
                case TOUCH_MODE_FLING:
                {
                    
                    final Scroller scroller = mScroller;
                    boolean more = scroller.computeScrollOffset();
                    
                    int x = scroller.getCurrX();
                    int y = scroller.getCurrY();
                    
                    Layout layout = mWidget.getLayout();
                    
                    int padding;
                    try {
                        padding = mWidget.getTotalPaddingTop() + mWidget.getTotalPaddingBottom();
                    } catch(Exception e) {
                        padding = 0;
                    }
                    
                    
                    y = Math.min(y, layout.getHeight() - (mWidget.getHeight() - padding));
                    y = Math.max(y, 0);
                    
                    Touch.scrollTo(mWidget, layout, x, y);
                    int delta = mLastFlingY - y;
                    //Log.d(TAG, "delta:"+delta);
                    if(Math.abs(delta) <= 5)
                    {
                        //Highlight.redraw();
                    }
                    if(more && delta != 0)
                    {
                        mWidget.invalidate();
                        mLastFlingY = y;
                        mWidget.post(this);
                    }else
                    {
                        endFling();
                        
                    }
                    break;
                }
            }
            
        }
    }
    
    
} // end of EDIT TEXT


public class EditSSLTask extends AsyncTask<Void, Void, String> {
    
    protected String mServer;
    protected String mUser;
    protected String mPass;
    protected String mType;
    protected String mPath;
    protected String mFTP;
    protected String mPort;
    
    protected EditSSLTask(String server, String user, String pass, String path,String port) {
        mServer = server;
        mUser = user;
        mPass = pass;
        //mType = type;
        mPath = path;
        //mFTP = sftp;
        mPort = port;
    }
    
    
    
    @Override
    protected String doInBackground(Void... v) {
        
        return getFeed(mServer, mUser, mPass, mPath, mPort);
        
    }
    
    private String getFeed(String server, String user, String pass,  String path, String port) {
        
        String value = "";
        
        String fullPath = "/"+path;
        String total = "";
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
            try
            {
                //Log.i("file",fullPath);
	            InputStream inStream = sftpChannel.get(fullPath);
	            if( fType.equals("recognized") ){
    	        	total = IOUtils.toString(inStream);
    			}else if( fType.equals("images") ){
                    
    				File sdCard = Environment.getExternalStorageDirectory();
                    
    				String[] fn = fileName.split("/");
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
    				imageName = fn[fn.length-1];
    			}else if(fType.equals("downloadable")){
    				
    				File sdCard = Environment.getExternalStorageDirectory();
                    
    				String[] fn = fileName.split("/");
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
    				fileName = fn[fn.length-1];
    			}else{
    	    		total = IOUtils.toString(inStream);
    			}
	            
	            
	            sftpChannel.exit();
	            session.disconnect();
	        } catch (SftpException e) {
	            e.printStackTrace();
	        }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return total;
        
        
    }
    
    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(EditFile.this, "Please Wait",
                                     "Connecting to "+servername, true);
        
    }
    protected void onPostExecute(String set){
        dialog.cancel();
        if(set!=null){
            
            fileContents = set;
            editFile = (com.zen.androidhtmleditor.JecEditText)findViewById(R.id.text_content);
            
            
            if( fType.equals("recognized") ){
                
                editFile.setText(fileContents, TextView.BufferType.SPANNABLE);
            }else if( fType.equals("images") ){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + "/sdcard/"+imageName), "image/*");
                startActivity(intent);
                finish();
            }else if( fType.equals("downloadable") ){
                
                Toast.makeText(EditFile.this, "The file \""+fileName+"\" has been downloaded to you SDCard", Toast.LENGTH_SHORT).show();
                finish();
                
            }else{
                
                AlertDialog.Builder builder = new AlertDialog.Builder(EditFile.this);
                builder.setMessage("Are you sure you want to open this file type in a text editor?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();
                        
                        editFile.setText(fileContents, TextView.BufferType.SPANNABLE);
                        //editFile.setText(fileContents, TextView.BufferType.SPANNABLE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditFile.this.finish();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                
            }
            
            
            
            
        }
        ext = null;
        fType = null;
        
    }
    
}

public class SaveTask extends AsyncTask<Void, Void, String> {
    
    protected String mServer;
    protected String mUser;
    protected String mPass;
    protected String mType;
    protected String mPath;
    protected String mFTP;
    protected String mPort;
    protected String mFile;
    
    protected SaveTask(String server, String user, String pass,  String path, String file, String sftp, String port) {
        mServer = server;
        mUser = user;
        mPass = pass;
        //mType = type;
        mPath = path;
        mFTP = sftp;
        mPort = port;
        mFile = file;
    }
    
    
    
    @Override
    protected String doInBackground(Void... v) {
        
        return getFeed(mServer, mUser, mPass, mPath, mFile, mFTP, mPort);
        
    }
    
    private String getFeed(String server, String user, String pass,  String path, String file, String sftp, String port) {
        
        String value = "";
        
        String fullPath = path;
        
        FTPClient con = new FTPClient();
        con.setDefaultTimeout(90000);
        con.setConnectTimeout(90000);
        try {
            
            con.connect(server);
            if (con.login(user, pass))
            {
                con.enterLocalPassiveMode(); // important!
                
                
                try {
                    String text = file;
                    InputStream inputStream = new ByteArrayInputStream(text.getBytes("UTF-8"));
                    OutputStream outputStream = con.storeFileStream(fullPath);
                    
                    byte[] buffer = new byte[4096];
                    int lf;
                    while((lf = inputStream.read(buffer))!=-1)
                    {
                        outputStream.write(buffer, 0, lf);
                    }
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                    // Toast.makeText(EditFile.this, "File saved", Toast.LENGTH_SHORT).show();
                    //EditFile.this.finish();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                
                
            }
            
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        return value;
        
        
    }
    
    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(EditFile.this, "",
                                     "Saving...", true);
        
    }
    protected void onPostExecute(String value){
        dialog.cancel();
        
        
    }
    
}

public class SaveSSLTask extends AsyncTask<Void, Void, String> {
    
    protected String mServer;
    protected String mUser;
    protected String mPass;
    protected String mType;
    protected String mPath;
    protected String mFTP;
    protected String mPort;
    protected String mFile;
    
    protected SaveSSLTask(String server, String user, String pass,  String path, String file, String sftp, String port) {
        mServer = server;
        mUser = user;
        mPass = pass;
        //mType = type;
        mPath = path;
        mFTP = sftp;
        mPort = port;
        mFile = file;
    }
    
    
    
    @Override
    protected String doInBackground(Void... v) {
        
        return getFeed(mServer, mUser, mPass, mPath, mFile, mFTP, mPort);
        
    }
    
    private String getFeed(String server, String user, String pass,  String path, String file, String sftp, String port) {
        
        String value = "";
        
        String fullPath = "/"+path;
        
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
            InputStream src = new ByteArrayInputStream(file.getBytes());
            sftpChannel.put(src, fullPath);
            
            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SftpException e) {
            e.printStackTrace();
        }
        
        
        return value;
        
        
    }
    
    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(EditFile.this, "",
                                     "Saving...", true);
        
    }
    protected void onPostExecute(String value){
        dialog.cancel();
        
        
    }
    
}

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
if (keyCode == KeyEvent.KEYCODE_BACK) {
final TabActivity t = (TabActivity)this.getParent();
AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setMessage("Close File?")
.setCancelable(false)
.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int id) {



TabHost tabHost = t.getTabHost();



String fileToSave = tabHost.getCurrentTabTag();
close();
}
})
.setNegativeButton("No", new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int id) {
dialog.cancel();
}
});

AlertDialog alert = builder.create();
alert.show();

return true;
}
return super.onKeyDown(keyCode, event);
}

public static void printException(Exception e)
{
Log.d("AHE", e.getMessage());
}
private void init_highlight()
{
int highlightEnabled = settings.getInt("highlight", 0);
boolean enabled = false;
if(highlightEnabled==1){
enabled = true;
}
enabled = true;
Highlight.setEnabled(enabled);
// kb
int limitSize;
try
{
limitSize = Integer.valueOf(mPref.getString("highlight_limit", Integer.toString(MAX_HIGHLIGHT_FILESIZE)));
}catch (Exception e)
{
limitSize = MAX_HIGHLIGHT_FILESIZE;
//printException(e);
}
Highlight.setLimitFileSize(limitSize);
}
public boolean unpackSyntax()
{
try
{
InputStream is = getAssets().open("syntax.zip");
ZipInputStream zin = new ZipInputStream(is);
ZipEntry ze = null;
String name;
File file;
while ((ze = zin.getNextEntry()) != null)
{
name = ze.getName();
// Log.v("Decompress", "Unzipping " + name);

if(ze.isDirectory())
{
file = new File(TEMP_PATH + File.separator + name);
if(!file.exists())
{
if(!file.mkdir())
{
return false;
}
}
}else
{
FileOutputStream fout = new FileOutputStream(TEMP_PATH + File.separator + name);
byte[] buf = new byte[1024 * 4];
int len;
while ((len = zin.read(buf)) > 0)
{
fout.write(buf, 0, len);
}
buf = null;
zin.closeEntry();
fout.close();
}

}
zin.close();
}catch (Exception e)
{
e.printStackTrace();
return false;
}
return true;
}


public static void writeFile(String path, String text)
{
writeFile(path, text, "UTF-8", true);
}

public static boolean writeFile(String path, String text, String encoding, boolean isRoot)
{
try
{
File file = new File(path);
String tempFile = EditFile.TEMP_PATH + "/temp.root.file";
String fileString = path;
boolean root = false;
if(!file.canWrite() && isRoot)
{
//Ã©Å“â‚¬Ã¨Â¦ï¿½RootÃ¦ï¿½Æ’Ã©â„¢ï¿½Ã¥Â¤â€žÃ§ï¿½â€
fileString = tempFile;
root = true;
}
BufferedWriter bw = null;
bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileString), Charset.forName(encoding)));
bw.write(text);
bw.close();

return true;
}catch (Exception e)
{
e.printStackTrace();
return false;
}
}




}