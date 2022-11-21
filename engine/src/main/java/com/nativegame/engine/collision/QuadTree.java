package com.nativegame.engine.collision;

import android.graphics.Rect;

import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {

    private static final int MAX_TREE_NODE = 12;

    private final List<TreeNode> mTreeNodePool = new ArrayList<TreeNode>();
    private final List<Collision> mDetectedCollisions = new ArrayList<Collision>();

    private final TreeNode mRoot;

    public QuadTree() {
        mRoot = new TreeNode(this);
        // We add them to the pool now
        mTreeNodePool.clear();
        for (int i = 0; i < MAX_TREE_NODE; i++) {
            mTreeNodePool.add(new TreeNode(this));
        }
    }

    public void init(GameEngine gameEngine) {
        mRoot.setArea(new Rect(0, 0, gameEngine.mScreenWidth, gameEngine.mScreenHeight));
    }

    public void checkCollision(GameEngine gameEngine) {
        // Clear the collisions from the previous cycle
        while (!mDetectedCollisions.isEmpty()) {
            Collision.returnToPool(mDetectedCollisions.remove(0));
        }
        mRoot.checkCollision(gameEngine, mDetectedCollisions);
    }

    public void addCollisionObject(Sprite objectToAdd) {
        mRoot.mCollisionObjects.add(objectToAdd);
    }

    public void removeCollisionObject(Sprite objectToRemove) {
        mRoot.mCollisionObjects.remove(objectToRemove);
    }

    public TreeNode getNode() {
        return mTreeNodePool.remove(0);
    }

    public void returnToPool(TreeNode treeNode) {
        mTreeNodePool.add(treeNode);
    }

    public boolean isEnoughNode() {
        return mTreeNodePool.size() >= 4;
    }

}
