package com.safield.SafireRingtoneMaker;

import android.os.Environment;

public class sfTools {

	//check if device storage is writable
	public static boolean isExternalStorageWritable() {

	    String state = Environment.getExternalStorageState();

	    if (Environment.MEDIA_MOUNTED.equals(state))
	        return true;

	    return false;
	}

	
	//check if device storage is writable / readable
	public static boolean[] checkExternalMedia(){
	      boolean [] results=new boolean[2];
	    
	    String state = Environment.getExternalStorageState();

	    if (Environment.MEDIA_MOUNTED.equals(state)) {

	        // Can read and write the media
	        results[0] = results[1] = true;

	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

	        // Can only read the media
	    	results[0] = true;
	    	results[1] = false;

	    } else {

	        // Can't read or write
	    	results[0] = results[1] = false;
	    }

	    return results;
	}
	
}
