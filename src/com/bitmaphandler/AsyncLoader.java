package com.bitmaphandler;
//package com.bitmaphandler;
//
//import java.io.File;
//import java.lang.ref.WeakReference;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.TransitionDrawable;
//import android.util.Log;
//import android.widget.ImageView;
//
//import com.displayer.CircleImageView;
//import com.displayer.RecyclingBitmapDrawable;
//import com.displayer.RoundedBitmapDisplayer;
//import com.utils.ImageCache;
//import com.utils.Utils;
//
//public class AsyncLoader {
//	private static String TAG = "AsyncImageLoader";
//	private static int FADE_IN_TIME = 200;
//	
//	protected boolean mPauseWork = false;
//	private boolean mFadeInBitmap = true;
//	private boolean mExitTasksEarly = false;
//	private boolean useLoadingImage = true;
//	
//	private final Object mPauseWorkLock = new Object();  
//	   
//    protected Resources mResources;  
//    private Bitmap mLoadingBitmap = null; 
//    private ImageCache imageCache = null;
//    
//    private static final int MESSAGE_CLEAR = 0;
//    private static final int MESSAGE_INIT_DISK_CACHE = 1;
//    private static final int MESSAGE_FLUSH = 2;
//    private static final int MESSAGE_CLOSE = 3;
//	
//    /**
//     * This the default constructor. Images will be handled by the default cache directory</br>
//     * For saving images in a file use <b>AsyncLoader(Context context, File file)</b>
//     * @param context
//     * @see a AsyncLoader(Context context, File file)
//     */
//	public AsyncLoader(Context context){
//		mResources = context.getResources();
//		imageCache = new ImageCache(context);
//	}
//	
//	/**
//	 * Imaegs will be saved in the given directory
//	 * @param context
//	 * @param file
//	 * @see AsyncLoader(Context context)
//	 */
//	public AsyncLoader(Context context, File file){
//		mResources = context.getResources();
//		if(!file.exists()){
//			if(!file.mkdirs()){
//				imageCache = new ImageCache(context);
//				Log.w(TAG, "Unable to identify given file. Images will be handled by the default cache directory");
//			}else{
//				imageCache = new ImageCache(context, file);
//			}
//		}else{
//			imageCache = new ImageCache(context, file);
//		}		
//	}
//	
//	/**
//     * Set placeholder bitmap that shows when the the background thread is running.
//     *
//     * @param resId
//     */
//    public void setLoadingImage(int resId) {
//        mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
//    }
//	
//	public interface Callback{
//		void getDrawable(Drawable drawable, String name);
//	}
//		
//	/**
//     * If set to true, the image will fade-in once it has been loaded by the background thread.
//     */
//    public void setImageFadeIn(boolean fadeIn) {
//        mFadeInBitmap = fadeIn;
//    }	
//    
//    public void setImageFadeInTime(int milliSecond) {
//        FADE_IN_TIME = milliSecond;
//    }
//    
//    /**
//     * If set to true, the loading image will be used as background for
//     * fade-in effect.
//     * @param boolean what
//     */
//    public void useLoadingImageForFadein(boolean what){
//    	useLoadingImage = what;
//    }
//	
//    /**
//     * Images will be downloaded from internet and desplayed on the ImageView.</br>
//     * If fitExactly is true, then the image Width Height will be the same as the ImageView Width and Height.
//     *  Ohterwise the image will decoded with respect to the Width and Height of the given ImageView.</br>  
//     * Both callback and imageview cannot be  null.</br>
//     * If no imageView is provided, then the default image will be pass to the callback.</br>
//	 * URL string cannot be null.</br>
//	 * If callback is provided, the image will be provided to the callback as a drawable.
//	 * </br></br> 
//	 * <b>Important:</b> If imageview is null, the image will not be optimized. which mean, you are only trying to 
//	 * get the image not to display it. for that, use the other <b>loadFromInternet(..)</b> method
//     * @param imageView
//     * @param URL
//     * @param fitExactly
//     * @param callback
//     * 
//     * @see a loadFromInternet(ImageView imageView, Object URL, int width, int height, boolean fitExactly, Callback callback)
//     */
//    
//	public void loadFromInternet(ImageView imageView, Object URL, boolean fitExactly, Callback callback){
//		
//		if (URL == null) {
//            return;
//        }	
//		
//		if(imageView == null && callback == null){
//			Log.w(TAG, "Both imageview and callback cannot be null");
//			return;
//		}
//
//        BitmapDrawable value = null;
//        value = imageCache.getBitmapFromMemCache(String.valueOf(URL));
//        
//        if(callback != null && value != null){
//        	callback.getDrawable(value, String.valueOf(URL));
//        	if(imageView == null)
//        		return;
//        }
//
//        if (value != null && imageView != null) {
//            // Bitmap found in memory cache
//            imageView.setImageDrawable(value);
//        } else if (cancelPotentialWork(URL, imageView)) {
//        	int width, height;
//        	if(imageView == null){
//        		width = -1;
//        		height = -1;
//        	}else{
//        		width = imageView.getWidth();
//        		height = imageView.getHeight();
//        	}
//            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, -1, width, height, 
//            		fitExactly, callback);
//            if(imageView!=null){
//            	final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);
//            	imageView.setImageDrawable(asyncDrawable);
//            }
//
//            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
//            // framework and slightly modified. Refer to the docs at the top of the class
//            // for more info on what was changed.
//            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, URL);
//        }
////        Log.e("asyncLoader", "load, nothing is loading");
//	}
//	
//	/**
//	 * Images will be downloaded from internet and desplayed on the ImageView.</br>
//     * If fitExactly is true, then the image Width Height will be the same as the given Width and Height. 
//     *  Otherwise the Image will be decoded with respect to the given Width and Height.</br> 
//     * Both callback and imageview cannot be  null.</br>
//	 * URL string cannot be null.</br>
//	 * If callback is provided, the image will be provided to the callback as a drawable.
//	 * @param imageView
//	 * @param URL
//	 * @param width
//	 * @param height
//	 * @param fitExactly
//	 * @param callback
//	 * @see a loadFromInternet(ImageView imageView, Object URL, boolean fitExactly, Callback callback)
//	 */
//	public void loadFromInternet(ImageView imageView, Object URL, int width, int height, boolean fitExactly,
//			Callback callback){
//		
//		if (URL == null) {
//            return;
//        }
//		
//		if(width <= 0 || height <= 0){
//			Log.w(TAG, "Width & Height must be greater than 0");
//			return;
//		}
//		
//		if(imageView == null && callback == null){
//			Log.w(TAG, "Both imageview and callback cannot be null");
//		}
//
//        BitmapDrawable value = null;
//        value = imageCache.getBitmapFromMemCache(String.valueOf(URL));
//        
//        if(callback != null && value != null){
//        	callback.getDrawable(value, String.valueOf(URL));
//        	if(imageView == null)
//        		return;
//        }
//
//        if (value != null && imageView != null) {
//            // Bitmap found in memory cache
//            imageView.setImageDrawable(value);
//        } else if (cancelPotentialWork(URL, imageView)) {
//            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, -1, width, height, fitExactly, callback);
//            if(imageView!=null){
//            	final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);
//            	imageView.setImageDrawable(asyncDrawable);
//            }
//
//            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
//            // framework and slightly modified. Refer to the docs at the top of the class
//            // for more info on what was changed.
//            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, URL);
//        }
////        Log.e("asyncLoader", "load, nothing is loading");
//	}
//	
//	/**
//	 * Images will be loaded from local resource.</br>
//	 * If fitExactly is true, then the image Width Height will be the same as the ImageView Width and Height.
//     *  Ohterwise the image will decoded with respect to the Width and Height of the given ImageView.</br> 
//	 * Both callback and imageview cannot be  null.</br>
//	 * If no imageView is provided, then the default image will be pass to the callback.</br>
//	 * URL string cannot be null.</br>
//	 * If callback is provided, the image will be provided to the callback as a drawable.</br>
//	 * </br></br> 
//	 * <b>Important:</b> If imageview is null, the image will not be optimized. which mean, you are only trying to 
//	 * get the image not to display it. for that, use the other <b>loadFromResource(..)</b> method.
//	 * @param imageView
//	 * @param resId
//	 * @param URL
//	 * @param fitExactly
//	 * @param callback
//	 * @see a loadFromResource(ImageView imageView, int resId, Object URL, int width, int height, boolean fitExactly,	Callback callback)
//	 */
//	public void loadFromResource(ImageView imageView, int resId, Object URL, boolean fitExactly,
//			Callback callback){
//		
//		if (URL == null) {
//			Log.w(TAG, "URL string cannot be  null");
//            return;
//        }
//		
//		if(callback == null && imageView == null){
//			Log.w(TAG, "Both callback and imageview cannot be  null");
//			return;
//		}
//
//        BitmapDrawable value = null;
//        value = imageCache.getBitmapFromMemCache(String.valueOf(URL));
//        
//        if(callback != null && value != null){
//        	callback.getDrawable(value, String.valueOf(URL));
//        	if(imageView == null)
//        		return;
//        }
//
//        if (value != null && imageView != null) {
//            // Bitmap found in memory cache
//            imageView.setImageDrawable(value);
//        } else if (cancelPotentialWork(URL, imageView)) {
//        	int width, height;
//        	if(imageView == null){
//        		width = -1;
//        		height = -1;
//        	}else{
//        		width = imageView.getWidth();
//        		height = imageView.getHeight();
//        	}
//            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, resId, width, height, fitExactly, callback);
//            if(imageView!=null){
//            	final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);            
//            	imageView.setImageDrawable(asyncDrawable);
//            }
//
//            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
//            // framework and slightly modified. Refer to the docs at the top of the class
//            // for more info on what was changed.
//            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, URL);
//        }
////        Log.e("asyncLoader", "load, nothing is loading");
//	}
//	
//	/**
//	 * Images will be loaded from local resource.</br>
//	 * If fitExactly is true, then the image Width Height will be the same as the given Width and Height. 
//     *  Otherwise the Image will be decoded with respect to the given Width and Height.</br> 
//	 * Both callback and imageview cannot be  null.</br>
//	 * URL string cannot be null.</br>
//	 * If callback is provided, the image will be provided to the callback as a drawable.</br>	
//	 * @param imageView
//	 * @param resId
//	 * @param URL
//	 * @param fitExactly
//	 * @param callback
//	 * @see a loadFromResource(ImageView imageView, int resId, Object URL, boolean fitExactly, Callback callback)
//	 */
//	public void loadFromResource(ImageView imageView, int resId, Object URL, int width, int height, boolean fitExactly,
//			Callback callback){
//		
//		if (URL == null) {
//            return;
//        }
//
//		if(width <= 0 || height <= 0){
//			Log.w(TAG, "Width & Height must be greater than 0");
//			return;
//		}
//		
//		if(callback == null && imageView == null){
//			Log.w(TAG, "Both callback and imageview cannot be  null");
//			return;
//		}
//		
//        BitmapDrawable value = null;
//        value = imageCache.getBitmapFromMemCache(String.valueOf(URL));
//        
//        if(callback != null && value != null){
//        	callback.getDrawable(value, String.valueOf(URL));
//        	if(imageView == null)
//        		return;
//        }
//
//        if (value != null && imageView != null) { 
//            // Bitmap found in memory cache
//            imageView.setImageDrawable(value);
//        } else if (cancelPotentialWork(URL, imageView)) {
//            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, resId, width, height, fitExactly, callback);
//            if(imageView!=null){
//            	final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, mLoadingBitmap, task);
//            	imageView.setImageDrawable(asyncDrawable);
//            }
//
//            // NOTE: This uses a custom version of AsyncTask that has been pulled from the
//            // framework and slightly modified. Refer to the docs at the top of the class
//            // for more info on what was changed.
//            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, URL);
//        }
////        Log.e("asyncLoader", "load, nothing is loading");
//	}
//	
//	
//	
//	public static void cancelWork(ImageView imageView) {
//        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
//        if (bitmapWorkerTask != null) {
//            bitmapWorkerTask.cancel(true);
//        }
//    }
//	
//	public static boolean cancelPotentialWork(Object data, ImageView imageView) {
//        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
//
//        if (bitmapWorkerTask != null) {
//            final Object bitmapData = bitmapWorkerTask.data;
//            if (bitmapData == null || !bitmapData.equals(data)) {
//                bitmapWorkerTask.cancel(true);
//            } else {
//                // The same work is already in progress.
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private class BitmapWorkerTask extends AsyncTask<Object, Void, BitmapDrawable> {
//    	private Object data = null;
//    	private WeakReference<ImageView> imageViewReference = null;
//    	private int width, height, resId;
//    	private boolean fitExactly;
//    	private Callback callback;
//
//        public BitmapWorkerTask(ImageView imageView, int resId, int width, int height, boolean fitExactly, Callback callback) {
//        	imageViewReference = new WeakReference<ImageView>(imageView);
//        	this.width = width;
//        	this.height = height;
//        	this.fitExactly = fitExactly;
//        	this.callback = callback;
//        	this.resId = resId;
//        }
//
//        /**
//         * Background processing.
//         */
//        @Override
//        protected BitmapDrawable doInBackground(Object... params) {
//            Bitmap bitmap = null;
//            BitmapDrawable drawable = null;
//            
//            data = params[0];
//            String URL = String.valueOf(data); 
//            //URL = "http://a1.mzstatic.com/us/r30/Music/v4/22/23/cc/2223cc64-576f-739d-4912-54d32b7a8f49/634904059361.600x600-75.jpg";
//            
//            // Wait here if work is paused and the task is not cancelled
//            synchronized (mPauseWorkLock) {
//                while (mPauseWork && !isCancelled()) {
//                    try {
//                        mPauseWorkLock.wait();
//                    } catch (InterruptedException e) {}
//                }
//            }
//
//            // If the bitmap was not found in the cache and this task has not been cancelled by
//            // another thread and the ImageView that was originally bound to this task is still
//            // bound back to this task and our "exit early" flag is not set, then call the main
//            // process method (as implemented by a subclass)
//            if (!isCancelled() &&  !mExitTasksEarly) {
////            	Log.e("getBitmapFromDiskCache", "getBitmapFromDiskCache");
//            	bitmap = imageCache.getBitmapFromDiskCache(URL, width, height, fitExactly, resId);                
//            }
//            
//            if (bitmap == null && !isCancelled() && !mExitTasksEarly) {
//                bitmap = imageCache.processBitmap(URL, width, height, fitExactly, resId);
//            }
//            
//            if(bitmap != null){
//	            if(Utils.hasHoneycomb()){
//	            	drawable = new BitmapDrawable(mResources, bitmap);
//	            }else{
//	            	drawable = new RecyclingBitmapDrawable(mResources, bitmap);
//	            }
//	            imageCache.addBitmapToMemoryCache(URL, drawable, resId);
//            }            
//            return drawable;
//        }        
//
//		/**
//         * Once the image is processed, associates it to the imageView
//         */
//        @Override
//        protected void onPostExecute(BitmapDrawable value) {
//        	
//        	if (isCancelled() || mExitTasksEarly) {
//                value = null;
//            }
//        	
//            if(callback != null){
//            	callback.getDrawable(value, String.valueOf(data));
//            }
//
//            final ImageView imageView = getAttachedImageView();
//            if (value != null && imageView != null) {
//                setImageDrawable(imageView, value);
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            synchronized (mPauseWorkLock) {
//                mPauseWorkLock.notifyAll();
//            }
//        }
//        
//        private ImageView getAttachedImageView() {        	
//            ImageView imageView = null;;
//			try {
//				imageView = imageViewReference.get();
//			} catch (Exception e) {
////				Log.e("error imageViewReference", e.toString());
//				return null;
//			}
//            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
//
//            if (this == bitmapWorkerTask) {
//                return imageView;
//            }
//
//            return null;
//        }
//    }
//    
//    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
//        if (imageView != null) {
//            final Drawable drawable = imageView.getDrawable();
//            if (drawable instanceof AsyncDrawable) {
//                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
//                return asyncDrawable.getBitmapWorkerTask();
//            }
//        }
//        return null;
//    }
//	
//	public void setExitTasksEarly(boolean exitTasksEarly) {
//        mExitTasksEarly = exitTasksEarly;        
//        setPauseWork(false);
//    }
//	
//	public void setPauseWork(boolean pauseWork) {
//        synchronized (mPauseWorkLock) {
//            mPauseWork = pauseWork;
//            if (!mPauseWork) {
//                mPauseWorkLock.notifyAll();
//            }
//        }
//    }
//	
//    private static class AsyncDrawable extends BitmapDrawable {
//        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
//
//        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
//            super(res, bitmap);
//            bitmapWorkerTaskReference =
//                new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
//        }
//
//        public BitmapWorkerTask getBitmapWorkerTask() {
//            return bitmapWorkerTaskReference.get();
//        }
//    }
//
//    /**
//     * Called when the processing is complete and the final drawable should be 
//     * set on the ImageView.
//     *
//     * @param imageView
//     * @param drawable
//     */
//    @SuppressLint("NewApi")
//	@SuppressWarnings("deprecation")
//	private void setImageDrawable(ImageView imageView, Drawable drawable) {
//        if (mFadeInBitmap) {
//            // Transition drawable with a transparent drawable and the final drawable
//            final TransitionDrawable td =
//                    new TransitionDrawable(new Drawable[] {
//                            new ColorDrawable(android.R.color.transparent),
//                            drawable
//                    });
//            
//            if(useLoadingImage && mLoadingBitmap != null){
//	            // Set background to loading bitmap
//	            if(imageView instanceof CircleImageView){
//	            	if(!Utils.hasJellyBean()){
//		            	imageView.setBackgroundDrawable(
//		                        new BitmapDrawable(mResources, CircleImageView.getCroppedBitmap(mLoadingBitmap, mLoadingBitmap.getWidth())));
//					}else{
//						imageView.setBackground(
//			                    new BitmapDrawable(mResources, CircleImageView.getCroppedBitmap(mLoadingBitmap, mLoadingBitmap.getWidth())));
//					} 
//	            }else if(imageView instanceof RoundedBitmapDisplayer){
//	            	if(!Utils.hasJellyBean()){
//		            	imageView.setBackgroundDrawable(
//		                        new BitmapDrawable(mResources, RoundedBitmapDisplayer.roundCorners(mLoadingBitmap, imageView, ((RoundedBitmapDisplayer) imageView).getRoundPixle())));
//					}else{
//						imageView.setBackground(
//			                    new BitmapDrawable(mResources, RoundedBitmapDisplayer.roundCorners(mLoadingBitmap, imageView, ((RoundedBitmapDisplayer) imageView).getRoundPixle())));
//					} 
//	            }else {
//		            if(!Utils.hasJellyBean()){
//		            	imageView.setBackgroundDrawable(
//		                        new BitmapDrawable(mResources, mLoadingBitmap));
//					}else{
//						imageView.setBackground(
//			                    new BitmapDrawable(mResources, mLoadingBitmap));
//					}  
//	            }
//            }
//
//            imageView.setImageDrawable(td);
//            td.startTransition(FADE_IN_TIME);
//        } else {
//            imageView.setImageDrawable(drawable);
//        }
//    }
//    
//    protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Object... params) {
//            switch ((Integer)params[0]) {
//                case MESSAGE_CLEAR:
//                    clearCacheInternal();
//                    break;
//                case MESSAGE_INIT_DISK_CACHE:
//                    initDiskCacheInternal();
//                    break;
//                case MESSAGE_FLUSH:
//                    flushCacheInternal();
//                    break;
//                case MESSAGE_CLOSE:
//                    closeCacheInternal();
//                    break;
//            }
//            return null;
//        }
//    }
//
//    protected void initDiskCacheInternal() {
//        if (imageCache != null) {
//        	imageCache.initDiskCache();
//        }
//    }
//
//    protected void clearCacheInternal() {
//        if (imageCache != null) {
//        	imageCache.clearCache();
//        }
//    }
//
//    protected void flushCacheInternal() {
//        if (imageCache != null) {
//            imageCache.flush();
//        }
//    }
//
//    protected void closeCacheInternal() {
//        if (imageCache != null) {
//            imageCache.close();
//            imageCache = null;
//        }
//    }
//
//    public void clearCache() {
//        new CacheAsyncTask().execute(MESSAGE_CLEAR);
//    }
//
//    public void flushCache() {
//        new CacheAsyncTask().execute(MESSAGE_FLUSH);
//    }
//
//    public void closeCache() {
//        new CacheAsyncTask().execute(MESSAGE_CLOSE);
//    }
//}
