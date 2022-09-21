package com.nativegame.engine;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View {

    private ArrayList<ArrayList<GameObject>> mLayers;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGameObjects(ArrayList<ArrayList<GameObject>> gameObjects) {
        mLayers = gameObjects;
    }

    public void draw() {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mLayers) {
            int layers = mLayers.size();
            for (int i = 0; i < layers; i++) {
                ArrayList<GameObject> currentLayer = mLayers.get(i);
                int size = currentLayer.size();
                for (int j = 0; j < size; j++) {
                    currentLayer.get(j).onDraw(canvas);
                }
            }
        }
    }

}
