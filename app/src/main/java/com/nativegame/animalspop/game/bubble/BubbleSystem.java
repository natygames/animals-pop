package com.nativegame.animalspop.game.bubble;

import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.game.player.PlayerBubble;
import com.nativegame.animalspop.game.player.BasicBubble;
import com.nativegame.animalspop.game.bubble.type.DummyBubble;
import com.nativegame.animalspop.game.bubble.type.ItemBubble;
import com.nativegame.animalspop.game.bubble.type.LargeItemBubble;
import com.nativegame.animalspop.game.bubble.type.LargeObstacleBubble;
import com.nativegame.animalspop.game.bubble.type.LockedBubble;
import com.nativegame.animalspop.game.bubble.type.ObstacleBubble;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.engine.GameEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BubbleSystem {

    private static final int ROW_ON_SCREEN = 11;
    private static final int TOTAL_COLUMN = 11;
    private static final int BUBBLE_LAYER = 2;

    private final GameEngine mGameEngine;
    private final float mBubbleWidth;
    private final float mIntervalX, mIntervalY;
    private final ArrayList<ArrayList<Bubble>> mBubbleList = new ArrayList<>();
    private final ArrayList<Bubble> mDeleteList = new ArrayList<>();

    private int mTotalRow;
    private int mRoot;

    public BubbleSystem(GameEngine gameEngine) {
        mGameEngine = gameEngine;
        mBubbleWidth = gameEngine.mPixelFactor * Utils.BUBBLE_WIDTH;
        mIntervalX = mBubbleWidth;
        mIntervalY = mBubbleWidth * 0.85f;

        MyLevel level = (MyLevel) gameEngine.mLevel;
        String bubbles = level.mBubble.replaceAll("\\s+", "");
        mTotalRow = bubbles.length() / TOTAL_COLUMN;
        mRoot = 0;
        initBubble(convertToArray(bubbles.toCharArray()));
    }

    private char[][] convertToArray(char[] board) {
        char[][] charArray = new char[mTotalRow][TOTAL_COLUMN];
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                charArray[i][j] = board[j + i * TOTAL_COLUMN];
            }
        }
        return charArray;
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

    private void initBubble(char[][] charArray) {
        for (int i = 0; i < mTotalRow; i++) {
            mBubbleList.add(new ArrayList<>(TOTAL_COLUMN));
        }
        // Add bubble to list
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                // Init new bubble
                Bubble bubble;
                if (charArray[i][j] == 'x') {
                    bubble = new ItemBubble(mGameEngine, i, j);
                } else if (charArray[i][j] == 'X') {
                    bubble = new LargeItemBubble(mGameEngine, i, j);
                } else if (charArray[i][j] == 'o') {
                    bubble = new ObstacleBubble(mGameEngine, i, j);
                } else if (charArray[i][j] == 'O') {
                    bubble = new LargeObstacleBubble(mGameEngine, i, j);
                } else if (charArray[i][j] == 'B') {
                    bubble = new LockedBubble(mGameEngine, i, j, BubbleColor.BLUE);
                } else if (charArray[i][j] == 'R') {
                    bubble = new LockedBubble(mGameEngine, i, j, BubbleColor.RED);
                } else if (charArray[i][j] == 'Y') {
                    bubble = new LockedBubble(mGameEngine, i, j, BubbleColor.YELLOW);
                } else if (charArray[i][j] == 'G') {
                    bubble = new LockedBubble(mGameEngine, i, j, BubbleColor.GREEN);
                } else if (charArray[i][j] == '+') {
                    bubble = new DummyBubble(mGameEngine, i, j);
                } else {
                    bubble = new Bubble(mGameEngine, i, j, getBubbleColor(charArray[i][j]));
                }

                // Add to list and engine
                mBubbleList.get(i).add(bubble);
                bubble.addToGameEngine(mGameEngine, BUBBLE_LAYER);
            }
        }

        // Add adjacent bubble to edges
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble bubble = mBubbleList.get(i).get(j);

                if (i > 0) {
                    bubble.mEdges.add(mBubbleList.get(i - 1).get(j));   // Up
                }
                if (i < mTotalRow - 1) {
                    bubble.mEdges.add(mBubbleList.get(i + 1).get(j));   // Down
                }
                if (j > 0) {
                    bubble.mEdges.add(mBubbleList.get(i).get(j - 1));   // Left
                }
                if (j < TOTAL_COLUMN - 1) {
                    bubble.mEdges.add(mBubbleList.get(i).get(j + 1));   // Right
                }

                if ((i % 2) == 0) {
                    if (i > 0 && j > 0) {
                        bubble.mEdges.add(mBubbleList.get(i - 1).get(j - 1));   // Top left
                    }
                    if (i < mTotalRow - 1 && j > 0) {
                        bubble.mEdges.add(mBubbleList.get(i + 1).get(j - 1));   // Bottom left
                    }
                } else {
                    if (i > 0 && j < TOTAL_COLUMN - 1) {
                        bubble.mEdges.add(mBubbleList.get(i - 1).get(j + 1));   // Top right
                    }
                    if (i < mTotalRow - 1 && j < TOTAL_COLUMN - 1) {
                        bubble.mEdges.add(mBubbleList.get(i + 1).get(j + 1));   // Bottom right
                    }
                }

                // Add dummy bubble to list
                if (bubble instanceof LargeObstacleBubble) {
                    LargeObstacleBubble largeObstacleBubble = (LargeObstacleBubble) bubble;
                    largeObstacleBubble.addDummyBubble((DummyBubble) mBubbleList.get(i).get(j - 1));   // Left
                    largeObstacleBubble.addDummyBubble((DummyBubble) mBubbleList.get(i).get(j + 1));   // Right
                }
            }
        }
    }

    private void addOneRow() {
        ArrayList<Bubble> arrayList = new ArrayList<>(TOTAL_COLUMN);
        for (int j = 0; j < TOTAL_COLUMN; j++) {
            Bubble bubble = new Bubble(mGameEngine, mTotalRow, j, BubbleColor.BLANK);
            bubble.mX = j * mIntervalX;
            bubble.mY = Math.min(mTotalRow, ROW_ON_SCREEN) * mIntervalY;

            // We adjust x interval at odd row
            if ((mTotalRow % 2) != 0) {
                bubble.mX += mIntervalX / 2f;
            }
            arrayList.add(bubble);
            bubble.addToGameEngine(mGameEngine, BUBBLE_LAYER);
        }
        mBubbleList.add(arrayList);

        // Add adjacent bubble to edges of new row
        for (int j = 0; j < TOTAL_COLUMN; j++) {
            int row = mTotalRow;
            Bubble bubble = mBubbleList.get(row).get(j);

            bubble.mEdges.add(mBubbleList.get(row - 1).get(j));   // Up
            if (j > 0) {
                bubble.mEdges.add(mBubbleList.get(row).get(j - 1));   // Left
            }
            if (j < TOTAL_COLUMN - 1) {
                bubble.mEdges.add(mBubbleList.get(row).get(j + 1));   // Right
            }
            if ((row % 2) == 0) {
                if (j > 0) {
                    bubble.mEdges.add(mBubbleList.get(row - 1).get(j - 1));   // Top left
                }
            } else {
                if (j < TOTAL_COLUMN - 1) {
                    bubble.mEdges.add(mBubbleList.get(row - 1).get(j + 1));   // Top right
                }
            }
        }

        // Add adjacent bubble to edges of last row
        for (int j = 0; j < TOTAL_COLUMN; j++) {
            int row = mTotalRow - 1;
            Bubble bubble = mBubbleList.get(row).get(j);

            bubble.mEdges.add(mBubbleList.get(row + 1).get(j));   // Down
            if ((row % 2) == 0) {
                if (j > 0) {
                    bubble.mEdges.add(mBubbleList.get(row + 1).get(j - 1));   // Bottom left
                }
            } else {
                if (j < TOTAL_COLUMN - 1) {
                    bubble.mEdges.add(mBubbleList.get(row + 1).get(j + 1));   // Bottom right
                }
            }
        }

        // Update total row
        mTotalRow++;
    }

    public Bubble getBubble(PlayerBubble playerBubble, Bubble bubble) {
        int row = bubble.mRow;
        int col = bubble.mCol;
        Bubble newBubble;

        if (playerBubble.mY > bubble.mY + mBubbleWidth / 2) {   // Player collide bubble at bottom

            // Check we have enough row
            if (row == mTotalRow - 1) {
                addOneRow();
            }

            if (playerBubble.mX >= bubble.mX) {   // Player collide bubble at right bottom
                // Check odd or even row
                if (row % 2 == 0) {
                    newBubble = mBubbleList.get(row + 1).get(col);
                } else {
                    newBubble = mBubbleList.get(row + 1).get(col + 1);
                }
            } else {   // Player collide bubble at left bottom
                // Check odd or even row
                if (row % 2 == 0) {
                    newBubble = mBubbleList.get(row + 1).get(col - 1);
                } else {
                    newBubble = mBubbleList.get(row + 1).get(col);
                }
            }

        } else {   // Player collide bubble at side
            if (playerBubble.mX >= bubble.mX) {   // Player collide bubble at right side
                newBubble = mBubbleList.get(row).get(col + 1);
            } else {   // Player collide bubble at left side
                newBubble = mBubbleList.get(row).get(col - 1);
            }
        }

        return newBubble;
    }

    public void addBubble(BasicBubble basicBubble, Bubble bubble) {
        // We get the new added bubble
        Bubble newBubble = getBubble(basicBubble, bubble);
        // We set new bubble color
        newBubble.setBubbleColor(basicBubble.mBubbleColor);
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
        int size = mDeleteList.size();
        for (Bubble b : mDeleteList) {
            // We set it to blank if 3 bubble match
            if (size >= 3) {
                b.popBubble(mGameEngine);
            } else {
                // Reset depth and order
                b.mDepth = -1;
                b.mOrder = -1;
            }
        }
        mDeleteList.clear();

        // Check is any obstacle or locked bubble left
        bubble.checkLockedBubble(mGameEngine);
        bubble.checkObstacleBubble(mGameEngine);
    }

    private void bfs(Bubble root, BubbleColor color) {
        Queue<Bubble> queue = new LinkedList<>();
        root.mDepth = 0;
        queue.offer(root);

        int order = 0;
        while (!queue.isEmpty()) {
            Bubble currentBubble = queue.poll();
            currentBubble.mOrder = order;
            order++;
            mDeleteList.add(currentBubble);
            for (Bubble b : currentBubble.mEdges) {
                // Unvisited bubble
                if (b.mDepth == -1 && b.mBubbleColor == color) {
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
                    bubble.popFloater(mGameEngine);
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
            if (!b.mDiscover && b.mBubbleColor != BubbleColor.BLANK) {
                dfs(b);
            }
        }
    }

    public void shiftBubble() {
        // Maxim row on screen
        int maxRow = 0;
        outer:
        for (int i = mTotalRow - 1; i >= 0; i--) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                if (mBubbleList.get(i).get(j).mBubbleColor != BubbleColor.BLANK) {
                    maxRow = i;
                    break outer;
                }
            }
        }

        // Total row on screen
        int rowOnScreen = maxRow - (mRoot - 1);

        // Row added to screen
        int rowAdd = ROW_ON_SCREEN - rowOnScreen;
        if (rowAdd > mRoot) {   // Check is enough row to add
            rowAdd = mRoot;
        }
        mRoot -= rowAdd;

        // Move the bubble
        shift(rowAdd * mIntervalY);
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
                    bubble.popBubble(mGameEngine);
                }
            }
        }
    }

    public void addBubbleHint(BubbleColor color) {
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble bubble = mBubbleList.get(i).get(j);
                if (bubble.mBubbleColor == color) {
                    bubble.addHint(mGameEngine);
                }
            }
        }
    }

    public void removeBubbleHint(BubbleColor color) {
        for (int i = 0; i < mTotalRow; i++) {
            for (int j = 0; j < TOTAL_COLUMN; j++) {
                Bubble bubble = mBubbleList.get(i).get(j);
                if (bubble.mBubbleColor == color) {
                    bubble.removeHint(mGameEngine);
                }
            }
        }
    }

}
