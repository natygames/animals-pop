package com.nativegame.engine;

import android.graphics.Canvas;

public abstract class GameObject {

    public int mLayer;

    public abstract void startGame(GameEngine gameEngine);

    public abstract void onUpdate(long elapsedMillis, GameEngine gameEngine);

    public abstract void onDraw(Canvas canvas);

    public void onPostUpdate() {
    }

    public void onGameEvent(GameEvent gameEvent) {
    }

    public void addToGameEngine(GameEngine gameEngine, int layer) {
        gameEngine.addGameObject(this, layer);
    }

    public void removeFromGameEngine(GameEngine gameEngine) {
        gameEngine.removeGameObject(this);
    }

    public void onAddedToGameEngine() {
    }

    public void onRemovedFromGameEngine() {
    }

}
