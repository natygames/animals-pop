package com.nativegame.animalspop.sound;

import com.nativegame.engine.sound.SoundEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public enum MySoundEvent implements SoundEvent {
    BUBBLE_POP,
    BUBBLE_HIT,
    BUBBLE_SHOOT,
    BUBBLE_SWITCH,
    COLLECT_ITEM,
    WOOD_EXPLODE,

    ADD_BOOSTER,
    FIRE_BUBBLE_SHOOT,
    BOMB_BUBBLE_EXPLODE,

    START_GAME,
    COMBO,
    PLAYER_WIN,
    PLAYER_LOSS,
    GAME_COMPLETE,

    SWEEP_01,
    SWEEP_02,
    CALCULATE_SCORE,
    ADD_STAR,

    BUTTON_CLICK,
    WHEEL_SPIN
}
