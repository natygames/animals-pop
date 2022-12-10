package com.nativegame.animalspop.game.bubble;

import com.nativegame.animalspop.game.bubble.type.DummyBubble;
import com.nativegame.animalspop.game.bubble.type.ItemBubble;
import com.nativegame.animalspop.game.bubble.type.LargeItemBubble;
import com.nativegame.animalspop.game.bubble.type.LargeObstacleBubble;
import com.nativegame.animalspop.game.bubble.type.LockedBubble;
import com.nativegame.animalspop.game.bubble.type.ObstacleBubble;
import com.nativegame.animalspop.game.player.PlayerBubble;
import com.nativegame.animalspop.game.player.BasicBubble;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.nattyengine.Game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BubbleSystem {

    private static final int ROW_ON_SCREEN = 11;
    private static final int TOTAL_COLUMN = 11;

    public static final int GRID_WIDTH = 300;
    public static final int GRID_HEIGHT = 258;   // 300 * 0.86

    private final List<List<Bubble>> mBubbleList = new ArrayList<>();
    private final List<Bubble> mBubblePool = new ArrayList<>();
    private final List<Bubble> mRemovedList = new ArrayList<>();

    private final Game mGame;
    private final float gridWidth;
    private final float gridHeight;

    private int mTotalRow;
    private int mRoot = 0;

    public BubbleSystem(Game game) {
        mGame = game;
        gridWidth = GRID_WIDTH * game.getPixelFactor();
        gridHeight = GRID_HEIGHT * game.getPixelFactor();
        // Init bubble list
        MyLevel level = (MyLevel) game.getLevel();
        String bubbles = level.mBubble.replaceAll("\\s+", "") + "00000000000";
        mTotalRow = bubbles.length() / TOTAL_COLUMN;
        initBubble(get2DArray(bubbles.toCharArray(), mTotalRow, TOTAL_COLUMN));
        // We add them to the pool now
        for (int i = 0; i < 11; i++) {
            mBubblePool.add(new Bubble(mGame, BubbleColor.BLANK));
        }
    }

    public char[][] get2DArray(char[] array, int row, int col) {
        char[][] temp = new char[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                temp[i][j] = array[j + i * col];
            }
        }
        return temp;
    }

    private void initBubble(char[][] charArray) {
        // Add row to list
        for (int i = 0; i < mTotalRow; i++) {
            mBubbleList.add(new ArrayList<>(TOTAL_COLUMN));
        }

        // Init the bubbles
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble b = getBubbleType(charArray[i][j]);
                float x = j * gridWidth + ((i % 2) != 0 ? gridWidth / 2f : 0);
                float y = i * gridHeight;
                b.setPosition(x, y, i, j);
                mBubbleList.get(i).add(b);
            }
        }

        // Add adjacent bubbles to edges
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble b = mBubbleList.get(i).get(j);
                addEdges(b);
            }
        }

        // Add the bubbles to game after init the adjacent bubbles
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble b = mBubbleList.get(i).get(j);
                b.addToGame();
            }
        }
    }

    public Bubble getBubbleType(char type) {
        switch (type) {
            case 'x':
                return new ItemBubble(mGame);
            case 'X':
                return new LargeItemBubble(mGame);
            case 'o':
                return new ObstacleBubble(mGame);
            case 'O':
                return new LargeObstacleBubble(mGame);
            case 'B':
                return new LockedBubble(mGame, BubbleColor.BLUE);
            case 'R':
                return new LockedBubble(mGame, BubbleColor.RED);
            case 'Y':
                return new LockedBubble(mGame, BubbleColor.YELLOW);
            case 'G':
                return new LockedBubble(mGame, BubbleColor.GREEN);
            case '+':
                return new DummyBubble(mGame);
            default:
                return new Bubble(mGame, getBubbleColor(type));
        }
    }

    private BubbleColor getBubbleColor(char color) {
        switch (color) {
            case 'b':
                return BubbleColor.BLUE;
            case 'r':
                return BubbleColor.RED;
            case 'y':
                return BubbleColor.YELLOW;
            case 'g':
                return BubbleColor.GREEN;
            case '0':
                return BubbleColor.BLANK;
        }
        return BubbleColor.BLANK;
    }

    private void addEdges(Bubble bubble) {
        int row = bubble.mRow;
        int col = bubble.mCol;
        // Check the row is odd or even
        if ((row % 2) == 0) {
            if (row > 0 && col > 0) {
                bubble.mEdges[0] = (mBubbleList.get(row - 1).get(col - 1));   // Top left
            }
            if (row > 0) {
                bubble.mEdges[1] = (mBubbleList.get(row - 1).get(col));   // Top Right
            }
            if (row < mTotalRow - 1 && col > 0) {
                bubble.mEdges[4] = (mBubbleList.get(row + 1).get(col - 1));   // Bottom left
            }
            if (row < mTotalRow - 1) {
                bubble.mEdges[5] = (mBubbleList.get(row + 1).get(col));   // Bottom right
            }
        } else {
            if (row > 0) {
                bubble.mEdges[0] = (mBubbleList.get(row - 1).get(col));   // Top left
            }
            if (row > 0 && col < TOTAL_COLUMN - 1) {
                bubble.mEdges[1] = (mBubbleList.get(row - 1).get(col + 1));   // Top right
            }
            if (row < mTotalRow - 1) {
                bubble.mEdges[4] = (mBubbleList.get(row + 1).get(col));   // Bottom left
            }
            if (row < mTotalRow - 1 && col < TOTAL_COLUMN - 1) {
                bubble.mEdges[5] = (mBubbleList.get(row + 1).get(col + 1));   // Bottom right
            }
        }

        if (col > 0) {
            bubble.mEdges[2] = (mBubbleList.get(row).get(col - 1));   // Left
        }
        if (col < TOTAL_COLUMN - 1) {
            bubble.mEdges[3] = (mBubbleList.get(row).get(col + 1));   // Right
        }
    }

    private void addRow() {
        List<Bubble> list = new ArrayList<>(TOTAL_COLUMN);
        for (int i = 0; i < TOTAL_COLUMN; i++) {
            Bubble b = getFromPool();
            float x = i * gridWidth + ((mTotalRow % 2) != 0 ? gridWidth / 2f : 0);
            float y = Math.min(mTotalRow, ROW_ON_SCREEN + 1) * gridHeight;
            b.setPosition(x, y, mTotalRow, i);
            b.addToGame();
            list.add(b);
        }

        // Update total row
        mBubbleList.add(list);
        mTotalRow++;

        // Add adjacent bubble to edges of new row
        for (int i = 0; i < TOTAL_COLUMN; i++) {
            Bubble b = mBubbleList.get(mTotalRow - 1).get(i);
            addEdges(b);
        }

        // Add adjacent bubble to edges of last row
        for (int i = 0; i < TOTAL_COLUMN; i++) {
            Bubble b = mBubbleList.get(mTotalRow - 2).get(i);
            addEdges(b);
        }
    }

    private void reduceRow() {
        int maxRow = getMaxRow() + 1;
        while (mTotalRow > maxRow) {
            // Update last row edges
            for (int i = 0; i < TOTAL_COLUMN; i++) {
                Bubble b = mBubbleList.get(mTotalRow - 2).get(i);
                b.mEdges[4] = null;
                b.mEdges[5] = null;
            }
            // Remove and return to pool
            for (int i = 0; i < TOTAL_COLUMN; i++) {
                Bubble b = mBubbleList.get(mTotalRow - 1).get(i);
                b.removeFromGame();
                returnToPool(b);
            }
            // Remove last row
            mBubbleList.remove(mTotalRow - 1);
            mTotalRow--;
        }
    }

    private Bubble getFromPool() {
        // Check we have have bubble in pool
        if (!mBubblePool.isEmpty()) {
            return mBubblePool.remove(0);
        } else {
            return new Bubble(mGame, BubbleColor.BLANK);
        }
    }

    private void returnToPool(Bubble bubble) {
        // We make sure exclude the subclass
        if (bubble.getClass() == Bubble.class) {
            // Return a blank bubble to pool
            bubble.setBubbleColor(BubbleColor.BLANK);
            mBubblePool.add(bubble);
        }
    }

    public Bubble getCollidedBubble(PlayerBubble playerBubble, Bubble bubble) {
        Bubble newBubble = bubble.getCollidedBubble(playerBubble);
        // Check we have enough row
        if (newBubble.mRow == mTotalRow - 1) {
            addRow();
        }

        return newBubble;
    }

    public void addCollidedBubble(BasicBubble basicBubble, Bubble bubble) {
        // We get the new added bubble
        Bubble newBubble = getCollidedBubble(basicBubble, bubble);
        // We set new bubble color
        newBubble.setBubbleColor(basicBubble.mBubbleColor);
        // Reduce row if need
        reduceRow();
        // Remove same color bubble
        popBubble(newBubble);
        // Remove floater
        popFloater();
        // Shift bubble if need
        shiftBubble();
    }

    private void popBubble(Bubble bubble) {
        // Search same color bubble with bfs
        bfs(bubble, bubble.mBubbleColor);

        // Update bubble after bfs
        int size = mRemovedList.size();
        for (Bubble b : mRemovedList) {
            // We set it to blank if 3 bubble match
            if (size >= 3) {
                b.popBubble();
            } else {
                // Reset depth and order
                b.mDepth = -1;
            }
        }
        mRemovedList.clear();

        // Check is any obstacle or locked bubble
        bubble.checkLockedBubble();
        bubble.checkObstacleBubble();
    }

    private void bfs(Bubble root, BubbleColor color) {
        Queue<Bubble> queue = new LinkedList<>();
        root.mDepth = 0;
        queue.offer(root);

        while (!queue.isEmpty()) {
            Bubble currentBubble = queue.poll();
            mRemovedList.add(currentBubble);
            for (Bubble b : currentBubble.mEdges) {
                // Unvisited bubble
                if (b != null
                        && b.mDepth == -1
                        && b.mBubbleColor == color) {
                    b.mDepth = currentBubble.mDepth + 1;
                    queue.offer(b);
                }
            }
        }
    }

    public void popFloater() {
        // We start dfs from root
        for (int i = 0; i < TOTAL_COLUMN; i++) {
            Bubble bubble = mBubbleList.get(0).get(i);   // Bubble at row 0
            if (bubble.mBubbleColor != BubbleColor.BLANK) {
                // Search from root bubble with dfs
                dfs(bubble);
            }
        }

        // Update bubble after dfs
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble bubble = mBubbleList.get(i).get(j);
                // We remove floater if bubble undiscovered
                if (!bubble.mDiscover && bubble.mBubbleColor != BubbleColor.BLANK) {
                    bubble.popFloater();
                } else {
                    // Reset discover
                    bubble.mDiscover = false;
                }
            }
        }
    }

    private void dfs(Bubble bubble) {
        bubble.mDiscover = true;
        for (Bubble b : bubble.mEdges) {
            if (b != null
                    && !b.mDiscover
                    && b.mBubbleColor != BubbleColor.BLANK) {
                dfs(b);
            }
        }
    }

    public void shiftBubble() {
        // Row added to screen
        int rowAdd = ROW_ON_SCREEN - getRowOnScreen();
        if (rowAdd > mRoot) {   // Check is enough row to add
            rowAdd = mRoot;
        }
        mRoot -= rowAdd;

        // Move the bubble
        float shiftDistance = rowAdd * gridHeight;
        shift(shiftDistance);
    }

    private int getMaxRow() {
        // Maxim row on screen
        for (int i = mTotalRow - 1; i >= 0; i--) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                BubbleColor c = mBubbleList.get(i).get(j).mBubbleColor;
                if (c != BubbleColor.BLANK) {
                    return i + 1;
                }
            }
        }

        return 0;
    }

    private int getRowOnScreen() {
        return getMaxRow() - mRoot;
    }

    private void shift(float shiftDistance) {
        if (shiftDistance == 0) {
            return;
        }
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                mBubbleList.get(i).get(j).shiftBubble(shiftDistance);
            }
        }
    }

    public boolean isShifting() {
        // We only check one bubble shifting
        if (mBubbleList.get(0).get(0).isShifting()) {
            return true;
        } else {
            return false;
        }
    }

    public void clearBubble() {
        // Clear the remaining bubble in level order
        int depth = 0;
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble bubble = mBubbleList.get(i).get(j);
                if (bubble.mBubbleColor != BubbleColor.BLANK) {
                    bubble.mDepth = depth++;
                    bubble.popBubble();
                }
            }
        }
    }

    public void addBubbleHint(BubbleColor color) {
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble bubble = mBubbleList.get(i).get(j);
                if (bubble.mBubbleColor == color) {
                    bubble.addHint();
                }
            }
        }
    }

    public void removeBubbleHint(BubbleColor color) {
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble bubble = mBubbleList.get(i).get(j);
                if (bubble.mBubbleColor == color) {
                    bubble.removeHint();
                }
            }
        }
    }

}
