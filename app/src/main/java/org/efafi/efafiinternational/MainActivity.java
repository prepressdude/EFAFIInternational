package org.efafi.efafiinternational;

/* MainActivity.java
The main activity of the app. This page mirrors the website and sets the
alarm to check the rss feed of the website every 5 minutes for updates.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.webkit.WebView;

public class MainActivity extends Activity {
    private final long frequency = 300 * 1000;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        WebView webview = (WebView) findViewById( R.id.webView );
        webview.loadUrl( getResources().getString( R.string.main_url ) ); //pull URL data

        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ); // set rss feed alarm
        Intent intent = new Intent( this, RssAlarm.class );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( this, 0, intent, 0 );
        alarmManager.setRepeating( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), frequency, pendingIntent );
    }
}