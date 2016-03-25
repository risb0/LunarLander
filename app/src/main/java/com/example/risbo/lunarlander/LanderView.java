package com.example.risbo.lunarlander;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;

import stanford.androidlib.graphics.GCanvas;
import stanford.androidlib.graphics.GSprite;

/**
 * Created by Risbo on 24/03/16.
 */
public class LanderView extends GCanvas {
    private static final float MAX_SAFE_LANDING_VELOCITY = 7.0f;


    private GSprite rocket;

    public LanderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init() {
        Bitmap rocketImage = BitmapFactory.decodeResource(
                getResources(), R.drawable.rocket1
        );
        rocket = new GSprite(rocketImage,50,10);
        rocket.setVelocityY(5);
        add(rocket);

        animate(30);

    }

    @Override
    public void onAnimateTick() {
        super.onAnimateTick();
         // TODO: 25/03/16
    }

    public void startGame() {

    }

    public void stopGame() {

    }

}
