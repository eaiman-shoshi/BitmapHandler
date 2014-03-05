package com.utils;

import java.io.File;
import java.util.ArrayList;
 
public class FileCache {
 
    private File cacheDir;
//    private File latest_photos;
//    private File graphicsDir;
//    private File root;
 
    public FileCache(File file){
        //Find the dir to save cached images
        cacheDir=file;
    }
 
    public File getFile(String url){
    	String filename = "";
    	if(!isHashCode(url))
    		filename = String.valueOf(url.hashCode());
    	else
    		filename = url;
    	//Log.e("file name for downloaded images", filename);
        File f = new File(cacheDir, filename);
        return f;
 
    }
    
    private boolean isHashCode(String url) {
		// TODO Auto-generated method stub
    	try {
			Integer.parseInt(url);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			//Log.e("NumberFormatException", e.toString());
			return false;
		}
		return true;
	}

	public ArrayList<String> getFiles(){
    	 File[] files=cacheDir.listFiles();
    	 ArrayList<String> arrayFiles = new ArrayList<String>();
         if(files==null)
             return null;
         for(File file : files){
        	 //Log.e("file name", file.getName());
             arrayFiles.add(file.getName());
         }
         return arrayFiles;
    }
 
    public boolean deleteFile(String filename){
        File f = new File(cacheDir, filename);
        try {
			if(f.exists()){
				f.delete();
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return false;
		}
    }
}