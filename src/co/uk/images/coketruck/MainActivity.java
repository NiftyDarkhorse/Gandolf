package co.uk.images.coketruck;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.connection.ConnectionEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends Activity {
    Pusher pusher;
    LinearLayout ll;
    public boolean newGif = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ll = (LinearLayout) this.findViewById(R.id.mainLayout);
        this.enablePusher();
    }

    protected void enablePusher(){
        pusher = new Pusher("c0a25a463fbbf294a4a8");
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
            }

            @Override
            public void onError(String message, String code, Exception e) {
            }

        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe( "gifs" );
        channel.bind("giffinish", new SubscriptionEventListener() {

            @Override
            public void onEvent(String channel, String event, String data) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        newGif = true;
                        System.out.println("Invalidating view");
                        WebView view1 = new WebView(MainActivity.this);
                        view1.loadUrl("http://10.254.26.28:8888/front.gif");
                        ll.addView(view1);
                        WebView view2 = new WebView(MainActivity.this);
                        view2.loadUrl("http://10.254.26.28:8888/back.gif");
                        ll.addView(view2);
                        ll.invalidate();
                        System.out.println("Invalidated");
                    }
                });
            }
        });
    }
}
