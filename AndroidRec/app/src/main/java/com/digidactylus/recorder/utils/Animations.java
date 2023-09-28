package com.digidactylus.recorder.utils;

import android.animation.ObjectAnimator;
import android.widget.ImageView;

public class Animations {

    public static void GaugeNeedleAnimation(ImageView needle, float endRotation){
        float startRotation = -220f;
        ObjectAnimator animator = ObjectAnimator.ofFloat(needle, "rotation", startRotation, endRotation);
        animator.setDuration(1000);
        animator.start();
    }
}
