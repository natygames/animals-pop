package com.nativegame.engine;

import android.content.Context;

import com.nativegame.engine.input.InputController;
import com.nativegame.engine.level.Level;
import com.nativegame.engine.sound.SoundManager;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;
import com.nativegame.engine.ui.GameActivity;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine {

    private final ArrayList<ArrayList<GameObject>> mLayers = new ArrayList<>();
    private final ArrayList<GameObject> mGameObjects = new ArrayList<>();
    private final ArrayList<Sprite> mCollisionObjects = new ArrayList<>();
    private final ArrayList<GameObject> mObjectsToAdd = new ArrayList<>();
    private final ArrayList<GameObject> mObjectsToRemove = new ArrayList<>();

    public final GameActivity mActivity;
    private final GameView mGameView;
    private UpdateThread mUpdateThread;
    private DrawThread mDrawThread;
    public InputController mInputController;
    public SoundManager mSoundManager;
    public Level mLevel;
    public Random mRandom = new Random();

    public final int mScreenWidth;
    public final int mScreenHeight;
    public float mPixelFactor;

    public GameEngine(GameActivity activity, GameView gameView) {
        mActivity = activity;
        mGameView = gameView;
        mGameView.setGameObjects(mLayers);

        mScreenWidth = gameView.getWidth() - gameView.getPaddingRight() - gameView.getPaddingLeft();
        mScreenHeight = gameView.getHeight() - gameView.getPaddingTop() - gameView.getPaddingBottom();
        mPixelFactor = mScreenWidth / 3000f;   // Default value
    }

    public void setPixelFactor(float basePixel) {
        mPixelFactor = mScreenWidth / basePixel;
    }

    public void setInputController(InputController inputController) {
        mInputController = inputController;
    }

    public void setSoundManager(SoundManager soundManager) {
        mSoundManager = soundManager;
    }

    public void setLevel(Level level) {
        mLevel = level;
    }

    //--------------------------------------------------------
    // Methods to change state of game
    // start, stop, pause, resume
    //--------------------------------------------------------
    public void startGame() {
        // Stop a game if it is running
        stopGame();

        // Setup the game objects
        int numObjects = mGameObjects.size();
        for (int i = 0; i < numObjects; i++) {
            mGameObjects.get(i).startGame(this);
        }

        if (mInputController != null) {
            mInputController.onStart();
        }

        // Start the update thread
        mUpdateThread = new UpdateThread(this);
        mUpdateThread.start();

        // Start the drawing thread
        mDrawThread = new DrawThread(this);
        mDrawThread.start();
    }

    public void stopGame() {
        if (mUpdateThread != null) {
            mUpdateThread.stopGame();
            mUpdateThread = null;
        }
        if (mDrawThread != null) {
            mDrawThread.stopGame();
        }
        if (mInputController != null) {
            mInputController.onStop();
        }
    }

    public void pauseGame() {
        if (mUpdateThread != null) {
            mUpdateThread.pauseGame();
        }
        if (mDrawThread != null) {
            mDrawThread.pauseGame();
        }
        if (mInputController != null) {
            mInputController.onPause();
        }
    }

    public void resumeGame() {
        if (mUpdateThread != null) {
            mUpdateThread.resumeGame();
        }
        if (mDrawThread != null) {
            mDrawThread.resumeGame();
        }
        if (mInputController != null) {
            mInputController.onResume();
        }
    }

    public boolean isRunning() {
        return mUpdateThread != null && mUpdateThread.isGameRunning();
    }

    public boolean isPaused() {
        return mUpdateThread != null && mUpdateThread.isGamePaused();
    }
    //========================================================

    public void onUpdate(long elapsedMillis) {
        int numObjects = mGameObjects.size();
        for (int i = 0; i < numObjects; i++) {
            mGameObjects.get(i).onUpdate(elapsedMillis, this);
            mGameObjects.get(i).onPostUpdate();
        }
        checkCollisions();
        synchronized (mLayers) {
            while (!mObjectsToRemove.isEmpty()) {
                GameObject objectToRemove = mObjectsToRemove.remove(0);
                if (mGameObjects.remove(objectToRemove)) {
                    mLayers.get(objectToRemove.mLayer).remove(objectToRemove);
                    objectToRemove.onRemovedFromGameEngine();
                }
            }
            while (!mObjectsToAdd.isEmpty()) {
                GameObject objectToAdd = mObjectsToAdd.remove(0);
                addToLayerNow(objectToAdd);
            }
        }
    }

    public void onDraw() {
        mGameView.draw();
    }

    private void checkCollisions() {
        int numObjects = mCollisionObjects.size();
        for (int i = 0; i < numObjects; i++) {
            Sprite spriteA = mCollisionObjects.get(i);
            for (int j = i + 1; j < numObjects; j++) {
                Sprite spriteB = mCollisionObjects.get(j);
                if (spriteA.checkCollision(spriteB)) {
                    spriteA.onCollision(this, spriteB);
                    spriteB.onCollision(this, spriteA);
                }
            }
        }
    }

    private void addToLayerNow(GameObject object) {
        int layer = object.mLayer;

        // Add new layer if need
        while (mLayers.size() <= layer) {
            mLayers.add(new ArrayList<>());
        }

        // Add to layers
        mLayers.get(layer).add(object);
        mGameObjects.add(object);
        if (object instanceof Sprite) {
            Sprite sprite = (Sprite) object;
            if (sprite.mBodyType != BodyType.None) {
                mCollisionObjects.add(sprite);
            }
        }
        object.onAddedToGameEngine();
    }

    public void addGameObject(final GameObject gameObject, int layer) {
        gameObject.mLayer = layer;
        if (isRunning()) {
            synchronized (mLayers) {
                mObjectsToAdd.add(gameObject);
            }
        } else {
            addToLayerNow(gameObject);
        }
    }

    public void removeGameObject(final GameObject gameObject) {
        synchronized (mLayers) {
            mObjectsToRemove.add(gameObject);
        }
    }

    public void onGameEvent(GameEvent gameEvent) {
        synchronized (mLayers) {
            // We notify all the GameObjects
            int numObjects = mGameObjects.size();
            for (int i = 0; i < numObjects; i++) {
                mGameObjects.get(i).onGameEvent(gameEvent);
            }
        }
    }

    public Context getContext() {
        return mGameView.getContext();
    }

}
