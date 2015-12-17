package com.zen.androidhtmleditor;
 
import java.util.List;

import com.zen.androidhtmleditor.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class FetchAdapter extends ArrayAdapter<String[]> {
	
    int resource;
    String response;
    Context context;
    int screenWidth;
    //String postId;
    //Initialize adapter
    public FetchAdapter(Context context, int resource, List<String[]> items) {
        super(context, resource, items);
        this.resource=resource;
        this.context=context;
        
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout alertView;
        //Get the current alert object
        final String[] al = getItem(position);
        

        //Inflate the view
        if(convertView==null)
        {
            alertView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(resource, alertView, true);
        }
        else
        {
            alertView = (LinearLayout) convertView;
        }

        
        TextView fileFolder =(TextView)alertView.findViewById(R.id.fileFolderName);
        fileFolder.setText(al[0].toString());
        
        ImageView type =(ImageView)alertView.findViewById(R.id.type);
        
        TextView typeId =(TextView)alertView.findViewById(R.id.typeId);
        
       
        
        
        
       if(al[1].toString().equals("folder")){
    	   type.setImageResource(R.drawable.hfolder);
    	   typeId.setText("folder");
    	   
       }else{
    	   type.setImageResource(R.drawable.hfile);
    	   typeId.setText("file");
    	 
    	  }
       
       

        return alertView;
    }
    
    
    
}