package com.nativegame.engine.collision;

import android.graphics.Rect;

import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private static final int MAX_OBJECTS_TO_CHECK = 8;

    public final List<Sprite> mCollisionObjects = new ArrayList<Sprite>();
    private final List<TreeNode> mChildren = new ArrayList<TreeNode>(4);

    private final QuadTree mParent;
    private final Rect mArea = new Rect();
    private final Rect mChildArea = new Rect();

    public TreeNode(QuadTree quadTree) {
        mParent = quadTree;
    }

    public void setArea(Rect area) {
        mArea.set(area);
    }

    public void checkCollision(GameEngine gameEngine, List<Collision> detectedCollisions) {
        int size = mCollisionObjects.size();
        if (size > MAX_OBJECTS_TO_CHECK && mParent.isEnoughNode()) {
            // Divide the area in 4 part
            divideAndCheck(gameEngine, detectedCollisions);
        } else {
            for (int i = 0; i < size; i++) {
                Sprite objectA = mCollisionObjects.get(i);
                for (int j = i + 1; j < size; j++) {
                    Sprite objectB = mCollisionObjects.get(j);
                    if (objectA.checkCollision(objectB)) {
                        Collision c = Collision.init(objectA, objectB);
                        if (!hasBeenDetected(detectedCollisions, c)) {
                            detectedCollisions.add(c);
                            objectA.onCollision(gameEngine, objectB);
                            objectB.onCollision(gameEngine, objectA);
                        }
                    }
                }
            }
        }
    }

    private void divideAndCheck(GameEngine gameEngine, List<Collision> detectedCollisions) {
        mChildren.clear();
        // Add children
        for (int i = 0; i < 4; i++) {
            mChildren.add(mParent.getNode());
        }
        // Check children collision
        for (int i = 0; i < 4; i++) {
            TreeNode node = mChildren.get(i);
            node.setArea(getArea(i));
            node.checkObjects(mCollisionObjects);
            node.checkCollision(gameEngine, detectedCollisions);
            // Clear and return to the pool
            node.mCollisionObjects.clear();
            mParent.returnToPool(node);
        }
    }

    private void checkObjects(List<Sprite> sprites) {
        mCollisionObjects.clear();
        int size = sprites.size();
        for (int i = 0; i < size; i++) {
            Sprite sprite = sprites.get(i);
            if (Rect.intersects(sprite.mHitBoxRect, mArea)) {
                mCollisionObjects.add(sprite);
            }
        }
    }

    private boolean hasBeenDetected(List<Collision> detectedCollisions, Collision c) {
        int size = detectedCollisions.size();
        for (int i = 0; i < size; i++) {
            if (detectedCollisions.get(i).equals(c)) {
                return true;
            }
        }
        return false;
    }

    private Rect getArea(int area) {
        int startX = mArea.left;
        int startY = mArea.top;
        int width = mArea.width();
        int height = mArea.height();
        switch (area) {
            case 0:
                mChildArea.set(startX, startY, startX + width / 2, startY + height / 2);
                break;
            case 1:
                mChildArea.set(startX + width / 2, startY, startX + width, startY + height / 2);
                break;
            case 2:
                mChildArea.set(startX, startY + height / 2, startX + width / 2, startY + height);
                break;
            case 3:
                mChildArea.set(startX + width / 2, startY + height / 2, startX + width, startY + height);
                break;
        }
        return mChildArea;
    }

}
