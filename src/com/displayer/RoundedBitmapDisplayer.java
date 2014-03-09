package com.displayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bitmaphandler.R;

public class RoundedBitmapDisplayer extends ImageView {

	private int roundPixels = 10;

	public RoundedBitmapDisplayer(Context context) {
        super(context);
        roundPixels = 10;
    }

    public RoundedBitmapDisplayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        roundPixels = 10;
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.com_displayer_RoundedBitmapDisplayer);
        roundPixels = a.getInt(R.styleable.com_displayer_RoundedBitmapDisplayer_round_pixel, 10);
        a.recycle();
    }
    
    public RoundedBitmapDisplayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        roundPixels = 20;
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.com_displayer_RoundedBitmapDisplayer);
        roundPixels = a.getInt(R.styleable.com_displayer_RoundedBitmapDisplayer_round_pixel, 10);
        a.recycle();
	}
	
	public void setRoundPixle(int roundPixels) {
		this.roundPixels = roundPixels;
	}
	
	public int getRoundPixle() {
		return roundPixels;
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
        
//        if(drawable == null){
//        	super.setImageDrawable(null);
//        	notifyDrawable(previousDrawable, false);
//        	return;
//        }
//        
//        if (drawable instanceof RecyclingBitmapDrawable) {
//        	drawable = new RecyclingBitmapDrawable(getResources(), roundCorners(((RecyclingBitmapDrawable) drawable).getBitmap(), this, roundPixels));
//        }else{
//        	drawable = new RecyclingBitmapDrawable(getResources(), roundCorners(((BitmapDrawable) drawable).getBitmap(), this, roundPixels));
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
        
        Bitmap bitmap =  ((BitmapDrawable)drawable).getBitmap() ;
        
        if(bitmap == null)
			return;
        
        Bitmap roundBitmap = roundCorners(bitmap, this, roundPixels);
        
		canvas.drawBitmap(roundBitmap, 0,0, null);
    }

	/**
	 * Process incoming {@linkplain Bitmap} to make rounded corners according to target
	 * @param bitmap      Incoming Bitmap to process
	 * @param imageView  Target to display bitmap in
	 * @param roundPixels Rounded pixels of corner
	 * @return Result bitmap with rounded corners
	 */
	public static Bitmap roundCorners(Bitmap bitmap, ImageView imageView, int roundPixels) {
		if (imageView == null) {
			return bitmap;
		}
		
		if(bitmap == null)
			return null;

		Bitmap roundBitmap;

		int bw = bitmap.getWidth();
		int bh = bitmap.getHeight();
		int vw = imageView.getWidth();
		int vh = imageView.getHeight();
		if (vw <= 0) vw = bw;
		if (vh <= 0) vh = bh;

		final ImageView.ScaleType scaleType = imageView.getScaleType();
		if (scaleType == null) {
			return bitmap;
		}

		int width, height;
		Rect srcRect;
		Rect destRect;
		switch (scaleType) {
			case CENTER_INSIDE:
				float vRation = (float) vw / vh;
				float bRation = (float) bw / bh;
				int destWidth;
				int destHeight;
				if (vRation > bRation) {
					destHeight = Math.min(vh, bh);
					destWidth = (int) (bw / ((float) bh / destHeight));
				} else {
					destWidth = Math.min(vw, bw);
					destHeight = (int) (bh / ((float) bw / destWidth));
				}
				int x = (vw - destWidth) / 2;
				int y = (vh - destHeight) / 2;
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(x, y, x + destWidth, y + destHeight);
				width = vw;
				height = vh;
				break;
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			default:
				vRation = (float) vw / vh;
				bRation = (float) bw / bh;
				if (vRation > bRation) {
					width = (int) (bw / ((float) bh / vh));
					height = vh;
				} else {
					width = vw;
					height = (int) (bh / ((float) bw / vw));
				}
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER_CROP:
				vRation = (float) vw / vh;
				bRation = (float) bw / bh;
				int srcWidth;
				int srcHeight;
				if (vRation > bRation) {
					srcWidth = bw;
					srcHeight = (int) (vh * ((float) bw / vw));
					x = 0;
					y = (bh - srcHeight) / 2;
				} else {
					srcWidth = (int) (vw * ((float) bh / vh));
					srcHeight = bh;
					x = (bw - srcWidth) / 2;
					y = 0;
				}
				width = srcWidth;// Math.min(vw, bw);
				height = srcHeight;//Math.min(vh, bh);
				srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
				destRect = new Rect(0, 0, width, height);
				break;
			case FIT_XY:
				width = vw;
				height = vh;
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER:
			case MATRIX:
				width = Math.min(vw, bw);
				height = Math.min(vh, bh);
				x = (bw - width) / 2;
				y = (bh - height) / 2;
				srcRect = new Rect(x, y, x + width, y + height);
				destRect = new Rect(0, 0, width, height);
				break;
		}

		try {
			roundBitmap = getRoundedCornerBitmap(bitmap, roundPixels, srcRect, destRect, width, height);
		} catch (OutOfMemoryError e) {
			roundBitmap = bitmap;
		}

		return roundBitmap;
	}

	private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixels, Rect srcRect, Rect destRect, int width,
												 int height) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		final Paint paint = new Paint();
		final RectF destRectF = new RectF(destRect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFF000000);
		canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

		return output;
	}
}
