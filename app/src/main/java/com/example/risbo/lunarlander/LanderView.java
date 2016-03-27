package com.example.risbo.lunarlander;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import stanford.androidlib.graphics.GCanvas;
import stanford.androidlib.graphics.GColor;
import stanford.androidlib.graphics.GLabel;
import stanford.androidlib.graphics.GSprite;
import stanford.androidlib.util.RandomGenerator;


public class LanderView extends GCanvas {
    private static final float MAX_SAFE_LANDING_VELOCITY = 7.0f;


    private GSprite rocket;
    private GSprite moonSurface;
    private GLabel points;
    // create an ArrayList for all the asteroids that have been generated
    private ArrayList<GSprite> asteroids = new ArrayList<>();
    private int frames = 0;
    private int count = 0;

    public LanderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void init() {

        setBackgroundColor(GColor.BLACK);

        /*TO SEE RECTANGLE FOR COLLISIONS REALLY USEFUL
        GSprite.setDebug(true);*/

        Bitmap moonImage = BitmapFactory.decodeResource(
                getResources(), R.drawable.moonsuface
        );

        float newWidth = getWidth()  ;
        float newHeight = moonImage.getHeight() / (moonImage.getWidth() / getWidth());

        moonImage = Bitmap.createScaledBitmap(moonImage, (int) newWidth, (int) newHeight, true);

        moonSurface = new GSprite(moonImage);
        moonSurface.setBottomY(getHeight());
        moonSurface.setCollisionMarginTop(150);
        add(moonSurface);

        //Make an ArrayList of Bitmap to animate Sprite
        ArrayList<Bitmap> rocketPics = new ArrayList<>();
        rocketPics.add(loadScaleBitmap(R.drawable.rocket2,2));
        rocketPics.add(loadScaleBitmap(R.drawable.rocket3, 2));
        rocketPics.add(loadScaleBitmap(R.drawable.rocket4, 2));

        rocket = new GSprite(rocketPics,60,10);
        rocket.setFramesPerBitmap(3);
        rocket.setVelocityY(5);
        rocket.setAccelerationY(0.25f);
        rocket.setCollisionMargin(150);
        add(rocket);


        points = new GLabel(String.valueOf(count), 750, 10);
        points.setFont(Typeface.MONOSPACE, Typeface.BOLD,400f);
        points.setColor(GColor.WHITE);
        add(points);

        animate(30);

    }


    private Bitmap loadScaleBitmap(int id, int factor) {
        Bitmap image = BitmapFactory.decodeResource(
                getResources(), id
        );

        image = Bitmap.createScaledBitmap(image,
                image.getWidth() / factor,
                image.getHeight() / factor,
                true);
        return image;
    }


    //onAnimateTick() is basically the update of everything
    @Override
    public void onAnimateTick() {
        super.onAnimateTick();

        frames++;
        if(frames % 60 == 0){
            // add an asteroid every 60 frames
            GSprite asteroid = new GSprite(loadScaleBitmap(R.drawable.asteroid,2));
            asteroid.setRightX(getWidth());
            float y = RandomGenerator.getInstance().nextFloat((getHeight()));
            if (y > (getHeight()-moonSurface.getHeight())) {
                y -= ( moonSurface.getHeight());
            }

          /*  Log.v("moon", "aster height " + y);
            Log.v("moon", "moonsurface height " + ( moonSurface.getHeight()));
            Log.v("moon", "screen height " + ( getHeight()));*/

            asteroid.setY(y);
            asteroid.setVelocityX(-9);

            asteroid.setCollisionMargin(10);
            add(asteroid);
            //add the generated asteroid to the ArrayList of all the asteroids generated
            asteroids.add(asteroid);



        }

        if(frames % 3 == 0){
            countUp();
        }


        //collisions
        doCollisions();
        //update

    }

    private void doCollisions(){

        if(rocket.collidesWith(moonSurface)){
            rocket.stop(); //stop moving the sprite
        }

        for (GSprite asteroid: asteroids){ //for each asteroid generated in the collection, check if it collides with the rocket
                if (rocket.collidesWith(asteroid)){
                    //all stops, game over
                    animationStop();
                }
            }
        }


    private void countUp() {
        for(GSprite asteroid : asteroids){

            if ( asteroid.getX() < 50 && asteroid.getX() > 0 && asteroid.getY() < getWidth() && asteroid.getY() > 0){
                count++;

            }

            points.setLabel(String.valueOf(count));

//            Log.v("count", "count = " + count);
//            Log.v("count", "asteroid Y = " + asteroid.getY());
//            Log.v("count","asteroid X = " + asteroid.getX());
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            // NB : NO LOOPING HERE

            //thrust (acceleration)
            rocket.setAccelerationY(-.25f);

            return true;

        } else if (event.getAction()==MotionEvent.ACTION_UP){
            //stop thrust
            rocket.setAccelerationY(.5f);

        }

        return super.onTouchEvent(event);
    }

    public void startGame() {
        rocket.setLocation(50, 10);
        for (GSprite asteroid : asteroids){
            remove(asteroid);

        }
        asteroids.clear();
        rocket.setVelocityY(5);
        rocket.setAccelerationY(0);
        count = 0;
        animate(30);



    }

    public void stopGame() {
        animationStop();
    }

}
