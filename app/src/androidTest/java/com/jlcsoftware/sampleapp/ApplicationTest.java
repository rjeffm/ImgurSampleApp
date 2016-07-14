package com.jlcsoftware.sampleapp;

import android.app.Application;
import android.os.StrictMode;
import android.test.ApplicationTestCase;

import com.jlcsoftware.api.ImgurAPI;
import com.jlcsoftware.api.ImgurGallery;
import com.jlcsoftware.api.ImgurImage;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

    }


    public void testImgurAPI() throws Exception {
        // allow Network connections on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            String jsonString = ImgurAPI.getGalleryImagesResponse("top");
            assertFalse("Response empty",jsonString.isEmpty()); // not empty
            assertTrue("Bad Json String", jsonString.contains("data")); // has the JSON data string

            ImgurGallery imgurGallery = new ImgurGallery(jsonString); // will kill the test if not a proper JSON string
            assertTrue("Gallery has no images", imgurGallery.length() > 0); // got'ta assume that there are "some" images....

            ImgurImage imgurImage = imgurGallery.get(0); // get the first one...
            assertNotNull("Failed to get image 0", imgurImage);

            String title = imgurImage.getTitle();
            assertFalse("Title empty", title.isEmpty()); // not empty

            String imageLink = imgurImage.getLink();
            assertFalse("Link empty", imageLink.isEmpty()); // not empty

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    }