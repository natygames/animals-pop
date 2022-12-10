package com.nativegame.animalspop.game;

import com.nativegame.nattyengine.event.GameEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public enum MyGameEvent implements GameEvent {

    // Player input event
    SHOOT_BUBBLE,
    SWITCH_BUBBLE,

    // Bubble event
    BUBBLE_SHOT,
    BUBBLE_HIT,
    BUBBLE_POP,
    BUBBLE_CONSUMED,
    COLLECT_ITEM,

    // Booster event
    BOOSTER_ADDED,
    BOOSTER_REMOVED,
    BOOSTER_SHOT,
    BOOSTER_CONSUMED,

    // Game lifecycle event
    GAME_WIN,
    GAME_OVER,
    EMIT_CONFETTI,
    SHOW_WIN_DIALOG,
    ADD_EXTRA_MOVE

}
