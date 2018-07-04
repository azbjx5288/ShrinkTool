package com.shrinktool.component;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 将制定View展开或收起
 * Created by Alashi on 2016/12/21.
 */

public class ExpandableViewHelper {
    private static final String TAG = "ExpandableViewHelper";

    private View expandableView;

    public ExpandableViewHelper(View expandableView) {
        this.expandableView = expandableView;
    }

    public View getExpandableView() {
        return expandableView;
    }

    public int getExpandHeight() {
        int widthMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        int heightMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        expandableView.measure(widthMS, heightMS);
        return expandableView.getMeasuredHeight();
    }

    public int startOpenAnimation(Animation.AnimationListener animationListener) {
        int widthMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        int heightMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        expandableView.measure(widthMS, heightMS);
        int expandHeight = expandableView.getMeasuredHeight();
        Log.i(TAG, "startOpenAnimation: " + expandHeight);
        expandableView.clearAnimation();
        OpenAnimation openAnimation = new OpenAnimation(expandableView, expandHeight);
        openAnimation.setAnimationListener(animationListener);
        expandableView.startAnimation(openAnimation);
        return expandHeight;
    }

    public int startOpenAnimation() {
        int widthMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        int heightMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        expandableView.measure(widthMS, heightMS);
        int expandHeight = expandableView.getMeasuredHeight();
        Log.i(TAG, "startOpenAnimation: " + expandHeight);
        expandableView.clearAnimation();
        expandableView.startAnimation(new OpenAnimation(expandableView, expandHeight));
        return expandHeight;
    }

    public void startCloseAnimation(Animation.AnimationListener animationListener){
        expandableView.clearAnimation();
        CloseAnimation animation = new CloseAnimation(expandableView, expandableView.getHeight());
        animation.setAnimationListener(animationListener);
        expandableView.startAnimation(animation);
    }

    public void startCloseAnimation(){
        Log.i(TAG, "startCloseAnimation: ");
        expandableView.clearAnimation();
        expandableView.startAnimation(new CloseAnimation(expandableView, expandableView.getHeight()));
    }

    private class CloseAnimation extends Animation {
        private int startHigh;
        private View expand;
        ViewGroup.LayoutParams params;

        CloseAnimation(View expand, int startHigh) {
            setDuration(300);
            this.startHigh = startHigh;
            this.expand = expand;
            params = expand.getLayoutParams();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            params.height = (int) (startHigh * (1-interpolatedTime));
            Log.d(TAG, "applyTransformation: CloseAnimation params.height=" + params.height);
            expand.setLayoutParams(params);
            expand.invalidate();
        }
    }

    private class OpenAnimation extends Animation {
        private int endHigh;
        private View expand;
        ViewGroup.LayoutParams params;

        OpenAnimation(View expand, int endHigh) {
            setDuration(300);
            this.endHigh = endHigh;
            this.expand = expand;
            params = expand.getLayoutParams();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            params.height = (int) (endHigh * interpolatedTime);
            Log.i(TAG, "applyTransformation: OpenAnimation params.height = " + params.height);
            expand.setLayoutParams(params);
            expand.invalidate();
        }
    }
}
