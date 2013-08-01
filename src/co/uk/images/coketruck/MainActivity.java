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
    Movie [] bothMovies;
    Canvas animationCanvasOne;
    View thisView;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bothMovies = new Movie[2];
        GifView view = new GifView(this);

        //This is no good:
        @Deprecated
        WebView theWebView = new GifWebView(this);

        setContentView(view);

//        main = new Canvas();
//
//
//        thisView = findViewById(R.id.mainLayout);
//
//        thisView.draw(animationCanvasOne);
//        new GetGifFromNetwork().execute();


    }

    class GetGifFromNetwork extends AsyncTask<Void, Integer, Movie[]> {
        protected Movie[] doInBackground(Void... objects) {
            InputStream in;

            try {
                in = new URL("http://10.254.26.28:8888/front.gif").openStream();
                movie1 = Movie.decodeStream(in);
                in = new URL("http://10.254.26.28:8888/front.gif").openStream();
                movie2 = Movie.decodeStream(in);
                Movie[] moviearray = {movie1, movie2};
                return moviearray;
            }
            catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] io) {
            super.onPostExecute(io);

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
            new GetGifFromNetwork().execute();
        }

    }

}
