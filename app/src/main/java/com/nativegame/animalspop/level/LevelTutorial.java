package com.nativegame.animalspop.level;

import com.nativegame.animalspop.R;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public enum LevelTutorial {

    SHOOT_BUBBLE,
    BOUNCE_BUBBLE,
    SWITCH_BUBBLE,
    COLLECT_ITEMS,
    COLOR_BUBBLE,
    FIRE_BUBBLE,
    BOMB_BUBBLE,
    LOCKED_BUBBLE,
    OBSTACLE_BUBBLE;

    public int getStringId() {
        switch (this) {
            case SHOOT_BUBBLE:
                return R.string.txt_tutorial_shoot;
            case BOUNCE_BUBBLE:
                return R.string.txt_tutorial_bounce;
            case SWITCH_BUBBLE:
                return R.string.txt_tutorial_switch;
            case COLLECT_ITEMS:
                return R.string.txt_tutorial_collect_item;
            case COLOR_BUBBLE:
                return R.string.txt_tutorial_color_ball;
            case FIRE_BUBBLE:
                return R.string.txt_tutorial_fireball;
            case BOMB_BUBBLE:
                return R.string.txt_tutorial_bomb;
            case LOCKED_BUBBLE:
                return R.string.txt_tutorial_locked_bubble;
            case OBSTACLE_BUBBLE:
                return R.string.txt_tutorial_obstacle_bubble;
        }

        return 0;
    }

    public int getDrawableId() {
        switch (this) {
            case SHOOT_BUBBLE:
                return R.drawable.tutorial_shoot;
            case BOUNCE_BUBBLE:
                return R.drawable.tutorial_bounce;
            case SWITCH_BUBBLE:
                return R.drawable.tutorial_switch;
            case COLLECT_ITEMS:
                return R.drawable.tutorial_collect_items;
            case COLOR_BUBBLE:
                return R.drawable.tutorial_color_bubble;
            case FIRE_BUBBLE:
                return R.drawable.tutorial_fire_bubble;
            case BOMB_BUBBLE:
                return R.drawable.tutorial_bomb_bubble;
            case LOCKED_BUBBLE:
                return R.drawable.tutorial_locked_bubble;
            case OBSTACLE_BUBBLE:
                return R.drawable.tutorial_obstacle_bubble;
        }

        return 0;
    }

}
