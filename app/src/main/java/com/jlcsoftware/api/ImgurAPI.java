package com.jlcsoftware.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jlcsoftware.helpers.MyLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Jeff on 06-Mar-16.
 *
 * A quick and dirty basic REST library to talk to Imgur.
 *
 * A solution based on Volley or Apache Http Components or OkHTTP or ION would be better
 *
 * This solution here is seriously no good for very large bitmaps/images
 */
public class ImgurAPI {
    public static String CLIENT_ID = "561b6197d570e0e";
    public static String CLIENT_SECRET = "04e35f6075f3ab16f39e8673ebe1e984bc8a666c";
    private static String TAG = "ImgurAPI";

    /**
     * A https Get function
     *
     * @param urlStr
     * @return what we pulled down...
     * @throws IOException
     */
    private static String httpsGet(final String urlStr) throws IOException {
        URL url = new URL(urlStr);

        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }
        String headerField = conn.getHeaderField("Content-Type");
        if (!headerField.toLowerCase().equals("application/json")) {
            throw new IOException(headerField);
        }
        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();

        conn.disconnect();
        return sb.toString();
    }

    /**
     * Get a raw bitmap
     * @param urlStr
     * @return the bytes in the bitmap
     * @throws IOException
     */

    public static byte[] httpGetBitmapData(final String urlStr) throws IOException {
        URL url =  new URL(urlStr);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(true); // Use the System Cache to cache/save the images
 //       conn.setRequestProperty("Authorization", "Client-ID " + CLIENT_ID);

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        InputStream input = conn.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int read = 0;
        while (-1 != (read = input.read(buffer))) {
            out.write(buffer, 0, read);
        }
        input.close();

        conn.disconnect();
        return out.toByteArray();

    }

    /**
     * Check to see if we have network connectivity
     * @param context Context
     * @return true/false
     */
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * Check if we can reach Imgur
     * @param context Context
     * @return true/false
     */
    public static boolean hasInternetAccess(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection conn = (HttpURLConnection)
                        (new URL("http://imgur.com")
                                .openConnection());
                conn.setRequestProperty("User-Agent", "ImgurApp");
                conn.setRequestProperty("Connection", "close");
                conn.setConnectTimeout(1500);
                conn.connect();
                final int responseCode = conn.getResponseCode();
                return (responseCode == 200);
            } catch (IOException e) {
                MyLogger.e(TAG, "Error checking internet connection", e);
            }
        } else {
            MyLogger.d(TAG, "No network available!");
        }
        return false;
    }


    /**
     * Get a Imgur Album
     * @param albumId
     * @return Album JSON object
     * @throws IOException
     * @throws JSONException
     */

    public static JSONObject getAlbumImages(String albumId) throws IOException, JSONException {
        String get = "https://api.imgur.com/3/album/" + albumId + "/images";
        get = httpsGet(get);
        return new JSONObject(get);
    }

    /**
     * Get a Imgur Gallery
     * @param gallery
     * @return Gallery JSON object
     * @throws IOException
     * @throws JSONException
     */

    public static JSONObject getGalleryImages(String gallery) throws IOException, JSONException {
        String get = getGalleryImagesResponse(gallery);
        return new JSONObject(get);
    }

    /**
     * Gets the Imgur Gallery Response as a String
     * @param gallery
     * @return
     * @throws IOException
     * @throws JSONException
     */

    public static String getGalleryImagesResponse(String gallery) throws IOException, JSONException {
        String get = "https://api.imgur.com/3/gallery/" + gallery;  // Uses the defaults
        get = httpsGet(get);
        return get;
    }

    /**
     * Get a Imgur Image Response
     * @param id mgur Image Id
     * @return Image JSON Object
     * @throws IOException
     * @throws JSONException
     */

    public static JSONObject getImage(String id) throws IOException, JSONException{
        String get = getImageResponse(id);
        return new JSONObject(get);
    }

    /**
     * Get a Imgur Image Response as a String
     * @param id Imgur Image Id
     * @return Imgur Image JSON string Response
     * @throws IOException
     * @throws JSONException
     */

    public static String getImageResponse(String id) throws IOException, JSONException{
        String get = "https://api.imgur.com/3/image/" + id;
        get = httpsGet(get);
        return get;
    }
}
