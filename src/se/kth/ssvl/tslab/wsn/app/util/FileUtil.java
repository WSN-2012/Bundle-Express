/*
 * Copyright 2012 KTH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

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
	 * Moves folder "src" to folder "dst". This will be done with copy and
	 * delete operations.
	 * 
	 * @param src
	 *            What folder to move
	 * @param dst
	 *            The folder to move it to
	 * @return True if it was successful, otherwise false
	 * @throws IOException
	 */
	public static boolean moveFolder(File src, File dst) {
		Log.d(TAG,
				"Moving folder " + src.getAbsolutePath() + " to "
						+ dst.getAbsolutePath());

		// First copy src
		if (!copyFolder(src, dst)) {
			return false;
		}

		// Then delete the src
		deleteFolder(src);

		return true;
	}

	/**
	 * Move a file from one point to another. This will use a copying operation,
	 * which means it is usable between different mountpoints.Note: The
	 * destination file will be created and also the parent directories if
	 * needed
	 * 
	 * @param src
	 *            The source file to move
	 * @param dst
	 *            The destination file to save it to.
	 * @return True if it was successful otherwise false.
	 */
	public static boolean moveFile(File src, File dst) {
		Log.d(TAG,
				"Moving file " + src.getAbsolutePath() + " to "
						+ dst.getAbsolutePath());

		// First copy the file
		if (!copyFile(src, dst)) {
			return false;
		}

		// Then delete the source file
		if (!src.delete()) {
			return false;
		}

		return true;
	}

	/**
	 * Copy a file from src to dst. Note: dst is created if it doesn't exist and
	 * also all parent folders
	 * 
	 * @param src
	 *            The source file
	 * @param dst
	 *            The destianation file
	 * @return True if the copy was successful
	 */
	public static boolean copyFile(File src, File dst) {
		try {
			// Create the parent directories if they don't exist
			if (!dst.getParentFile().exists() && !dst.getParentFile().mkdirs()) {
				return false;
			}
			// Start the copying
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			proxyStream(in, out);
			in.close();
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Copies a folder with all files from src to dst. Note: dst is created if
	 * it doesn't exist
	 * 
	 * @param src
	 *            The source folder
	 * @param dst
	 *            The destionation folder
	 * @return True if the copy was successful
	 * @throws IOException
	 */
	public static boolean copyFolder(File src, File dst) {
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
				copyFile(f, newDst);
			}
		}

		return true;
	}

	/**
	 * Deletes all files within a folder and the folder itself
	 * 
	 * @param folder
	 *            The folder to delete
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
	 * 
	 * @param in
	 *            The inputstream
	 * @param out
	 *            The outputstream
	 * @throws IOException
	 */
	public static void proxyStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}
