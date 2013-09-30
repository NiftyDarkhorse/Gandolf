package co.uk.images.coketruck;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.*;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.connection.ConnectionEventListener;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {
    Pusher pusher;
    RelativeLayout ll;
    Typeface rt;
    public boolean newGif = false;
    String sessionID;
    boolean gotImage = false;
    private boolean subscribed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rt = Typeface.createFromAsset(this.getAssets(),"Montserrat-Regular.ttf");
        setContentView(R.layout.main);
        TextView email = (TextView) findViewById(R.id.emailText);
        ((TextView) findViewById(R.id.textView)).setTypeface(rt);

        email.setTypeface(rt);
        Button emailButton = (Button) findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(gotImage){
                    EditText emailField = (EditText) findViewById(R.id.emailEdit);
                    EditText nameField = (EditText) findViewById(R.id.nameEdit);
                    String emailText = emailField.getText().toString();
                    String nameText = nameField.getText().toString();
                    if(!emailText.contains("@")){
                        Toast.makeText(MainActivity.this, "Please use a valid email address", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String emailReqURL = "http://ccht.imagesstage.co.uk/email.php?session="+sessionID+"&email="+emailText+"&name="+nameText;
                        Toast.makeText(MainActivity.this, "Sending...", Toast.LENGTH_SHORT).show();
                        new GetURL().execute(emailReqURL);
                        emailField.setText("");
                        nameField.setText("");
                    }
                }
            }
        });
        ll = (RelativeLayout) this.findViewById(R.id.mainLayout);
        if (!subscribed){
            this.enablePusher();
        }
    }
    protected class GetURL extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... url) {
            String response = "";
            try {
                HttpClient client = new DefaultHttpClient();
                String getURL = url[0];
                System.out.println(getURL);
                HttpGet get = new HttpGet(getURL);
                HttpResponse responseGet = null;
                responseGet = client.execute(get);
                HttpEntity resEntityGet = responseGet.getEntity();
                if (resEntityGet != null) {
                    response = EntityUtils.toString(resEntityGet);
                }
                System.out.println("sent to "+response);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Thanks! Your happiness moment has been submitted", Toast.LENGTH_SHORT).show();
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
        subscribed = true;
        channel.bind("giffinish", new SubscriptionEventListener() {

            @Override
            public void onEvent(String channel, String event, final String data) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        newGif = true;
                        String gifURL=data.replace("\"","");
                        sessionID = gifURL;
                        WebView view1 = new WebView(MainActivity.this);
                        ImageView gifMask = new ImageView(getBaseContext());
                        gifMask.setImageDrawable(getResources().getDrawable(R.drawable.gifmask));

                        view1.loadUrl("http://cocacolahappiness.s3-website-eu-west-1.amazonaws.com/"+gifURL+".gif");
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(420,620);
                        params.leftMargin = 30;
                        params.topMargin = 40;

                        ll.addView(view1, params);
                        ll.addView(gifMask, params);
                        ll.invalidate();
                        gotImage = true;
                    }
                });
            }
        });
    }
}

