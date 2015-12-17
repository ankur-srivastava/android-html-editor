package com.zen.androidhtmleditor.util;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

//
public class CheckedText extends CheckedTextView {

public CheckedText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

@Override
protected void onDraw(Canvas canvas) {
    // draw the shadow
    //getPaint().setShadowLayer(2, -2, 1, Color.BLACK); // or whatever shadow you use
    //getPaint().setShader(null);
    //super.onDraw(canvas);

    // draw the gradient filled text
    //getPaint().clearShadowLayer();
   getPaint().setShader(new LinearGradient(0, getHeight(), 0, 0, Color.parseColor("#333333"), Color.parseColor("#333333"), TileMode.CLAMP)); // or whatever gradient/shader you use
   super.onDraw(canvas);
}


}