package com.nativegame.animalspop.game.counter.combo;

import com.nativegame.animalspop.R;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public enum Combo {
    WOW,
    GOOD,
    WONDERFUL;

    public int getDrawableId() {
        switch (this) {
            case WOW:
                return R.drawable.text_wow;
            case GOOD:
                return R.drawable.text_good;
            case WONDERFUL:
                return R.drawable.text_wonderful;
        }
        return 0;
    }

}
