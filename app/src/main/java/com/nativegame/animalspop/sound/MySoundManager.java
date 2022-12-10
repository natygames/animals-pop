package com.nativegame.animalspop.sound;

import android.content.Context;

import com.nativegame.animalspop.R;
import com.nativegame.nattyengine.sound.SoundManager;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class MySoundManager extends SoundManager {

    public MySoundManager(Context context) {
        super(context);
        loadSound(MySoundEvent.BUBBLE_POP, R.raw.bubble_pop);
        loadSound(MySoundEvent.BUBBLE_SHOOT, R.raw.bubble_shoot);
        loadSound(MySoundEvent.FIRE_BUBBLE_SHOOT, R.raw.fire_bubble_shoot);
        loadSound(MySoundEvent.BOMB_EXPLODE, R.raw.bomb_explode);
        loadSound(MySoundEvent.SWEEP_IN, R.raw.sweep_in);
        loadSound(MySoundEvent.SWEEP_OUT, R.raw.sweep_out);
        loadSound(MySoundEvent.START_GAME, R.raw.start_game);
        loadSound(MySoundEvent.COMBO, R.raw.add_combo);
        loadSound(MySoundEvent.PLAYER_WIN, R.raw.player_win);
        loadSound(MySoundEvent.CALCULATE_SCORE, R.raw.calculate_score);
        loadSound(MySoundEvent.ADD_STAR, R.raw.add_star);
        loadSound(MySoundEvent.BUBBLE_HIT, R.raw.bubble_hit);
        loadSound(MySoundEvent.BUBBLE_SWITCH, R.raw.bubble_switch);
        loadSound(MySoundEvent.ADD_BOOSTER, R.raw.add_booster);
        loadSound(MySoundEvent.GAME_COMPLETE, R.raw.game_complete);
        loadSound(MySoundEvent.COLLECT_ITEM, R.raw.collect_item);
        loadSound(MySoundEvent.WOOD_EXPLODE, R.raw.wood_explode);
        loadSound(MySoundEvent.PLAYER_LOSS, R.raw.player_loss);
        loadSound(MySoundEvent.BUTTON_CLICK, R.raw.button_click);
        loadSound(MySoundEvent.WHEEL_SPIN, R.raw.wheel_spin);
    }

}
