package co.uk.images.coketruck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GifView extends View {
    long movieStart = 0;
    boolean processFinish = false;
    Movie [] bothMovies;

    // The following are the three View constructors  that load the Gifs asynchronously from the server...

//    public GifView(Context context) {
//
//        super(context);
//        new GetGifFromNetwork().execute();
//        bothMovies = new Movie[2];
//    }
//    public GifView(Context context, AttributeSet attrs){
//        super(context, attrs);
//        new GetGifFromNetwork().execute();
//        bothMovies = new Movie[2];
//    }
//    public GifView(Context context, AttributeSet attrs, int defStyle){
//        super(context, attrs, defStyle);
//        new GetGifFromNetwork().execute();
//        bothMovies = new Movie[2];
//
//    }

    // ...and the following three load two test Gifs from the static directories, for debugging purposes.

    public GifView(Context context) {

        super(context);
        bothMovies = new Movie[]
                {Movie.decodeStream(getResources().openRawResource(R.drawable.back)),
                        Movie.decodeStream(getResources().openRawResource(R.drawable.front))
                };
    }
    public GifView(Context context, AttributeSet attrs){
        super(context, attrs);
        bothMovies = new Movie[]
                {Movie.decodeStream(getResources().openRawResource(R.drawable.back)),
                        Movie.decodeStream(getResources().openRawResource(R.drawable.front))
                };
    }
    public GifView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        bothMovies = new Movie[]
                {Movie.decodeStream(getResources().openRawResource(R.drawable.back)),
                        Movie.decodeStream(getResources().openRawResource(R.drawable.front))
                };

    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        Paint p = new Paint();
        p.setAntiAlias(true);
        if(movieStart == 0){
            movieStart = now;   }
        int relTime;
        relTime = (int)((now - movieStart) % bothMovies[0].duration());
        bothMovies[0].setTime(relTime);
        bothMovies[0].draw(canvas,0,0);
        relTime = (int)((now - movieStart) % bothMovies[1].duration());
        bothMovies[1].setTime(relTime);
        bothMovies[1].draw(canvas,0,300);
        this.invalidate();

    }
    class GetGifFromNetwork extends AsyncTask<Void, Integer, Movie[]> {
        protected Movie[] doInBackground(Void... objects) {
            InputStream in,in2;
            System.out.println("Getting movie");
            try {
                in = new URL("http://10.254.26.28:8888/front.gif").openStream();
                Movie movie1 = Movie.decodeStream(in);
//                in2 = new URL("http://10.254.26.28:8888/back.gif").openStream();
//                Movie movie2 = Movie.decodeStream(in2);
//                return new Movie[]{movie1, movie2};
            }
            catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);

            }
            return null;
        }
        @Override
        protected void onPostExecute(Movie[] io) {
            super.onPostExecute(io);
            processFinish = true;
            bothMovies = io;
            System.out.println("Process finished");

        }
    }
}