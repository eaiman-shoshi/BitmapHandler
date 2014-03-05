package com.displayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.bitmaphandler.R;

public class CircleImageView  extends ImageView {

	private static float strokeWidth = 5f;
	private static float strokeColor = 5f;
	
	public CircleImageView(Context context) {
        super(context);
        strokeWidth = 5f;
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        strokeWidth = 5f;
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.com_displayer_CircleImageView);
        strokeWidth = a.getFloat(R.styleable.com_displayer_CircleImageView_stroke_width, 5f);
        String tempColor = a.getString(R.styleable.com_displayer_CircleImageView_stroke_color);
        Log.d("CircularImageView", "ColorTag: "+tempColor);
        
        if(tempColor != null && !tempColor.equalsIgnoreCase("#0")){
        	String black = tempColor.replaceAll("0", "");
        	String white = tempColor.replaceAll("f", "");
        	white = white.replaceAll("F", "");
        	
        	if(black.equalsIgnoreCase("#")){
        		tempColor = "#000000";
        	}
        	
        	if(white.equalsIgnoreCase("#")){
        		tempColor = "#ffffff";
        	} 
        	
        	strokeColor = Color.parseColor(tempColor);
        }else{
        	strokeColor = Color.TRANSPARENT;
        }
        a.recycle();
    }
    
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        strokeWidth = 5f;
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.com_displayer_CircleImageView);
        strokeWidth = a.getFloat(R.styleable.com_displayer_CircleImageView_stroke_width, 5f);
        String tempColor = a.getString(R.styleable.com_displayer_CircleImageView_stroke_color);
        Log.d("CircularImageView", "ColorTag: "+tempColor);
        
        if(tempColor != null && !tempColor.equalsIgnoreCase("#0")){
        	String black = tempColor.replaceAll("0", "");
        	String white = tempColor.replaceAll("f", "");
        	white = white.replaceAll("F", "");
        	
        	if(black.equalsIgnoreCase("#")){
        		tempColor = "#000000";
        	}
        	
        	if(white.equalsIgnoreCase("#")){
        		tempColor = "#ffffff";
        	} 
        	
        	strokeColor = Color.parseColor(tempColor);
        }else{
        	strokeColor = Color.TRANSPARENT;
        }
        a.recycle();
    }
	
	public void setStroke(float strokeWidth, int strokeColor){
		CircleImageView.strokeWidth = strokeWidth;
		CircleImageView.strokeColor = strokeColor;
	}
	
	/**
     * @see android.widget.ImageView#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        // This has been detached from Window, so clear the drawable
        setImageDrawable(null);

        super.onDetachedFromWindow();
    }
    
    /**
     * @see android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        // Keep hold of previous Drawable
        final Drawable previousDrawable = getDrawable();
        
//        if (drawable instanceof RecyclingBitmapDrawable) {
//        	drawable = new RecyclingBitmapDrawable(getResources(), roundCorners(((RecyclingBitmapDrawable) drawable).getBitmap(), this));
//        }else{
//        	drawable = new RecyclingBitmapDrawable(getResources(), roundCorners(((BitmapDrawable) drawable).getBitmap(), this));
//        }

        // Call super to set new Drawable
        super.setImageDrawable(drawable);

        // Notify new Drawable that it is being displayed
        notifyDrawable(drawable, true);

        // Notify old Drawable so it is no longer being displayed
        notifyDrawable(previousDrawable, false);
    }
    
    /**
     * Notifies the drawable that it's displayed state has changed.
     *
     * @param drawable
     * @param isDisplayed
     */
    private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
        if (drawable instanceof RecyclingBitmapDrawable) {
            // The drawable is a CountingBitmapDrawable, so notify it
            ((RecyclingBitmapDrawable) drawable).setIsDisplayed(isDisplayed);
        } else if (drawable instanceof LayerDrawable) {
            // The drawable is a LayerDrawable, so recurse on each layer
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
            }
        }
    }
    
    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return; 
        }
        
        if(drawable instanceof TransitionDrawable){
        	drawable = ((TransitionDrawable) drawable).getDrawable(1);
        }
        
        Bitmap b =  ((BitmapDrawable)drawable).getBitmap() ;
        if(b == null){
        	return;
        }
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
//        b.recycle();

        int w = getWidth(), h = getHeight();
        
        //border
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor((int) strokeColor);
        canvas.drawCircle(w / 2+0.7f, h / 2+0.7f, w / 2+0.1f, paint);

        Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
        
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 255, 255, 255);
        paint.setColor(Color.parseColor("#7f97d2"));
        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
                sbmp.getWidth() / 2-strokeWidth, paint); //2-9f
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
        
//        Paint pTouch = new Paint(Paint.ANTI_ALIAS_FLAG);         
//	    pTouch.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); 
//	    pTouch.setAlpha(255);
//	    pTouch.setColor(Color.TRANSPARENT);
//	    pTouch.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL));
	    
//	    canvas.drawCircle(sbmp.getWidth() / 2-.6f, sbmp.getHeight() / 2, ((float) Math.ceil((sbmp.getHeight()+15)/11))+.2f, pTouch);
//	    canvas.drawCircle(sbmp.getWidth() / 2-.6f, sbmp.getHeight() / 2, 57, pTouch);
        return output;
    }
}
