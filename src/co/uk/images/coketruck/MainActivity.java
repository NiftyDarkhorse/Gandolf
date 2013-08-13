package co.uk.images.coketruck;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends Activity {

    Movie movie;
    Canvas animationCanvasOne;
    View thisView;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView view = new GifWebView(this);
        setContentView(view);
    }
    public class GifWebView extends WebView {

        public GifWebView(Context context) {
            super(context);
            loadUrl("http://10.254.26.28:8888/front.gif");
            System.out.println("Loaded");
        }
        @Override
        protected void onDraw(Canvas canvas){

        }
    }
}
