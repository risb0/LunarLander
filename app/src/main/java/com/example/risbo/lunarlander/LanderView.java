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

import java.text.DecimalFormat;
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
    private GLabel velocityLabel;
    private GLabel endMessageLabel;
    // create an ArrayList for all the asteroids that have been generated
    private ArrayList<GSprite> asteroids = new ArrayList<>();
    private int frames = 0;
    private int count = 0;
    private int pointsLen = 1;
    private int prevPointsLen = 1;
    private String rocketVelocity = "";

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

        // create the Labels
        points = new GLabel(String.valueOf(count));
        points.setFont(Typeface.MONOSPACE, Typeface.BOLD, 80f);
        points.setColor(GColor.WHITE);
        points.setRightX(getWidth() - points.getText().length() * points.getFontSize());
        add(points);

        velocityLabel = new GLabel("Velocity: 0 / " + MAX_SAFE_LANDING_VELOCITY);
        velocityLabel.setFont(Typeface.MONOSPACE, Typeface.BOLD, 60);
        velocityLabel.setColor(GColor.WHITE);
        velocityLabel.setLocation(10, 10);
        add(velocityLabel);

        endMessageLabel = new GLabel("");
        endMessageLabel.setLocation(10, points.getFontSize());
        endMessageLabel.setFont(Typeface.MONOSPACE, Typeface.BOLD, 50 * getResources().getDisplayMetrics().density);


        animate(30); // 30 frames per second

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

        rocketVelocity = new DecimalFormat("#.##").format(rocket.getVelocityY());
        updateLabels();

        frames++;
        if(frames % 30 == 0){
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
            asteroid.setVelocityX(-15);

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

            if (rocket.getVelocityY() <= MAX_SAFE_LANDING_VELOCITY){
                endMessageLabel.setText(R.string.you_win);
                endMessageLabel.setColor(GColor.GREEN);
                add(endMessageLabel);

            } else{
                endMessageLabel.setText(R.string.you_lose);
                endMessageLabel.setColor(GColor.RED);
                add(endMessageLabel);

            }
            rocket.stop(); //stop moving the sprite
            animationStop();

        }

        for (GSprite asteroid: asteroids){ //for each asteroid generated in the collection, check if it collides with the rocket
                if (rocket.collidesWith(asteroid)){
                    //all stops, game over
                    endMessageLabel.setText(R.string.you_lose);
                    endMessageLabel.setColor(GColor.RED);
                    add(endMessageLabel);
                    animationStop();
                }
            }
        }


    private void countUp() {


        for(GSprite asteroid : asteroids){

             prevPointsLen = pointsLen;

            if ( asteroid.getX() < 50 && asteroid.getX() > 0 && asteroid.getY() < getWidth() && asteroid.getY() > 0){
                count++;

            }
            String scoreStr = getResources().getString(
                    R.string.score, count
            );

            points.setLabel(scoreStr);
            pointsLen =  points.getText().length();


            if (pointsLen != prevPointsLen){
                points.setRightX(getWidth() - points.getText().length());
                add(points);

            }

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
        rocket.setAccelerationY(.25f);
        count = 0;
        endMessageLabel.setText("");

        animate(30);



    }


    public void updateLabels() {

        String velocityStr = getResources().getString(R.string.velocity, rocketVelocity, MAX_SAFE_LANDING_VELOCITY);
        velocityLabel.setText(velocityStr);

    }


    public void stopGame() {
        animationStop();
    }

}
