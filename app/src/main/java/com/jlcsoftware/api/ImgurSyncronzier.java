package com.jlcsoftware.api;

import android.content.Context;
import android.os.Looper;

import com.jlcsoftware.helpers.MyLogger;
import com.jlcsoftware.sampleapp.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by Jeff on 12-Jul-16.
 *
 * Handles connecting to Imgur, downloading JSON responses and saving them into the cache
 *
 */
public class ImgurSyncronzier {
    static String TAG = "ImgurSyncronzier";

    /**
     * Get our cache directory
     * @param context Context
     * @return directory File
     */
    private static File getPreferredCache(Context context) {
        File file = new File(context.getCacheDir(), "imgur");
        file.mkdirs();
        return file;
    }

    /**
     * Make sure the caller is NOT on the main thread
     */

    private static void checkIfMain() {
        if (Looper.myLooper() == Looper.getMainLooper())
            throw new IllegalStateException("Must be called on background thread");
    }

    /**
     * Refresh the cache of Imgur JSON Gallery responses
     * @param context Context
     */

    public static void refresh(Context context) {
        checkIfMain();
        AppPreferences preferences = AppPreferences.getInstance(context);
        Set<String> galleries = preferences.getGalleries();
        for (String gallery_id : galleries) {
            try {
                getAndCacheGallery(context, gallery_id);

            } catch (IOException e) {
               MyLogger.e(TAG,"void refresh(Context context)", e);
            } catch (JSONException e) {
                MyLogger.e(TAG,"void refresh(Context context)", e);
            }
        }

    }

    /**
     * Refresh the cache for a specific Gallery
     * @param context Context
     * @param gallery_id Imgur Gallery Id
     * @return JSON response String
     * @throws IOException
     * @throws JSONException
     */

    public static String refresh(Context context, String gallery_id) throws IOException, JSONException {
        checkIfMain();
        return getAndCacheGallery(context, gallery_id);
    }

    /**
     * Get a Imgur Gallery response and cache it
     * @param context Context
     * @param gallery_id Imgur Gallery Id
     * @return JSON response string
     * @throws IOException
     * @throws JSONException
     */
    private static String getAndCacheGallery(Context context, String gallery_id) throws IOException, JSONException {
        String response = ImgurAPI.getGalleryImagesResponse(gallery_id);
        writeToCache(context, gallery_id, response);
        return response;
    }

    /**
     * Get a Imgur Image response and cache it
     * @param context Context
     * @param image_id Imgur Image Id
     * @return JSON response string
     * @throws IOException
     * @throws JSONException
     */
    private static String getAndCacheImage(Context context, String image_id) throws IOException, JSONException {
        String response = ImgurAPI.getImageResponse(image_id);
        writeToCache(context, image_id, response);
        return response;
    }

    /**
     * Write a Gallery response string to the cache
     * @param context Context
     * @param gallery_id Gallery Id
     * @param response JSON response string
     */

    private static void writeToCache(Context context, String gallery_id, String response) {
        if (response.isEmpty()) return;
        File image = new File(getPreferredCache(context), gallery_id + ".cache");
        FileOutputStream outputStream = null;
        java.nio.channels.FileLock lock = null;
        try {
            outputStream = new FileOutputStream(image);
            lock = outputStream.getChannel().lock();
            outputStream = new FileOutputStream(image);
            outputStream.write(response.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            MyLogger.e(TAG, "writeToCache", e);
        } catch (IOException e) {
            MyLogger.e(TAG, "writeToCache", e);
        } finally {
            try {
                if (null != lock)
                    lock.release();
                if (null != outputStream)
                    outputStream.close();
            } catch (IOException e) {
                MyLogger.e(TAG, "writeToCache", e);
            }
        }
    }

    /**
     * Build the Cache file for a Imgur Id
     *
     * @param context  Context
     * @param imgur_id Imgur Id - Assumed to be unique
     * @return Cache File
     */
    private static File getCacheFile(Context context, String imgur_id) {
        return new File(getPreferredCache(context), imgur_id + ".cache");
    }

    /**
     * Lock the file and read it from the cache
     * @param context Context
     * @param gallery_id Imgur Gallery Id
     * @return Imgur Gallery JSON string
     */
    private static String readFromCache(Context context, String gallery_id) {
        File file = getCacheFile(context, gallery_id);
        StringBuffer stringBuffer = new StringBuffer("");

        FileLock lock = null;
        FileInputStream inputStream = null;
        InputStreamReader reader = null;
        try {
            inputStream = new FileInputStream(file);
            lock = inputStream.getChannel().lock(0L, Long.MAX_VALUE, true);
            reader = new InputStreamReader(inputStream);

            char[] buffer = new char[8 * 1024]; // 8K at a time
            StringBuffer buf = new StringBuffer();
            int numChars;

            while ((numChars = reader.read(buffer, 0, buffer.length)) > 0) {
                buf.append(buffer, 0, numChars);
            }

        } catch (FileNotFoundException e) {
            // this is ok!
        } catch (Exception e) {
            MyLogger.e(TAG, "readFromCache", e);
        } finally {
            try {
                if (null != lock) {
                    lock.release();
                }
                if (null != reader) {
                    reader.close();
                } else {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                }
            }catch (Exception e){
            }
        }
        return stringBuffer.toString();
    }

    /**
     * Clean up (delete) old response files
     * @param context Context
     */

    public static void cleanup(Context context) {
        checkIfMain();
        AppPreferences preferences = AppPreferences.getInstance(context);
        long ageInMills = preferences.getExpiry();
        long currentInMills = Calendar.getInstance().getTimeInMillis();
        final File cache = getPreferredCache(context);
        // we assume all files in this cache directory are ours
        File files[] = cache.listFiles();
        for (File file : files) {
            final long lastModified = file.lastModified();
            if (currentInMills - lastModified > ageInMills) {
                file.delete(); // might fail if the file is locked... but why is it locked if its old? Besides, we'll get it next time
            }
        }
    }

    /**
     * Get a Gallery Image from the Cache or Imgur (if the cache file does not exist)
     * @param context Context
     * @param gallery_id Imgur Gallery id
     * @return Gallery Image
     */

    public static ImgurGallery getGalleryImages(Context context, String gallery_id) {
        checkIfMain();
        String response = readFromCache(context, gallery_id);
        if (response.isEmpty()) {
            // pull from the wire...
            try {
                response = getAndCacheGallery(context, gallery_id);
            } catch (IOException e) {
                MyLogger.e(TAG, "getGalleryImages", e);
            } catch (JSONException e) {
                MyLogger.e(TAG, "getGalleryImages", e);
            }
        }
        try {
            return new ImgurGallery(new JSONObject(response));
        } catch (JSONException e) {
            MyLogger.e(TAG, "getGalleryImages", e);
            // delete the cache file, if it exists... maybe it is corrupted.
            File file = getCacheFile(context, gallery_id);
            if (file.exists())
                file.delete();
        }
        return null;
    }

    /**
     * Get a Imgur Image from the Cache or Imgur (if the cache file does not exist)
     * @param context Context
     * @param image_id Imgur Image id
     * @return Imgur Image
     */

    public static ImgurImage getImage(Context context, String image_id) {
        checkIfMain();
        String response = readFromCache(context, image_id);
        if (response.isEmpty()) {
            // pull from the wire...
            try {
                response = getAndCacheImage(context, image_id);
            } catch (IOException e) {
                MyLogger.e(TAG, "getImage", e);
            } catch (JSONException e) {
                MyLogger.e(TAG, "getImage", e);
            }
        }
        try {
            return new ImgurImage(new JSONObject(response));
        } catch (JSONException e) {
            MyLogger.e(TAG, "getImage", e);
        }
        return null;
    }

}
