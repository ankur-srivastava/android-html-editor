package com.zen.androidhtmleditor.highlight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.util.Log;

import com.zen.androidhtmleditor.util.ColorScheme;
import com.zen.androidhtmleditor.EditFile;
import com.zen.androidhtmleditor.util.ForegroundColorSpan;

public class Highlight
{
    static {
        System.loadLibrary("highlight");
    }
    
    public static final int GROUP_TAG_ID        = 1;
    public static final int GROUP_COMMENT_ID    = 2;
    public static final int GROUP_STRING_ID     = 3;
    public static final int GROUP_KEYWORD_ID    = 4;
    public static final int GROUP_FUNCTION_ID   = 5;
    public static final int GROUP_ATTR_NAME_ID  = 6;

    
    private final static String TAG = "Highlight";
    private static HashMap<String, String[]> langTab;
    private static ArrayList<String[]> nameTab;
    private static int color_tag;
    private static int color_string;
    private static int color_keyword;
    private static int color_function;
    private static int color_comment;
    private static int color_attr_name;
    private String mExt = "php";
    private int mEndOffset = -1;
    private int mStartOffset = -1;
    private boolean mStop = true;
    private static boolean mEnabled = true; 
    private static int mLimitFileSize = 0; 
    private static ArrayList<ForegroundColorSpan> mSpans = new ArrayList<ForegroundColorSpan>();
    
    public static void init()
    {
        loadLang();
        loadColorScheme();
    }
    
    public void setSyntaxType(String file_extension)
    {
        this.mExt = file_extension;
    }
    
    public void stop()
    {
        this.mStop = true;
    }
    
    public static void setLimitFileSize(int kb)
    {
        mLimitFileSize = kb;
    }
    
    public static int getLimitFileSize()
    {
        return mLimitFileSize;
    }
    
    public static void setEnabled(boolean enabled)
    {
        mEnabled = enabled;
    }
    
    public void redraw()
    {
        this.mStop = false;
        this.mEndOffset = -1;
        this.mStartOffset = -1;
    }
    
    /**
     * 
     * @param j 
     * @param mLayout 
     * @return è¿”å›ž[[é«˜äº®ç±»åž‹,å¼€å§‹offset, ç»“æ�Ÿoffset],,]
     */
    public boolean render(Editable mText, int startOffset, int endOffset)
    {
        if(!mEnabled || langTab == null)
            return false;

        if(this.mStop || "".equals(this.mExt))
            return false;
        
        if(this.mStartOffset <= startOffset && this.mEndOffset >= endOffset)
            return false;
        String[] lang = langTab.get(this.mExt);
        if(lang == null)
        {
            return false;
        }
        //lock it ä¸�ç„¶ä¼šå› ä¸ºæ·»åŠ äº†spanå�Žå¯¼è‡´offsetæ”¹å�˜ï¼Œä¸�æ–­åœ°è¿›è¡Œé«˜äº®
        this.mStop = true;
        //TimerUtil.start();
        //Log.d(TAG, startOffset+"="+endOffset);
        
        this.mStartOffset = startOffset;
        this.mEndOffset = endOffset;
        String text = mText.subSequence(0, endOffset).toString();
        int[] ret = jni_parse(text, EditFile.TEMP_PATH + File.separator + lang[1]);
        //TimerUtil.stop("hg parse");
        if(ret == null)
        {
            this.mStop = false;
            return false;
        }
        int len = ret.length;
        if(len < 1 || len % 3.0F != 0)
        {
            this.mStop = false;
            return false;
        }
        
        //TimerUtil.start();
        //ä¸�èƒ½æ¸…é™¤å…¨é™ªï¼Œå› ä¸ºæ»šåŠ¨æ�¡éœ€è¦�ä¸€ä¸ªspanæ�¥æŒ‰ä½�æ‹–åŠ¨
        //mText.clearSpans();
        
        int color;
        int start;
        int end;
        int index=0;
        int bufLen = mSpans.size();
        ForegroundColorSpan fcs;
        for(ForegroundColorSpan fcs2:mSpans)
        {
            mText.removeSpan(fcs2);
        }
        for(int i=0; i<len; i++)
        {
            
            switch(ret[i])
            {
                case GROUP_TAG_ID:
                    color = color_tag;
                    break;
                case GROUP_STRING_ID:
                    color = color_string;
                    break;
                case GROUP_KEYWORD_ID:
                    color = color_keyword;
                    break;
                case GROUP_FUNCTION_ID:
                    color = color_function;
                    break;
                case GROUP_COMMENT_ID:
                    color = color_comment;
                    break;
                case GROUP_ATTR_NAME_ID:
                    color = color_attr_name;
                    break;
                default:
                    Log.d(TAG, "èŽ·å�–é¢œè‰²group idå¤±è´¥");
                    mStop = false;
                    return false;
            }
            
            start = ret[++i];
            end   = ret[++i];
            
            if(end < startOffset)
                continue;
            
            if(index >= bufLen)
            {
                fcs = new ForegroundColorSpan(color);
                mSpans.add(fcs);
            } else {
                fcs = mSpans.get(index);
                fcs.setColor(color);
            }
            
            ++index;
            mText.setSpan(fcs, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            
        }
        ret = null;
        //TimerUtil.stop("hg 1");
        this.mStop = false;
        return true;
    }
    
    /**
     * [{è¯­æ³•å��ç§°,å…¶ä¸­ä¸€ä¸ªæ‰©å±•å��},,]
     */
    public static ArrayList<String[]> getLangList()
    {
        return nameTab;
    }
    
    public static boolean loadLang()
    {
        String langfile = EditFile.TEMP_PATH + "/lang.conf";
        File file = new File(langfile);
        if(!file.isFile())
        {
            return false;
        }
        file = null;
        langTab = new HashMap<String, String[]>();
        nameTab = new ArrayList<String[]>();

        try
        {
            byte[] mByte = readFile(langfile);
            String mData = new String(mByte, "utf-8");
            String[] lines = mData.split("\n");
            String[] cols;
            for(String line:lines)
            {
                line = line.trim();
                if(line.startsWith("#"))
                    continue;
                cols = line.split(":");
                String name = cols[0].trim();
                String synfile = cols[1].trim();
                String extsString = cols[2].trim();
                String[] exts = extsString.split("\\s+");
                nameTab.add(new String[] {name, exts[0]});
                for(String ext:exts)
                {
                    langTab.put(ext, new String[]{name, synfile});
                }
            }
            mByte = null;
        }catch (Exception e)
        {
            return false;
        }
        
        return true;
    }
    
    public static void loadColorScheme()
    {
    	
    	//ColorScheme.set(settings,"dark");
        
    	
        //è‰²å½©æ¨¡å�—
        /*color_tag            = Color.parseColor(ColorScheme.color_tag);
        color_string         = Color.parseColor(ColorScheme.color_string);
        color_keyword        = Color.parseColor(ColorScheme.color_keyword);
        color_function       = Color.parseColor(ColorScheme.color_function);
        color_comment        = Color.parseColor(ColorScheme.color_comment);
        color_attr_name      = Color.parseColor(ColorScheme.color_attr_name);
        */
    	 color_tag            = ColorScheme.color_tag;
         color_string         = ColorScheme.color_string;
         color_keyword        = ColorScheme.color_keyword;
         color_function       = ColorScheme.color_function;
         color_comment        = ColorScheme.color_comment;
         color_attr_name      = ColorScheme.color_attr_name;
        
    }
    
    public static String getNameByExt(String ext)
    {
        String[] info = langTab.get(ext);
        if(info == null)
        {
            return "";
        }
        return info[0];
    }
    
    public static byte[] readFile(String file)
    {
        byte[] ret;
        ret = read_file(file);
        return ret;
    }
    
    public static String readFile(String file, String encoding)
    {
        try
        {
            byte[] mByte = readFile(file);
            return new String(mByte, encoding);
        }catch (Exception e)
        {
            try {
                return ReadFile(file, encoding);
            }catch (Exception e2) {
                return "";
            }
        }
    }


    public static String ReadFile(String filename, String encoding)
    {
        return ReadFile(new File(filename), encoding);
    }
    
    public static String ReadFile(File filename, String encoding)
    {
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            return ReadFile(fis, encoding);
        }catch (FileNotFoundException e)
        {
            return "";
        }
    }
    
    public static String ReadFile(InputStream fis, String encoding)
    {
        BufferedReader br;
        StringBuilder b = new StringBuilder();
        String line;
        String sp = System.getProperty("line.separator");

        try
        {
            br = new BufferedReader(new InputStreamReader(fis, encoding));
            try
            {
                while ((line = br.readLine()) != null)
                {
                    b.append(line).append(sp);
                }
                br.close();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        return b.toString();
    }
    
    
    private native static byte[] read_file(String file);
    
    private native static int[] jni_parse(String text, String syntaxFile);

}
