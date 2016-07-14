package com.jlcsoftware.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jeff on 12-Jul-16.
 *
 *  Wraps a Imgur Gallery JSONObject to make it more useful
 *
 */
public class ImgurGallery {
    JSONArray jsonArray; // who ever thought Android JSONArray should not inherit from JSONObject should get a boot to the head!

    /**
     * Constuctor
     * @param object JSONObject that contains the Imgur Gallery Response
     */
    public ImgurGallery(JSONObject object) {
        this.jsonArray = object.optJSONArray("data");
    }

    /**
     * Constructor
     * @param jsonString JSON string that is the Imgur Gallery Response
     * @throws JSONException
     */
    public ImgurGallery(String jsonString) throws JSONException {
        this.jsonArray = new JSONArray(jsonString);
    }

    /**
     * Number of Imgur Images
     * @return Number of Imgur Images
     */
    public int length(){
        return jsonArray.length();
    }

    /**
     * Get a Imgur Image
     * @param ndx which?
     * @return
     */
    public ImgurImage get(int ndx){
        ImgurImage imgurImage=null;
        try {
            final JSONObject object = jsonArray.getJSONObject(ndx);
            imgurImage = new ImgurImage(object);
        } catch (JSONException e) {
        }
        return imgurImage;
    }

    /**
     * Get ALL Imgur Images
     * @return Array of Imgur Images
     */

   public ArrayList<ImgurImage> getImages() {
        ArrayList<ImgurImage> listOfImages = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject image = null;
            try {
                image = jsonArray.getJSONObject(i);
                final ImgurImage imgurImage = new ImgurImage(image);
                if (!imgurImage.isAlbum()) {
                    // can't draw some images
                    if (image.get("type").equals("image/jpeg") || image.get("type").equals("image/png") || image.get("type").equals("image/gif")) {
                        listOfImages.add(imgurImage);
                    }
                } else {
                    listOfImages.add(imgurImage);
                }
            } catch (JSONException e) {
            }
        }
        return listOfImages;

    }

    /**
     * Get the JSON string that represents the Imgur Image Gallery
     * @return JSON string that represents the Imgur Image Gallery
     */

    @Override
    public String toString() {
        // Return the JSON string
        return jsonArray.toString();
    }

}
