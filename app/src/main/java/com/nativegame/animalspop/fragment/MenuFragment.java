package com.nativegame.animalspop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nativegame.animalspop.MyTransition;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.dialog.ExitDialog;
import com.nativegame.animalspop.dialog.SettingDialog;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.ui.GameFragment;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class MenuFragment extends GameFragment implements View.OnClickListener,
        MyTransition.TransitionListener {

    private MyTransition mTransition;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTransition = new MyTransition(getGameActivity());
        mTransition.setListener(this);
        init();
    }

    private void init() {
        // Init logo
        ImageView imageLogoBg = (ImageView) getView().findViewById(R.id.image_logo_bg);
        ImageView imageLogo = (ImageView) getView().findViewById(R.id.image_logo);
        imageLogo.setScaleX(0);
        imageLogo.setScaleY(0);
        imageLogo.animate()
                .setStartDelay(300)
                .setDuration(1000)
                .scaleX(1)
                .scaleY(1)
                .setInterpolator(new OvershootInterpolator());

        // Init button
        ImageButton btnSetting = (ImageButton) getView().findViewById(R.id.btn_setting);
        ImageButton btnStart = (ImageButton) getView().findViewById(R.id.btn_start);
        btnSetting.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        Utils.createButtonEffect(btnSetting);
        Utils.createButtonEffect(btnStart);

        // Init animation
        Animation bgAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.light_rotate);
        imageLogoBg.startAnimation(bgAnimation);
        Animation logoAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.logo_pulse);
        imageLogo.startAnimation(logoAnimation);
        Animation pulseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.button_pulse);
        btnStart.startAnimation(pulseAnimation);
        Animation playerAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.player_pulse);
        getView().findViewById(R.id.image_fox).startAnimation(playerAnimation);

        // Init pop up
        Utils.createPopUpEffect(imageLogoBg, 1);
        Utils.createPopUpEffect(btnStart, 2);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_setting) {
            getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            SettingDialog settingDialog = new SettingDialog(getGameActivity());
            showDialog(settingDialog);
        } else if (id == R.id.btn_start) {
            getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            mTransition.show();
        }
    }

    @Override
    public void onTransition() {
        getGameActivity().navigateToFragment(new MapFragment());
    }

    @Override
    public boolean onBackPressed() {
        if (!super.onBackPressed()) {
            ExitDialog quitDialog = new ExitDialog(getGameActivity()) {
                @Override
                public void exit() {
                    getGameActivity().finish();
                }
            };
            showDialog(quitDialog);
            return true;
        }
        return super.onBackPressed();
    }

}
