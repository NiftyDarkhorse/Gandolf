package co.uk.images.coketruck;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import android.widget.TextView;
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
    RelativeLayout ll;
    Typeface rt;
    public boolean newGif = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rt = Typeface.createFromAsset(this.getAssets(),"Roboto-Thin.ttf");
        setContentView(R.layout.main);
        TextView fb = (TextView) findViewById(R.id.facebookText);
        fb.setTypeface(rt);
        TextView vk = (TextView) findViewById(R.id.vkText);
        vk.setTypeface(rt);
        ll = (RelativeLayout) this.findViewById(R.id.mainLayout);
        if (pusher==null){
            this.enablePusher();
        }
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
                        view1.loadUrl("http://10.254.26.69/front.gif");
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(420,620);
                        params.leftMargin = 30;
                        params.topMargin = 40;
                        ll.addView(view1, params);
                        ll.invalidate();
                        System.out.println("Invalidated");
                    }
                });
            }
        });
    }
}
