package com.nativegame.animalspop.level;

import com.nativegame.animalspop.R;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public enum LevelType {
    POP_BUBBLE,
    COLLECT_ITEM;

    public int getTargetDrawableId() {
        switch (this) {
            case POP_BUBBLE:
                return R.drawable.target_pop;
            case COLLECT_ITEM:
                return R.drawable.target_collect;
        }

        return 0;
    }

    public int getAnimalDrawableId() {
        switch (this) {
            case POP_BUBBLE:
                return R.drawable.fox_target_pop;
            case COLLECT_ITEM:
                return R.drawable.fox_target_collect;
        }

        return 0;
    }

    public int getTargetStringId() {
        switch (this) {
            case POP_BUBBLE:
                return R.string.txt_level_type_pop;
            case COLLECT_ITEM:
                return R.string.txt_level_type_collect;
        }

        return 0;
    }

}
