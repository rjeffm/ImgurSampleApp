package com.jlcsoftware.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jeff on 06-Mar-16.
 * <p>
 * Wraps a Imgur Image JSONObject to make it more useful
 */
public class ImgurImage {

    private JSONObject object; // The JSON object that contains the Imgur Image response

    /**
     * Constructor
     * @param object JSON object that contains the Imgur Image response
     */
    public ImgurImage(JSONObject object) {
        this.object = object;
        try {
            // remove data: level if it exists...
            this.object = object.getJSONObject("data");
        } catch (JSONException e) {
            // is ok!
        }
    }

    /**
     * Constructor
     * @param jsonString JSON string that is the Imgur Gallery Response
     * @throws JSONException
     */
    public ImgurImage(String jsonString) throws JSONException {
        this.object = new JSONObject(jsonString);
    }

    /**
     * Get the JSON object that contains the Imgur Image response
     * @return JSON object that contains the Imgur Image response
     */

    public JSONObject getJSONObject() {
        return object;
    }

    /**
     * Get the Image url
     * @return Image url
     */
    public String getLink() {
        try {
            return object.getString("link");
        } catch (JSONException e) {
        }
        return "";
    }

    /**
     * Get the Image Title
     * @return Image Title
     */
    public String getTitle() {
        try {
            return object.getString("title");
        } catch (JSONException e) {
        }
        return "";
    }

    /**
     * Get the Image Description
     * @return Image description (Can be "null" )
     */
    public String getDescription() {
        try {
            return object.getString("description");
        } catch (JSONException e) {
        }
        return "";
    }

    /**
     * Get the Image Mime Type
     * @return Mime type (ie: image/jpg )
     */

    public String getMimeType() {
        try {
            return object.getString("type");
        } catch (JSONException e) {
        }
        return "";
    }

    /**
     * Is the Image Animated?
     * @return true/false
     */
    public boolean isAnimated() {
        try {
            return object.getBoolean("animated");
        } catch (JSONException e) {
        }
        return false;
    }

    /**
     * Does this "Imgur Image" represent a Album?
     * @return true/false
     */
    public boolean isAlbum() {
        try {
            return object.getBoolean("is_album");
        } catch (JSONException e) {
        }
        return false;
    }

    /**
     * Cover image if its an Album
     * @return Imgur Image Id
     */

    public String getCover() {
        try {
            return object.getString("cover");
        } catch (JSONException e) {
        }
        return "";
    }

    /**
     * Generic get from the JSON object
     * @param method method/key
     * @return something
     */

    public Object get(String method) {
        try {
            return object.get(method);
        } catch (JSONException e) {
        }
        return null;
    }

    /**
     * Generic put to the JSON Object
     * @param method method/key
     * @param obj something
     */

    public void put(String method, Object obj) {
        try {
            object.put(method, obj);
        } catch (JSONException e) {
        }
    }

    /**
     * Return the JSON encoded String representing the Imgur Image response
     * @return JSON encoded String representing the Imgur Image response
     */

    @Override
    public String toString() {
        // Return the JSON string
        return object.toString();
    }
}