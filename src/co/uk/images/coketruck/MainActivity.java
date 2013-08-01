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

//        main = new Canvas();
//
//
//        thisView = findViewById(R.id.mainLayout);
//
//        thisView.draw(animationCanvasOne);
//        new GetGifFromNetwork().execute();


    }

    class GetGifFromNetwork extends AsyncTask<Void, Integer, Movie> {
        protected Movie doInBackground(Void... objects) {
            InputStream in;
            try {
                in = new URL("http://10.254.26.28:8888/front.gif").openStream();
                movie = Movie.decodeStream(in);
                return movie;
            }
            catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            }
            return null;

              //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(Movie io) {
            super.onPostExecute(io);    //To change body of overridden methods use File | Settings | File Templates.


            movie.draw(animationCanvasOne, 200,200);
            thisView.invalidate();
            System.out.println("Gif loaded");
        }
    }
    public class GifWebView extends WebView {

        public GifWebView(Context context) {
            super(context);
            loadUrl("http://10.254.26.28:8888/front.gif");
        }
        @Override
        protected void onDraw(Canvas canvas){

        }
    }

    public class GifView extends View{

        public GifView(Context context) {
            super(context);
        }
    }

}
