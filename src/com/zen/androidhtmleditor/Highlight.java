package com.zen.androidhtmleditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.text.Editable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;


class Highlight{
	
	public static Pattern keywords = Pattern.compile("(?<!\\w|$|%|@|>)(and|or|xor|for|do|while|foreach|as|return|die|exit|if|then|else|elseif|new|delete|try|throw|catch|finally|class|function|string|array|object|resource|var|bool|boolean|int|integer|float|double|real|string|array|global|const|static|public|private|protected|published|extends|switch|true|false|null|void|this|self|struct|char|signed|unsigned|short|long)(?!\\w|=\")");
	public static Pattern variables = Pattern.compile("(?<!\\w)((\\$|\\%|\\@)(\\-&gt;|\\w)+)(?!\\w)");
	public static Pattern strings = Pattern.compile("(?s)(\"|\')(.*?)(\"|\')",Pattern.MULTILINE);
	public static Pattern comments = Pattern.compile("(?<!(\'|\"|:|\\-|[A-Za-z0-9])( )?)(//|#).*(?!\\n)");
	
	public static Pattern CSSVariables = Pattern.compile("(?s)(\\{)(.*?)(\\})",Pattern.MULTILINE);
	public static Pattern CSSParts = Pattern.compile("(?<!\\w>)(\\{|\\}|\\:|\\;)(?!\\w)");
	
	public static String PREFS_NAME="DEVTOOLS_PREF";
	
    private static int mKeywords = 0xffffffff;
    private static int mVariables = 0xffffffff;
    private static int mComments = 0xffffffff;
    private static int mStrings = 0xffffffff;
    
    private static String mExt = "";
	
	public static boolean mStop = true;
    private static int mEndOffset = -1;
    private static int mStartOffset = -1;
    private static boolean mClear = true;
    
    private static boolean mHighlightOff = false;
    
	public static void stop()
    {
        mStop = true;
    }
    
    public static void redraw()
    {
        mStop = false;
        mEndOffset = -1;
        mStartOffset = -1;
    }
 
    public static void setExt(String ext){
    	mExt = ext;
    }
    public static void setKeywords(int col){
    	mKeywords = col;
    }
    public static void setVariables(int col){
    	mVariables = col;
    }
    public static void setComments(int col){
    	mComments = col;
    }
    public static void setStrings(int col){
    	mStrings = col;
    }

    public static void turnOn(){
    	mHighlightOff = false;
    }
    public static void turnOff(){
    	mHighlightOff = true;
    }   
    
    
    public static void singleLineDraw(Editable e, int start, int end){
    	mStop = false;
    	mClear = false;
    	Highlight.highlight(e, start, end);
    }
	
	public static boolean highlight(Editable e, int start, int end){

		if(mHighlightOff)
			return false;
		
		if(mStop)
            return false;
		 
		

		
		//Lock this bitch up
		mStop = true;
		
		 mStartOffset = start;
	     mEndOffset = end;
	   
		
		Spannable str = e;
		
			if(mClear){
				ForegroundColorSpan[] spans = e.getSpans(0, e.length() , ForegroundColorSpan.class);
				for(ForegroundColorSpan s : spans){
					e.removeSpan(s);
				}
			}
		
		CharSequence subStr = str.subSequence(start, end);
			
	    
	    //Keywords
		Matcher matcher = keywords.matcher(subStr);
	    while (matcher.find()) {
	    	str.setSpan(new ForegroundColorSpan(mKeywords), start+matcher.start(), start+matcher.start()+matcher.group(0).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	    
	    //Variables
		    
	    Matcher matcher2 = variables.matcher(subStr);
	    if(mExt.equals("css")){
	    	matcher2 = CSSVariables.matcher(subStr);
	    }
	    while (matcher2.find()) {
	    	str.setSpan(new ForegroundColorSpan(mVariables), start+matcher2.start(), start+matcher2.start()+matcher2.group(0).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}   	
	    
		 
	    //strings
	    Matcher codeMatch4 = strings.matcher(subStr);
	    if(mExt.equals("css")){
	    	codeMatch4 = CSSParts.matcher(subStr);
	    }
	    while (codeMatch4.find()) {
		    str.setSpan(new ForegroundColorSpan(mStrings), start+codeMatch4.start(), start+codeMatch4.start()+codeMatch4.group(0).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    }
	    
	    if(!mExt.equals("css")){
	    //Comments
	    Matcher codeMatch = comments.matcher(subStr);
	    
	    while (codeMatch.find()) {
	    	
		    str.setSpan(new ForegroundColorSpan(mComments), start+codeMatch.start(), start+codeMatch.start()+codeMatch.group(0).length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    }
	    }
	    mClear = true;
	    mStop = false;
        return true;
    
	}
}