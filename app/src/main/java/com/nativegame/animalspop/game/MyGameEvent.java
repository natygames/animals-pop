package com.nativegame.animalspop.game;

import com.nativegame.engine.GameEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public enum MyGameEvent implements GameEvent {
    SHOOT_BUBBLE,
    SWITCH_BUBBLE,
    COLLECT_ITEM,
    EMIT_CONFETTI,

    BUBBLE_SHOT,
    BUBBLE_HIT,
    BUBBLE_POP,
    BUBBLE_CONSUMED,

    BOOSTER_ADDED,
    BOOSTER_REMOVED,
    BOOSTER_SHOT,
    BOOSTER_CONSUMED,

    GAME_WIN,
    GAME_OVER,
    REMOVE_PLAYER,
    SHOW_WIN_DIALOG,
    ADD_EXTRA_MOVE
}
