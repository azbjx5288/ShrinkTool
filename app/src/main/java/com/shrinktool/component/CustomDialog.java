package com.shrinktool.component;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shrinktool.R;

/**
 * Created on 2016/01/20.
 *
 * @author ACE
 * @功能描述: 定义弹出窗布局
 */
@SuppressLint("NewApi")
public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @SuppressLint("NewApi")
    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        private CustomDialogLayout layoutSet = CustomDialogLayout.LEFT_AND_RIGHT;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the layout styles
         *
         * @param layoutSet
         */
        public Builder setLayoutSet(CustomDialogLayout layoutSet) {
            this.layoutSet = layoutSet;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        @SuppressLint("NewApi")
        public CustomDialog create() {
            CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = LayoutInflater.from(context).inflate(R.layout.alert_dialog_normal_layout, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            if (title != null) {
                ((TextView) layout.findViewById(R.id.title)).setText(title);
            } else {
                ((TextView) layout.findViewById(R.id.title)).setVisibility(View.GONE);
            }
            chooselayout(layout, dialog);
            return dialog;
        }

        private View chooselayout(View layout, CustomDialog dialog) {
            View newLayout = null;
            switch (layoutSet) {
                case UP_AND_DOWN:
                    newLayout = setUpAndDown(layout, dialog);
                    break;
                case LEFT_AND_RIGHT:
                    newLayout = setLeftAndRight(layout, dialog);
                    break;
                case SINGLE:
                    newLayout = setSingle(layout, dialog);
                    break;
            }
            return newLayout;
        }

        /**
         * set button the up and down Layout
         *
         * @param layout
         * @return
         */
        private View setUpAndDown(View layout, final CustomDialog dialog) {
            //设置布局上下显示
            LinearLayout belowLayout = (LinearLayout) layout.findViewById(R.id.alert_below_layout);
            belowLayout.setBackgroundResource(R.drawable.noticedialog_below_bg);
            belowLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            belowLayout.setLayoutParams(params);

            if (positiveButtonText != null) {

                Button positiveButton = (Button) layout.findViewById(R.id.positiveButton);
                positiveButton.setText(positiveButtonText);
                //positiveButton.setBackgroundResource(R.drawable.notidialog_corner_btn_selector_2);
                if (positiveButtonClickListener != null) {
                    positiveButton.setOnClickListener((View v) -> positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE));
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {

                Button negativeButton = (Button) layout.findViewById(R.id.negativeButton);
                negativeButton.setText(negativeButtonText);
                //negativeButton.setBackgroundResource(R.drawable.notidialog_corner_btn_selector);
                if (negativeButtonClickListener != null) {
                    negativeButton.setOnClickListener((View v) -> negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE));
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }

            View dialogView = layout.findViewById(R.id.alert_dialog_view);
            LinearLayout.LayoutParams dialogViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20);
            dialogView.setLayoutParams(dialogViewParams);
            dialogView.setBackgroundColor(Color.TRANSPARENT);
            //dialogView.setVisibility(View.GONE);

            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            }

            return layout;
        }

        /**
         * set button the left and right
         *
         * @param layout
         * @return
         */
        private View setLeftAndRight(View layout, final CustomDialog dialog) {
            //设置布局左右显示
            LinearLayout belowLayout = (LinearLayout) layout.findViewById(R.id.alert_below_layout);
            belowLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            belowLayout.setLayoutParams(params);


            if (positiveButtonText != null) {

                Button positiveButton = (Button) layout.findViewById(R.id.positiveButton);
                positiveButton.setText(positiveButtonText);
                //positiveButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
                //positiveButton.setBackgroundResource(R.drawable.notidialog_rightbtn_selector);
                if (positiveButtonClickListener != null) {
                    positiveButton.setOnClickListener((View v) -> positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE));
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {

                Button negativeButton = (Button) layout.findViewById(R.id.negativeButton);
                negativeButton.setText(negativeButtonText);
                //negativeButton.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
                //negativeButton.setBackgroundResource(R.drawable.notidialog_leftbtn_selector);
                if (negativeButtonClickListener != null) {
                    negativeButton.setOnClickListener((View v) -> negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE));
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }

            View dialogView = layout.findViewById(R.id.alert_dialog_view);
            LinearLayout.LayoutParams dialogViewParams = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
            dialogView.setLayoutParams(dialogViewParams);
            dialogView.setBackgroundColor(Color.TRANSPARENT);
            //dialogView.setVisibility(View.GONE);

            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            }
            return layout;
        }

        /**
         * set button the single
         *
         * @param layout
         * @return
         */
        private View setSingle(View layout, final CustomDialog dialog) {
            if (positiveButtonText == null && negativeButtonText == null) {
                (layout.findViewById(R.id.alert_below_layout)).setVisibility(View.GONE);
            }
            // set the confirm button
            if (positiveButtonText != null) {
                Button positiveButton = (Button) layout.findViewById(R.id.positiveButton);
                positiveButton.setText(positiveButtonText);
                //positiveButton.setBackgroundResource(R.drawable.notidialog_bottomcorner_selector);
                if (positiveButtonClickListener != null) {
                    positiveButton.setOnClickListener((View v) -> positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE));
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                Button negativeButton = (Button) layout.findViewById(R.id.negativeButton);
                negativeButton.setText(negativeButtonText);
                //negativeButton.setBackgroundResource(R.drawable.notidialog_bottomcorner_selector);
                if (negativeButtonClickListener != null) {
                    negativeButton.setOnClickListener((View v) -> negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE));
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.alert_dialog_view).setVisibility(View.GONE);
                layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
            }

            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            }
            return layout;
        }

        /**
         * set button the more
         *
         * @param layout
         * @return
         */
        private View setMoreButton(View layout, CustomDialog dialog) {

            return layout;
        }
    }


}
