package co.uk.images.coketruck;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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

public class MainActivity extends Activity {
    Pusher pusher;
    RelativeLayout ll;
    Typeface rt;
    public boolean newGif = false;
    String sessionID;
    boolean gotImage = false;
    private boolean subscribed;
    private boolean gotForm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rt = Typeface.createFromAsset(this.getAssets(), "Montserrat-Regular.ttf");
        setContentView(R.layout.setone);
        setButtonListenerOne();
        if (!subscribed){
            this.enablePusher();
        }
    }

    private void setButtonListenerOne() {
        ImageButton next1 = (ImageButton) this.findViewById(R.id.next);
        next1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("next pressed");
                if(gotImage){
                    MainActivity.this.setContentView(R.layout.formview);
                    gotForm = true;
                    setButtonListenerTwo();
                }
            }
        });
    }
    private void setButtonListenerTwo() {
        ImageButton next2 = (ImageButton) this.findViewById(R.id.next2);
        next2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("next pressed");
                if(gotForm){
                    MainActivity.this.initEmailSending();


                }
            }
        });
    }
    private void setButtonListenerThree() {
        ImageButton next2 = (ImageButton) this.findViewById(R.id.next2);
        next2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gotForm = false;
                gotImage = false;
                MainActivity.this.setContentView(R.layout.setone);

            }
        });
    }

    private WebView generateWebView(String url, boolean scrolled) {
        WebView view = new WebView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(toDp(320), toDp(480));
        params.setMargins(toDp(5), scrolled? toDp(5): -toDp(235), 0, 0);
        view.setLayoutParams(params);
        view.loadUrl(url);

        return view;
//        view.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView view, String url) {
//                view.scrollTo(0, 240);
//            }
//        });
    }
    private int toDp(int measure){
        final float scale = this.getResources().getDisplayMetrics().density;
        int pixels = (int) (measure * scale + 0.5f);
        return pixels;
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

        Channel channel = pusher.subscribe("gifs");
        subscribed = true;
        channel.bind("giffinish", new SubscriptionEventListener() {

            @Override
            public void onEvent(String channel, String event, final String data) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        newGif = true;
                        String gifURL=data.replace("\"","");
                        sessionID = gifURL;
                        RelativeLayout frame1= (RelativeLayout) findViewById(R.id.frame1);
                        RelativeLayout frame2= (RelativeLayout) findViewById(R.id.frame2);
                        String url = "http://cocacolahappiness.s3-website-eu-west-1.amazonaws.com/"+gifURL+".gif";
                        WebView wb = generateWebView(url, true);
                        frame1.addView(wb, 0);
                        frame1.setRotation(-15);
                        WebView second = generateWebView(url, false);
                        frame2.addView(second, 0);
                        frame2.setRotation(15);
                        gotImage = true;
                    }
                });
            }
        });
    }
    private void initEmailSending() {
        EditText emailField = (EditText) findViewById(R.id.emailField);
        EditText nameField = (EditText) findViewById(R.id.nameField);
        EditText captionField = (EditText) findViewById(R.id.captionField);
        String emailText = emailField.getText().toString();
        String nameText = nameField.getText().toString();
        String captionText = captionField.getText().toString();
        if(!emailText.contains("@")){
            Toast.makeText(MainActivity.this, "Please use a valid email address", Toast.LENGTH_SHORT).show();
        }
        else if(nameText.equals("")){
            Toast.makeText(MainActivity.this, "Please fill the name field", Toast.LENGTH_SHORT).show();
        }
        else{
            String emailReqURL = "http://ccht.imagesstage.co.uk/email.php?session="+sessionID+"&email="+emailText+"&name="+nameText+"&caption="+captionText;
            new GetURL().execute(emailReqURL);
            MainActivity.this.setContentView(R.layout.tickview);
        }
    }
}