package com.common.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * 基类popupwindow
 * Created by wang on 2016/6/24.
 */
public abstract class BasePopupWindow extends PopupWindow {

    protected Context context;
    protected LayoutInflater inflater;
    protected View mView;

    public BasePopupWindow(Context context) {
        super(context);
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mView = onCreateView(inflater);
        init();
        onViewCreate(mView);
    }

    /**
     * 创建布局
     *
     * @param inflater
     * @return
     */
    protected abstract View onCreateView(LayoutInflater inflater);

    /**
     * 布局创建后
     *
     * @param view
     */
    protected abstract void onViewCreate(View view);

    private void init() {
        this.setContentView(mView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 显示在底部
     *
     * @param parent
     */
    public void show(View parent) {
        this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

}
