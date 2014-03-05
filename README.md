BitmapHandler
=============

<h3>Library project for loading Images on ImageView from resources or from internet for Android. OOM error is handled in
this project</h3>

<h3>Feature:</h3>
     Included Circuler ImageView with border or no border, as you want
     Included Rounded Corner ImageView
     This library project will handle the Memory Management for loading images
     Callback method integrated

<h3>Code sample:</h3>
<h4>To load images from internet, use 'ImageFetcher' and from resource, use 'ImageResizer'</h4>

<h5>Variables:</h5>
     private static final String IMAGE_CACHE_DIR = "USE_AS_YOU_WISH";//the images will be cashed in this dir
     private ImageFetcher imageLoader; //use to load image from internet
     private ImageResizer imageLoader; //use to load image from resource

<h5>In onCreate():</h5>
     int longest = (ScreenHeight > ScreenWidth ? ScreenHeight : ScreenWidth) / 2;
     ImageCacheParams cacheParams = new ImageCacheParams(getActivity() or this, IMAGE_CACHE_DIR);
     cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
        
     imageLoader = new ImageFetcher(getActivity() or this, longest);
     imageLoader.setLoadingImage(R.drawable.empty_photo);
     imageLoader.useLoadingImageForFadein(true);
     imageLoader.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
     
<h5>To load image on ImageView:</h5>
     // use the third parameter of 'loadImage(param1, param2, param3)' always 'null'. this part is for under development.
     imageLoader.loadImage(URL, imageView, null); // to load image from internet
     imageLoader.loadImage(R.drawable.image_id, imageView, null); // to load image from internet
     
<h5>In onPause():</h5>     
     @Override
     public void onPause() {
         super.onPause();
         imageLoader.setPauseWork(false);
         imageLoader.setExitTasksEarly(true);
	     imageLoader.flushCache();
     }

<h5>In onResume():</h5>
     @Override
     public void onResume() {
         super.onResume();
	     imageLoader.setExitTasksEarly(false);
     }
	   
<h5>In onDestroy():</h5>	   
     @Override
     public void onDestroy() {
         super.onDestroy();
         imageLoader.closeCache();
     }

<h5>Callback:</h5>	   
     public class YOUR_CLASS extends Activity/Fragment implements Callback{
     
         @Override
	     public void getDrawable(Drawable drawable, Object name, File file) {
	         // 'drawable' is the Drawable, which is setted on the ImageView.
	         // 'name' is the URL or the resource id.
	         // 'file' is the File, where the setted image is cached.
	     }
     }

<h5>Use of CircleImageView in xml:</h5>
     // it is recomanded to use 'imageLoader.useLoadingImageForFadein(false);'. note that the parameter is 'false'
     
     <LinearLayout 
         xmlns:android="http://schemas.android.com/apk/res/android"
         xmlns:app="http://schemas.android.com/apk/res/YOUR_PROJECT'S_PACKAGE_NAME"
         android:layout_width="match_parent"
         android:layout_height="match_parent">
         
         <com.displayer.CircleImageView
              android:id="@+id/circleImageView"
              android:layout_width="wrap_content"
              android:layout_height="match_parent"
              android:scaleType="centerInside"
              android:layout_centerInParent="true"
              app:stroke_width="5" // use '0' for no borded
              app:stroke_color="@android:color/transparent"/> // this line means, the border is transparent. 
                                                              // use your desire border color.
     </LinearLayout>
      
     ** you can also create this ImageView from Java code also.
     CircleImageView circleImageView = new CircleImageView(getActivity() or this);
     circleImageView.setStroke(strokeWidth, strokeColor);

<h5>Use of RoundedBitmapDisplayer in xml:</h5>
     // it is recomanded use 'imageLoader.useLoadingImageForFadein(false);'. note that the parameter is 'false'
     
     <LinearLayout 
         xmlns:android="http://schemas.android.com/apk/res/android"
         xmlns:app="http://schemas.android.com/apk/res/YOUR_PROJECT'S_PACKAGE_NAME"
         android:layout_width="match_parent"
         android:layout_height="match_parent">
         
         <com.displayer.RoundedBitmapDisplayer
              android:id="@+id/roundedBitmapDisplayer"
              android:layout_width="wrap_content"
              android:layout_height="match_parent"
              android:scaleType="centerInside"
              android:layout_centerInParent="true"
              app:round_pixel="10" // this is radius of the rounded corner
      </LinearLayout>
      
      ** you can also create this ImageView from Java code also.
      RoundedBitmapDisplayer roundedBitmapDisplayer = new RoundedBitmapDisplayer(getActivity() or this);
      roundedBitmapDisplayer.setRoundPixle(roundPixels);
      
<h3>Reference Project:</h3>
<p>
This project is build with the help of 'BitmapFun' from Google.

Google BitmapFun project download Link:
http://developer.android.com/shareables/training/BitmapFun.zip. Google 'Displaying Bitmaps Efficiently' tutorial Link:
http://developer.android.com/training/displaying-bitmaps/index.html. I have really forget about the references about 'CircleImageView' and 'RoundedBitmapDisplayer'.But, they were definitely open source.
</p>
      
<h3>Developed by:</h3>
     Eaiman Shoshi
     coolshoshi.cse@gmail.com
     
<h3>License:</h3>
     The MIT License (MIT)

     Copyright (c) 2014 Eaiman Shoshi

     Permission is hereby granted, free of charge, to any person obtaining a copy
     of this software and associated documentation files (the "Software"), to deal
     in the Software without restriction, including without limitation the rights
     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     copies of the Software, and to permit persons to whom the Software is
     furnished to do so, subject to the following conditions:

     The above copyright notice and this permission notice shall be included in all
     copies or substantial portions of the Software.

     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     SOFTWARE.

