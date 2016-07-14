package com.jlcsoftware.sampleapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jeff on 10-Jul-16.
 * A Singleton
 * Wrapper around the default SharedPreferences so we can easily set and read our settings
 */
public class AppPreferences {

    private static AppPreferences instance = null;
    private SharedPreferences preferences; // Our shared prefs


    /**
     * private constructor
     * @param context Context
     */
    private AppPreferences(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE); // From Android sources for getDefaultSharedPreferences
    }

    /**
     * Must be called once on app startup
     *
     * @param context - application context
     * @return this
     */
    public static AppPreferences getInstance(Context context) {
        if (instance == null) {
            if (context == null) {
                throw new IllegalStateException(AppPreferences.class.getSimpleName() +
                        " is not initialized, call getInstance(Context) with a VALID Context first.");
            }
            instance = new AppPreferences(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Get the Galleries we want to look at..
     * TODO: make this configurable ( will need to update the navigation view menu dynamically in that case.)
     *
     * @return
     */

    public Set<String> getGalleries() {
        return (Set<String>) getSet("galleries", new HashSet<String>() {{
            add("top");
            add("hot");
        }});
    }

    /**
     * Set the galleries we want to look at
     *
     * @param galleries
     */

    public void setGalleries(Set<String> galleries) {
        putSet("galleries", galleries);
    }


    /**
     * Get the cache expiry term
     * @return
     */
    public long getExpiry() {
        return preferences.getLong("age_in_mills", 43200000); // default 1/2 day
    }

    /**
     * Set the cache expiry term
     * @param mills
     */
    public void setExpiry(long mills) {
        preferences.edit().putLong("age_in_mills", mills).commit();
    }

    /**
     * Get a set from the preferences
     * sets MUST be: Strings, Booleans, Integers, Longs, Doubles, null or NULL. Values may not be NaNs, infinities, or of any type not listed here.
     *
     * @param key key
     * @param defValues default values
     * @return the set (default or found)
     */

    public Set<?> getSet(String key, Set<?> defValues) {
        String serializedSet = preferences.getString(key, null);
        if (null == serializedSet || serializedSet.isEmpty()) return defValues;
        try {
            JSONArray array = new JSONArray(serializedSet);
            Set<Object> set = new HashSet<Object>();
            for (int i = 0; i < array.length(); i++) {
                set.add(array.get(i));
            }
            return set;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return defValues;
    }


    /**
     * Put a set to the preferences
     * sets MUST be: Strings, Booleans, Integers, Longs, Doubles, null or NULL. Values may not be NaNs, infinities, or of any type not listed here.
     *
     * @param key
     * @param values
     * @return
     */

    public void putSet(String key, Set<?> values) {
        JSONArray array = new JSONArray();
        for (Object obj : values) {
            array.put(obj);
        }

        String json = array.toString();
        preferences.edit().putString(key, json).commit();
    }

    /**
     * Put a string into the Preferences
     * @param key Key
     * @param value Value
     */
    public void putString(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

    /**
     * Get a string from the Preferences
     * @param key Key
     * @param defValue Default Value
     * @return Value or Default value
     */

    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

}
