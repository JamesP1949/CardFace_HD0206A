package com.wis.module.fragment_read;

import com.common.base.BasePresenter;
import com.wis.utils.GlobalConstant;

import cn.com.aratek.idcard.IDCard;
import cn.com.aratek.idcard.IDCardReader;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public class ReadPresenter extends BasePresenter<ReadContract.View> implements ReadContract
        .Presenter {
    private IDCardReader mReader;

    public ReadPresenter() {
        mReader = IDCardReader.getInstance();
    }

    @Override
    public void display(IDCard idCard) {

    }

    @Override
    public void open() {
        int open = mReader.open();
        if (open == IDCardReader.RESULT_OK)
            mView.showToast("身份证读卡器打开成功");
        else
            mView.showToast("身份证读卡器打开失败：" + GlobalConstant.getMsg(open));
    }

    @Override
    public void close() {
        int close = mReader.close();
        if (close != IDCardReader.RESULT_OK)
            mView.showToast("身份证读卡器关闭失败：" + GlobalConstant.getMsg(close));
    }


}
