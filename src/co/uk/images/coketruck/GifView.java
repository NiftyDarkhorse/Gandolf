package co.uk.images.coketruck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class GifView extends View {
    long movieStart = 0;
    boolean processFinish = false;
    Movie [] bothMovies;
    boolean staticMode = false;
    private boolean done = false;
    Context ctx;

    // The following are the three View constructors  that load the Gifs asynchronously from the server...

    public GifView(Context context) {

        super(context);
        new GetGifFromNetwork().execute();
        bothMovies = new Movie[2];
        ctx = context;
    }
    public GifView(Context context, AttributeSet attrs){
        super(context, attrs);
        new GetGifFromNetwork().execute();
        bothMovies = new Movie[2];
        ctx = context;
    }
    public GifView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        new GetGifFromNetwork().execute();
        bothMovies = new Movie[2];
        ctx = context;

    }

    // ...and the following three load two test Gifs from the static directories, for debugging purposes.

//    public GifView(Context context) {
//
//        super(context);
//        bothMovies = new Movie[]
//                {Movie.decodeStream(getResources().openRawResource(R.drawable.back)),
//                        Movie.decodeStream(getResources().openRawResource(R.drawable.front))
//                };
//    processFinish = true;
//    }
//    public GifView(Context context, AttributeSet attrs){
//        super(context, attrs);
//        bothMovies = new Movie[]
//                {Movie.decodeStream(getResources().openRawResource(R.drawable.back)),
//                        Movie.decodeStream(getResources().openRawResource(R.drawable.front))
//                };
//    processFinish = true;
//    }
//    public GifView(Context context, AttributeSet attrs, int defStyle){
//        super(context, attrs, defStyle);
//        bothMovies = new Movie[]
//                {Movie.decodeStream(getResources().openRawResource(R.drawable.back)),
//                        Movie.decodeStream(getResources().openRawResource(R.drawable.front))
//                };
//    processFinish = true;

//    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(staticMode){
            System.out.println("Starting stuff, very quickly");
             getTheStuffInTheCanvas(canvas);
        }
        else{
            while(!done){
                if(processFinish){
                    System.out.println("Starting stuff");
                    getTheStuffInTheCanvas(canvas);
                    done = true;
                    staticMode = true;
                }
            }
        }

    }
    class GetGifFromNetwork extends AsyncTask<Void, Integer, Movie[]> {
        protected Movie[] doInBackground(Void... objects) {
            InputStream in,in2;
            System.out.println("Getting movie");
            try {
                Movie movie1 = urlToMovie(new URL("http://10.254.26.28:8888/front.gif"));
                Movie movie2 = urlToMovie(new URL("http://10.254.26.28:8888/back.gif"));
                System.out.println("Stream closed");
                processFinish = true;
                bothMovies = new Movie[]{movie1, movie2};
                return bothMovies;
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("Shit got fucked");

            }
            return null;
        }
        @Override
        protected void onPostExecute(Movie[] io) {
            System.out.println("Post execute...");
            super.onPostExecute(io);
            System.out.println("Process finished");

        }
    }
    protected void getTheStuffInTheCanvas(Canvas canvas){
        long now = android.os.SystemClock.uptimeMillis();
        Paint p = new Paint();
        p.setAntiAlias(true);
        if(movieStart == 0){
            movieStart = now;   }
        int relTime;

        // Drawing first image
        relTime = (int)((now - movieStart) % bothMovies[0].duration());
        bothMovies[0].setTime(relTime);
        bothMovies[0].draw(canvas,0,0);

        // Now second image
        relTime = (int)((now - movieStart) % bothMovies[1].duration());
        bothMovies[1].setTime(relTime);
        bothMovies[1].draw(canvas,0,300);
        this.invalidate();
    }
    protected Movie urlToMovie(URL url) throws IOException {

        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        bis.mark(conn.getContentLength());
        Movie movie = Movie.decodeStream(bis);
        bis.close();
        return movie;

    }
}