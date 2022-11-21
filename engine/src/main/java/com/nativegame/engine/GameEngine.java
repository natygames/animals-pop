package com.nativegame.engine;

import android.content.Context;

import com.nativegame.engine.collision.QuadTree;
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
    private final ArrayList<GameObject> mObjectsToAdd = new ArrayList<>();
    private final ArrayList<GameObject> mObjectsToRemove = new ArrayList<>();
    private final QuadTree mQuadTree = new QuadTree();

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
        // Init the configure
        mScreenWidth = gameView.getWidth() - gameView.getPaddingRight() - gameView.getPaddingLeft();
        mScreenHeight = gameView.getHeight() - gameView.getPaddingTop() - gameView.getPaddingBottom();
        mPixelFactor = mScreenWidth / 3000f;   // Default value
        // Init the collision area of quadtree
        mQuadTree.init(this);
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
        checkCollision();
        synchronized (mLayers) {
            while (!mObjectsToRemove.isEmpty()) {
                GameObject objectToRemove = mObjectsToRemove.remove(0);
                if (mGameObjects.remove(objectToRemove)) {
                    mLayers.get(objectToRemove.mLayer).remove(objectToRemove);
                    if (objectToRemove instanceof Sprite) {
                        mQuadTree.removeCollisionObject((Sprite) objectToRemove);
                    }
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

    private void checkCollision() {
        mQuadTree.checkCollision(this);
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
                mQuadTree.addCollisionObject(sprite);
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
