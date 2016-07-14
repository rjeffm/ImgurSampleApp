package com.jlcsoftware.sampleapp;


import com.jlcsoftware.api.ImgurAPI;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 *
 * Not much to test in this App in plain non-android dependent Java
 */
public class ExampleUnitTest {
    @Test
    public void testImgurAPI() throws Exception {

        String jsonString = ImgurAPI.getGalleryImagesResponse("top");
        assertFalse("Response empty",jsonString.isEmpty()); // not empty
        assertTrue("Bad Json String", jsonString.contains("data")); // has the JSON data string


    }
}