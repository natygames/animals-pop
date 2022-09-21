package com.nativegame.animalspop.prize;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Prize {

    private final String mName;
    private final int mDrawableResId;
    private final int mNum;

    public Prize(String name, int drawableResId, int num) {
        mName = name;
        mDrawableResId = drawableResId;
        mNum = num;
    }

    public String getName() {
        return mName;
    }

    public int getDrawableResId() {
        return mDrawableResId;
    }

    public int getNum() {
        return mNum;
    }

}
