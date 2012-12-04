package se.kth.ssvl.tslab.wsn.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.kth.ssvl.tslab.wsn.app.config.ConfigManager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtil {

	private static final String TAG = "FileUtil";

	public static File phoneStoragePath(Context context) {
		return new File(context.getFilesDir().getAbsolutePath()
				+ ConfigManager.PATH).getParentFile();
	}

	public static File sdCardStoragePath(Context context) {
		return new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + ConfigManager.PATH).getParentFile();
	}
	
	/**
	 * Moves folder "src" to folder "dst". This will be done with copy and delete operations.
	 * @param src What folder to move
	 * @param dst The folder to move it to
	 * @return True if it was successful, otherwise false
	 * @throws IOException 
	 */
	public static boolean moveFolder(File src, File dst) throws IOException {
		Log.d(TAG, "Moving folder " + src.getAbsolutePath() + " to " + dst.getAbsolutePath());
		
		// First copy src
		if (!copyFolder(src, dst)) {
			return false;
		}
		
		// Then delete the src
		deleteFolder(src);
		
		return true;
	}
	
	
	/**
	 * Copies a folder with all files from src to dst. Note: dst is created if it doesn't exist
	 * @param src The source folder
	 * @param dst The destionation folder
	 * @return True if the copy was successful
	 * @throws IOException 
	 */
	public static boolean copyFolder(File src, File dst) throws IOException {
		// Create the folder if it doesn't exist
		if (!dst.exists()) {
			if (!dst.mkdir()) {
				return false;
			}
		}
		
		File[] files = src.listFiles();
		for (File f : files) {
			
			File newDst = new File(dst.getAbsoluteFile() + "/" + f.getName());
			
			// If it is a directory dig deeper
			if (f.isDirectory()) {
				if (!copyFolder(f, newDst)) {
					return false;
				}
			} else {
				// Otherwise copy the file to its location
				InputStream in = new FileInputStream(f);
			    OutputStream out = new FileOutputStream(newDst);
			    proxyStream(in, out);
			    in.close();
			    out.close();
			}
		}
		
		return true;
	}
	
	/**
	 * Deletes all files within a folder and the folder itself
	 * @param folder The folder to delete
	 */
	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	/**
	 * Proxies an inputstream to an outputstream.
	 * @param in The inputstream
	 * @param out The outputstream
	 * @throws IOException
	 */
	public static void proxyStream(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
}
