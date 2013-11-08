package co.uk.images.coketruck;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.*;

import com.dansd.UDP.Server;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;

public class MainActivity extends Activity {
    RelativeLayout ll;
    Typeface rt;
    public boolean newGif = false;
    String sessionID;
    boolean gotImage = false;
    private boolean subscribed;
    private boolean gotForm;
    private AppServer udpServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rt = Typeface.createFromAsset(this.getAssets(), "Montserrat-Regular.ttf");
        setContentView(R.layout.setone);
        udpServer = new AppServer(9876);
        udpServer.listen();
        setButtonListenerOne();
    }

    @Override
    protected void onPause() {
        this.onStop();
    }

    /**
     * Subclass of Server, to listen for UDP packets
     */
    private class AppServer extends Server{

            public AppServer(int port) {
                super(port);
            }

        /**
         * Defines what to do when an UDP package arrives
         * @param reqString the body of the request
         * @return the response byte array
         */
            @Override
            public byte[] onRequest(byte[] reqString) {
                System.out.println(lastIP);
                onImageReceived(reqString);
                return "ok".getBytes();

            }
    }

    /**
     * Initialises a listener for the first next button, which takes the user to the form
     */

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

    /**
     * Initialises a listener for the second next button, which takes the user to the confirmation view
     */
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

    /**
     * Initialises a listener for the back button, which takes the user to the initial view
     */
    private void setButtonListenerThree() {
        ImageButton next2 = (ImageButton) this.findViewById(R.id.back);
        next2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gotForm = false;
                gotImage = false;
                MainActivity.this.setContentView(R.layout.setone);

            }
        });
    }

    /**
     * Sets up a WebView based on the gif url it received, and positions it according to its scroll state
     * @param url A GIF URL
     * @param scrolled Whether the frame shown is the one in the top or the bottom
     * @return the formatted WebView object
     */
    private WebView generateWebView(String url, boolean scrolled) {
        WebView view = new WebView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(toDp(320), toDp(480));
        params.setMargins(toDp(5), scrolled? toDp(5): -toDp(235), 0, 0);
        view.setLayoutParams(params);
        view.loadUrl(url);

        return view;
    }

    /**
     * Converts density-indepent pixels into actual screen pixels.
     * @param measure the value in dp
     * @return the value in px
     */
    private int toDp(int measure){
        final float scale = this.getResources().getDisplayMetrics().density;
        int pixels = (int) (measure * scale + 0.5f);
        return pixels;
    }

    /**
     * Asynctask subclass to send request for email
     */
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

    /**
     * Method that is executed when a signal from the recorder is caught
     * @param data the byte array coming from the recorder
     */
    private void onImageReceived(final byte[] data){
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                newGif = true;
                String dataString = new String(data);
                String url = dataString.split("&")[0];
                sessionID = dataString.split("&")[1].substring(0,13);
                System.out.println(sessionID);
                RelativeLayout frame1= (RelativeLayout) findViewById(R.id.frame1);
                RelativeLayout frame2= (RelativeLayout) findViewById(R.id.frame2);
                WebView wb = generateWebView(url, true);
                System.out.println("generating view change");
                frame1.addView(wb, 0);
                frame1.setRotation(-15);
                WebView second = generateWebView(url, false);
                frame2.addView(second, 0);
                frame2.setRotation(15);
                frame1.invalidate();
                frame2.invalidate();
                gotImage = true;
            }
        });
    }

    /**
     * Begins the email sending process, by sending a request to a web server.
     */
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