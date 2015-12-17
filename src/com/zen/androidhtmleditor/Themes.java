package com.zen.androidhtmleditor;




import java.util.List;

import com.google.gson.Gson;
import com.zen.androidhtmleditor.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Themes extends Activity {

        
        public static String PREFS_NAME="DEVTOOLS_PREF";
        
        private int mKeywords = 0xffffffff;
        private int mVariables = 0xffffffff;
        private int mComments = 0xffffffff;
        private int mStrings = 0xffffffff;
        private int mBackground = 0xffffffff;
        private int mText = 0xffffffff;
        private String mThemeName;
        


        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.themes);
                
               
                
                
                TextView keywords = (TextView)findViewById(R.id.keywords);
                TextView variables = (TextView)findViewById(R.id.variables);
                TextView comments = (TextView)findViewById(R.id.comments);
                TextView strings = (TextView)findViewById(R.id.strings);
                TextView text = (TextView)findViewById(R.id.texts);
                TextView background = (TextView)findViewById(R.id.background);
                Button save = (Button)findViewById(R.id.save);
                
                keywords.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	final ColorPickerDialog d = new ColorPickerDialog(Themes.this, mKeywords);
		            	d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setAlphaSliderVisible(true);
                        
                        d.setButton("Ok", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                     ;
                                		mKeywords = d.getColor();
                                		
                                		
                                }
                        });

                        d.setButton2("Cancel", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                }
                        });

                        d.show();
		            }
                });
                
                
                variables.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	final ColorPickerDialog d = new ColorPickerDialog(Themes.this, mVariables);
	                    d.setAlphaSliderVisible(true);

	                    d.setButton("Ok", new DialogInterface.OnClickListener() {

	                            public void onClick(DialogInterface dialog, int which) {

	                               
	                            	mVariables = d.getColor();
	                            	
	                            }
	                    });

	                    d.setButton2("Cancel", new DialogInterface.OnClickListener() {

	                            public void onClick(DialogInterface dialog, int which) {

	                            }
	                    });

	                    d.show();
		            }
                });
                
                
                comments.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	final ColorPickerDialog d = new ColorPickerDialog(Themes.this, mComments);
		                d.setAlphaSliderVisible(true);

		                d.setButton("Ok", new DialogInterface.OnClickListener() {

		                        public void onClick(DialogInterface dialog, int which) {

		                
		                        	mComments = d.getColor();
		                        	
		                        }
		                });

		                d.setButton2("Cancel", new DialogInterface.OnClickListener() {

		                        public void onClick(DialogInterface dialog, int which) {

		                        }
		                });

		                d.show();
		            }
                });
                
                strings.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	 final ColorPickerDialog d = new ColorPickerDialog(Themes.this, mStrings);
		                 d.setAlphaSliderVisible(true);

		                 d.setButton("Ok", new DialogInterface.OnClickListener() {

		                         public void onClick(DialogInterface dialog, int which) {

		                          
		                         	mStrings = d.getColor();
		                         
		                         }
		                 });

		                 d.setButton2("Cancel", new DialogInterface.OnClickListener() {

		                         public void onClick(DialogInterface dialog, int which) {

		                         }
		                 });

		                 d.show();
		            }
                });
                
                background.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	final ColorPickerDialog d = new ColorPickerDialog(Themes.this, mBackground);
		                d.setAlphaSliderVisible(true);

		                d.setButton("Ok", new DialogInterface.OnClickListener() {

		                        public void onClick(DialogInterface dialog, int which) {

		                          
		                        	mBackground = d.getColor();
		                        	
		                        }
		                });

		                d.setButton2("Cancel", new DialogInterface.OnClickListener() {

		                        public void onClick(DialogInterface dialog, int which) {

		                        }
		                });

		                d.show();
		            }
                });
                
                
                text.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	 final ColorPickerDialog d = new ColorPickerDialog(Themes.this, mText);
		            	    d.setAlphaSliderVisible(true);

		            	    d.setButton("Ok", new DialogInterface.OnClickListener() {

		            	            public void onClick(DialogInterface dialog, int which) {

		            	                   
		            	            	mText = d.getColor();
		            	            	
		            	            }
		            	    });

		            	    d.setButton2("Cancel", new DialogInterface.OnClickListener() {

		            	            public void onClick(DialogInterface dialog, int which) {

		            	            }
		            	    });

		            	    d.show();
		            	
		            }
                });
                
                save.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View v) {
		            	AlertDialog.Builder alert = new AlertDialog.Builder(Themes.this);

		            	alert.setTitle("Theme Name");
		            	alert.setMessage("Message");

		            	// Set an EditText view to get user input 
		            	final EditText input = new EditText(Themes.this);
		            	input.setText(mThemeName);
		            	alert.setView(input);

		            	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		            	public void onClick(DialogInterface dialog, int whichButton) {
		            	  mThemeName = input.getText().toString();
		            	  
		            	  SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		            	  String customThemes = settings.getString("Themes", "");
		            	  
		            	  String jSonColor = "{\"themeName\":\""+mThemeName+"\",";
		            	  jSonColor += "\"keywordColor\":\""+String.valueOf(mKeywords)+"\",";
		            	  jSonColor += "\"variableColor\":\""+String.valueOf(mVariables)+"\",";
		            	  jSonColor += "\"commentColor\":\""+String.valueOf(mComments)+"\",";
		            	  jSonColor += "\"stringColor\":\""+String.valueOf(mStrings)+"\",";
		            	  jSonColor += "\"backgroundColor\":\""+String.valueOf(mBackground)+"\",";
		            	  jSonColor += "\"textColor\":\""+String.valueOf(mText)+"\"";
		            	  jSonColor += "}";
		            	  
		            	
		            	  
		              	String jSonStart = "{\"data\":[";
		              	String jSonEnd = "]}";
		              	String jSonSaveColor = "";
		              	
		              	if(customThemes.equals("")){
		              		jSonSaveColor = jSonStart+jSonColor+jSonEnd;
		              	}else{
		              		
		              		Gson gson = new Gson();
		        	        SearchResponse response = gson.fromJson(customThemes, SearchResponse.class);
		        	        List<Result> results = response.data;
		        	        
		        	        for(Result l : results){
		        	        	
		        	        	jSonColor += ",{\"themeName\":\""+l.themeName+"\",";
		        	        	jSonColor += "\"keywordColor\":\""+String.valueOf(l.keywordColor)+"\",";
		        	        	jSonColor += "\"variableColor\":\""+String.valueOf(l.variableColor)+"\",";
		        	        	jSonColor += "\"commentColor\":\""+String.valueOf(l.commentColor)+"\",";
		        	        	jSonColor += "\"stringColor\":\""+String.valueOf(l.stringColor)+"\",";
		        	        	jSonColor += "\"backgroundColor\":\""+String.valueOf(l.backgroundColor)+"\",";
		        	        	jSonColor += "\"textColor\":\""+String.valueOf(l.textColor)+"\"";
		        	        	jSonColor += "}";
		        	        }
		        	        jSonSaveColor = jSonStart+jSonColor+jSonEnd;
		        	        
		              	}
		              	Log.i("SavedTheme",jSonSaveColor);
		              	SharedPreferences.Editor editor = settings.edit();
		        		editor.putString("Themes", jSonSaveColor); 
		        		editor.commit(); 
		        		finish();
		            	  
		            	  }
		            	});

		            	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		            	  public void onClick(DialogInterface dialog, int whichButton) {
		            	   
		            	  }
		            	});

		            	alert.show();
		            }
                });
               

        }

       

        

        

      
        
        
}