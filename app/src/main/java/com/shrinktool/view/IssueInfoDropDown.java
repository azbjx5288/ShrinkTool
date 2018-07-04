package com.shrinktool.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.shrinktool.component.ExpandableViewHelper;

/**
 * 选号界面，支持下拉显示奖期的控件，并与外部的ScrollView进行联动
 * Created by Alashi on 2016/12/8.
 */
public class IssueInfoDropDown extends LinearLayout {
    private static final String TAG = "IssueInfoDropDown";
    private ExpandableViewHelper viewHelper;
    private View expandableView;
    private int expandHeight;
    private ViewGroup.LayoutParams expandParams;
    private float startY = -1;
    private int startHeight = 0;
    private float offsetDeviation;
    private ScrollView scrollView;

    public IssueInfoDropDown(Context context) {
        super(context);
        init();
    }

    public IssueInfoDropDown(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IssueInfoDropDown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /** 设置与此View进行操作联动的ScrollView */
    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
        onExpandableViewChanged();
        scrollView.setOnTouchListener((v, event) -> {
            if (expandableView != null) {
                onScrollViewAction(event);
            }
            return false;
        });
    }

    public void onExpandableViewChanged() {
        expandableView = getChildAt(0);
        expandParams = expandableView.getLayoutParams();
        int widthMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        int heightMS = View.MeasureSpec.makeMeasureSpec(1073741823, View.MeasureSpec.AT_MOST);
        expandableView.measure(widthMS, heightMS);
        expandHeight = expandableView.getMeasuredHeight();
    }

    private void init() {
        setOnClickListener(v -> onClick());
    }

    public void onScrollViewAction(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.d(TAG, "onTouch: ACTION_DOWN");
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //Log.d(TAG, "onTouch: ACTION_UP or ACTION_CANCEL");
                startY = -1;
                offsetDeviation = 0;
                closeIfNeed(scrollView);
                break;

            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG, "onTouch: ACTION_MOVE");
                if (startY == -1) {
                    startY = event.getY();
                    startHeight = expandableView.getHeight();
                    break;
                }
                float deviation = event.getY() - startY;
                if (deviation > 0) {
                    //手指向下
                    int scrollY = scrollView.getScrollY();
                    //Log.d(TAG, "onTouch: 手指向下 deviation = " + deviation + ", scrollY = " + scrollY);
                    if (scrollY == 0) {
                        if (expandableView.getHeight() < expandHeight) {
                            expandParams.height = Math.min(expandHeight,
                                    startHeight + (int) (deviation - offsetDeviation));
                            expandableView.setLayoutParams(expandParams);
                            expandableView.invalidate();
                        }
                    } else if (scrollY > 0){
                        offsetDeviation = deviation;
                    }
                } else {
                    //手指向上
                    //Log.d(TAG, "onTouch: 手指向上 deviation = " + deviation + ", scrollY = " + scrollView.getScrollY());
                    closeIfNeed(scrollView);
                }
                break;
        }
    }

    private void closeIfNeed(ScrollView scrollView) {
        int scrollY = scrollView.getScrollY();
        if (expandableView.getHeight() > 0 && scrollY >= expandableView.getHeight()) {
            expandParams.height = 0;
            expandableView.setLayoutParams(expandParams);
            expandableView.invalidate();
            scrollView.scrollTo(0, scrollY - expandableView.getHeight());
        }
    }

    private void onClick() {
        if (expandableView == null) {
            return;
        }
        if (expandableView.getHeight() > 0) {
            showClose();
            if (scrollView != null && scrollView.getScrollY() != 0) {
                CloseAnimation animation = new CloseAnimation(scrollView);
                scrollView.clearAnimation();
                scrollView.startAnimation(animation);
            }
        } else {
            showOpen();
        }
    }

    private void showClose() {
        if (viewHelper == null) {
            viewHelper = new ExpandableViewHelper(getChildAt(0));
        }
        viewHelper.startCloseAnimation();
    }

    private void showOpen() {
        if (viewHelper == null) {
            viewHelper = new ExpandableViewHelper(getChildAt(0));
        }
        viewHelper.startOpenAnimation();
    }

    private class CloseAnimation extends Animation {
        private int startY;
        private ScrollView scrollView;

        public CloseAnimation(ScrollView scrollView) {
            this.scrollView = scrollView;
            this.startY = scrollView.getScrollY();
            setDuration(300);
        }

        protected void applyTransformation(float interpolatedTime, Transformation t) {
            scrollView.scrollTo(0, (int) (startY * (1-interpolatedTime)));
        }
    }
}
