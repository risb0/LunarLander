package com.example.risbo.lunarlander;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import stanford.androidlib.SimpleActivity;

public class LanderActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faceview);
    }

  /*  public void playClick(View view){
        final LanderView faceView = new LanderView(this,);
        faceView.startGame();
    }

    public void stopClick(View view){
        final LanderView faceView = ...;
        faceView.stopGame();
    }*/

}
 