package com.nativegame.animalspop.sound;

import com.nativegame.nattyengine.sound.SoundEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public enum MySoundEvent implements SoundEvent {
    // Bubble sound
    BUBBLE_POP,
    BUBBLE_HIT,
    BUBBLE_SHOOT,
    BUBBLE_SWITCH,
    COLLECT_ITEM,
    WOOD_EXPLODE,

    // Booster sound
    ADD_BOOSTER,
    FIRE_BUBBLE_SHOOT,
    BOMB_EXPLODE,

    // Game lifecycle sound
    START_GAME,
    COMBO,
    PLAYER_WIN,
    PLAYER_LOSS,
    GAME_COMPLETE,
    CALCULATE_SCORE,
    ADD_STAR,

    // UI sound
    SWEEP_IN,
    SWEEP_OUT,
    BUTTON_CLICK,
    WHEEL_SPIN
}
