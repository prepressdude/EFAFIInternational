package org.efafi.efafiinternational;

/* RssAlarm.java
This class initiates when the main activity sends out an alarm signal.
It checks the rss data on the website and sends out a notification if
there is a new news item there.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class RssAlarm extends BroadcastReceiver {
    protected int notificationID;
    protected Context context;

    @Override
    public void onReceive( Context givenContext, Intent intent ) { // when called by the alarm, check rss
        context = givenContext;
        checkRSS();
    }

    public void checkRSS() { // check the rss url for an update
        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL( context.getResources().getString( R.string.feed_url ) ); // make the connection
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setReadTimeout( 10000 );
                    http.setConnectTimeout( 15000 );
                    http.setRequestMethod( "GET" );
                    http.setDoInput( true );
                    http.connect();

                    Scanner scanner = new java.util.Scanner( http.getInputStream() ).useDelimiter( "\\A" ); // pull the xml
                    String xml = scanner.hasNext() ? scanner.next() : "";

                    if ( xml.contains( "<pubDate>" ) ) { // narrow in on the date of the latest entry
                        xml = xml.split( "<pubDate>" )[ 1 ];
                        xml = xml.split( "</pubDate>" )[ 0 ];
                    }

                    Date pubDate = null;
                    try { // parse the date
                        DateFormat dateFormat = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz" );
                        pubDate = dateFormat.parse( xml );
                    } catch ( ParseException e ) { e.printStackTrace(); }

                    if ( pubDate != null ) { // compare the date to the previous date from preferences
                        Date previousDate = null;
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( context );

                        try {
                            previousDate = new Date( preferences.getLong( "time", 0 ) );
                        } catch ( Exception e ) { e.printStackTrace(); }

                        if ( previousDate == null || pubDate.after( previousDate ) ) { // if it's new, notify & update preferences
                            Log.i( "Testing", "previousDate = " + previousDate.toString() );
                            Log.i( "Testing", "pubDate = " + pubDate.toString() );
                            sendNotification();
                            SharedPreferences settings = preferences;
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putLong( "time", pubDate.getTime() );
                            editor.commit();
                        }
                    }
                } catch ( Exception e ) { e.printStackTrace(); }
            }
        });
        thread.start();
    }

    private void sendNotification() { // send out notification to user
        NotificationCompat.Builder builder = new NotificationCompat.Builder( context ); // format the notification
        builder.setSmallIcon( R.drawable.notification_icon );
        builder.setContentTitle( context.getResources().getString( R.string.notification_title ) );
        builder.setContentText( context.getResources().getString( R.string.notification_text ) );
        builder.setAutoCancel( true );

        Intent intent = new Intent( context, NotificationActivity.class ); // build the intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create( context );
        stackBuilder.addParentStack( MainActivity.class );
        stackBuilder.addNextIntent( intent );
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        builder.setContentIntent( resultPendingIntent );

        NotificationManager notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( notificationID, builder.build() ); // send the notification
    }
}
