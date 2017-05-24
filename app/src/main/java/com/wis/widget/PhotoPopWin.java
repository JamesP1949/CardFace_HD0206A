package com.wis.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.common.utils.ImageUtils;
import com.wis.R;

/**
 * Created by JamesP949 on 2017/3/6.
 * Function: 照片展示框
 */

public class PhotoPopWin extends PopupWindow {
    ImageView mIvLive;
    ImageView mIvCard;

    public PhotoPopWin(Context context) {
        super(context);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View mView = mInflater.inflate(R.layout.x_common_photo_preview, null);
        mIvLive = (ImageView) mView.findViewById(R.id.iv_live);
        mIvCard = (ImageView) mView.findViewById(R.id.iv_card);
        this.setContentView(mView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        final ColorDrawable dw = new ColorDrawable(0xffffffff);
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setImageData(String livePath, String cardPath) {
        Bitmap livebp = ImageUtils.readBitmapFromFile(livePath, 150, 200);
        Bitmap cardbp = ImageUtils.readBitmapFromFile(cardPath, 150, 200);
        mIvLive.setImageBitmap(livebp);
        mIvCard.setImageBitmap(cardbp);
    }

    /**
     * 显示
     *
     * @param parent
     */
    public void show(View parent) {
        this.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

}
