package co.uk.images.coketruck;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

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
        setContentView(R.layout.main);
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.mainLayout);
        WebView view1 = new WebView(this);
        view1.loadUrl("http://10.254.26.28:8888/front.gif");
        ll.addView(view1);
        WebView view2 = new WebView(this);
        view2.loadUrl("http://10.254.26.28:8888/back.gif");
        ll.addView(view2);

    }
}
