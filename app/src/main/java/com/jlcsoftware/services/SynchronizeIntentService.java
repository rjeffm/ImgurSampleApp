package com.jlcsoftware.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.jlcsoftware.api.ImgurSyncronzier;
import com.jlcsoftware.sampleapp.AppPreferences;

import org.json.JSONException;

import java.io.IOException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * This class mocks a SyncAdapter, since SyncAdapters need more code and dependencies (ContentProvider, etc) to work correctly
 * <p/>
 * <p/>
 * helper methods.
 */
public class SynchronizeIntentService extends IntentService {
    public static final String TAG = "SynchronizeIntentService"; // not a good idea for proguard?

    public static final String ACTION_REFRESH = "ca.jlcreative.services.action.REFRESH";
    public static final String ACTION_CLEANUP = "ca.jlcreative.services.action.CLEANUP";

    public static String BROADCAST_GALLERIES_SYNCHRONIZED = "com.jlcsoftware.broadcast.GALLERIES_SYNCHRONIZED";

    public SynchronizeIntentService() {
        super("SycnronizeIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRefresh(Context context) {
        Intent intent = new Intent(context, SynchronizeIntentService.class);
        intent.setAction(ACTION_REFRESH);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCleanup(Context context) {
        Intent intent = new Intent(context, SynchronizeIntentService.class);
        intent.setAction(ACTION_CLEANUP);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH.equals(action)) {
                handleActionRefresh( intent);

            } else if (ACTION_CLEANUP.equals(action)) {
                handleActionCleanup();
            }
        }
    }

    /**
     * Handle action Refresh in the provided background thread
     */
    private void handleActionRefresh(Intent intent) {
        ImgurSyncronzier.refresh(getApplicationContext());
        // We don't do anything with this at the moment... in the real world we would
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(BROADCAST_GALLERIES_SYNCHRONIZED));
    }

    /**
     * Handle action Cleanup in the provided background thread
     */
    private void handleActionCleanup() {
        ImgurSyncronzier.cleanup(getApplicationContext());
    }


    /**
     * Set the AlarmManager schedules to run this service at specific times, mocking a SyncAdapter
     *
     * @param context A context
     */

    public static void setSchedules(Context context) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SynchronizeIntentService.class);

        // Cleanup Action - Removes ALL stale files
        intent.setAction(SynchronizeIntentService.ACTION_CLEANUP);
        alarmIntent = PendingIntent.getService(context, 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                3 * 60 * 1000, // Clean in 3 minutes
                AlarmManager.INTERVAL_DAY, // then every day after that
                alarmIntent);

        // Refresh Action - refreshes the top galleries
        intent.setAction(SynchronizeIntentService.ACTION_REFRESH);
        alarmIntent = PendingIntent.getService(context, 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, // Refresh in 15 minutes (we call a refresh on startup)
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, // Then ever 15 minuts after that...
                alarmIntent);

    }

}
