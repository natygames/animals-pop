package com.nativegame.animalspop.level;

import com.nativegame.animalspop.R;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public enum LevelType {
    POP_BUBBLE,
    COLLECT_ITEM;

    public int getImageResId() {
        switch (this) {
            case POP_BUBBLE:
                return R.drawable.target_pop;
            case COLLECT_ITEM:
                return R.drawable.target_collect;
        }
        return 0;
    }

    public int getPlayerResId() {
        switch (this) {
            case POP_BUBBLE:
                return R.drawable.fox_target_pop;
            case COLLECT_ITEM:
                return R.drawable.fox_target_collect;
        }
        return 0;
    }

    public int getStringResId() {
        switch (this) {
            case POP_BUBBLE:
                return R.string.txt_level_type_pop;
            case COLLECT_ITEM:
                return R.string.txt_level_type_collect;
        }
        return 0;
    }

}
