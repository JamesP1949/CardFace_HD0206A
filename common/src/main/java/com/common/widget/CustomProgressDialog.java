package com.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.common.R;


/**
 * 自定义进度
 * Created by wang on 2016/1/27.
 */
public class CustomProgressDialog extends Dialog {
    private TextView tv_message;
    private int textColor;
    private String message;
    private Context mContext;
    public CustomProgressDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress);
        tv_message = (TextView) findViewById(R.id.tvMsg);
        if (!TextUtils.isEmpty(message)) {
            tv_message.setText(message);
        }
        if (textColor >0) {
            tv_message.setTextColor(ContextCompat.getColor(mContext, textColor));
        }
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
        this.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                    return true;
                }
                else{
                    return false;
                }
            }
        });
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public static class Builder {
        private Context mContext;
        private String message;
        private int msgColor;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMessage(String msg) {
            this.message = msg;
            return this;
        }

        public Builder setMsgColor(int color) {
            this.msgColor = color;
            return this;
        }

        public CustomProgressDialog create() {
            CustomProgressDialog customProgressDialog = new CustomProgressDialog(mContext, R.style.CustomProgressDialog);
            customProgressDialog.setMessage(message);
            customProgressDialog.setTextColor(msgColor);
            return customProgressDialog;
        }
    }
}
