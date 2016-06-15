package org.efafi.efafiinternational;

/* NotificationActivity.java
This activity just acts as a mirror to redirect the user to
the main activity after they hit a notification.
 */

import android.app.Activity;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Intent;

public class NotificationActivity extends Activity {
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        Timer timer = new Timer();
        timer.schedule( new TimerTask() { // set timer to 0 for immediate redirect
            @Override
            public void run() { // send user to the home page
                finish();
                startActivity( new Intent( NotificationActivity.this, MainActivity.class ) );
            }
        }, 0);
    }
}