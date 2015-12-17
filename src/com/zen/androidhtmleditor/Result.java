package com.zen.androidhtmleditor;

import com.google.gson.annotations.SerializedName;

public class Result {
    
    @SerializedName("server")
    public String serverName;
 
    @SerializedName("accountName")
    public String accountName;
    
    @SerializedName("user")
    public String userName;
 
    @SerializedName("pass")
    public String passWord;

    @SerializedName("sftp")
    public String sftp;

    @SerializedName("port")
    public String port;

    
    
    //Colors
    @SerializedName("themeName")
    public String themeName;
 
    @SerializedName("keywordColor")
    public String keywordColor;
    
    @SerializedName("variableColor")
    public String variableColor;
    
    @SerializedName("commentColor")
    public String commentColor;  
    
    @SerializedName("stringColor")
    public String stringColor;  
    
    @SerializedName("backgroundColor")
    public String backgroundColor;   
    
    @SerializedName("textColor")
    public String textColor;  
}