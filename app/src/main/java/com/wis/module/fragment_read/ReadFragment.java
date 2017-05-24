package com.wis.module.fragment_read;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.base.BaseFragment;
import com.socks.library.KLog;
import com.wis.R;
import com.wis.application.App;
import com.wis.config.UserConfig;
import com.wis.utils.GlobalConstant;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.com.aratek.idcard.IDCardReader;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public class ReadFragment extends BaseFragment<ReadPresenter> implements ReadContract.View {

    @Bind(R.id.iv_photo)
    ImageView mIvPhoto;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_sex)
    TextView mTvSex;
    @Bind(R.id.tv_nation)
    TextView mTvNation;
    @Bind(R.id.tv_birth)
    TextView mTvBirth;
    @Bind(R.id.tv_address)
    TextView mTvAddress;
    @Bind(R.id.tv_id)
    TextView mTvId;
    @Bind(R.id.tv_official)
    TextView mTvOfficial;
    @Bind(R.id.tv_valid)
    TextView mTvValid;
    private MyReceiver mReceiver;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_read;
    }

    @Override
    public void register() {
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConstant.Action_READ);
        filter.addAction(GlobalConstant.Action_READ_SUC);
        filter.addAction(GlobalConstant.Action_RESTART_READ);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }

    @Override
    public void reRead() {
        clearUI();
        App.getInstance().setReStartReader();
    }

    @Override
    public void clearUI() {
        mTvName.setText("");
        mTvSex.setText("");
        mTvNation.setText("");
        mTvBirth.setText("");
        mTvAddress.setText("");
        mTvId.setText("");
        mTvOfficial.setText("");
        mTvValid.setText("");
        mIvPhoto.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.avart));
    }

    @Override
    public void setReadStatus(boolean readStatus) {

    }

    @Override
    public void openDevice() {

    }

    @Override
    public void closeDevice() {

    }

    @Override
    protected ReadPresenter getPresenter() {
        return new ReadPresenter();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        register();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // 读卡成功刷新UI
    private void updateUI() {
        UserConfig config = App.getInstance().getUserConfig();
        KLog.e("xingming:" + config.getName());
        mTvName.setText(config.getName());
        mTvSex.setText(config.getSex());
        mTvNation.setText(config.getNation());
        mTvBirth.setText(config.getBirthday());
        mTvAddress.setText(config.getAddress());
        mTvId.setText(config.getIdNum());
        mTvOfficial.setText(config.getOffice());
        mTvValid.setText(config.getValidDate());
        if (!TextUtils.isEmpty(config.getImagePath())) {
            Bitmap bitmap = BitmapFactory.decodeFile(config.getImagePath());
            mIvPhoto.setImageBitmap(bitmap);
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GlobalConstant.Action_MSG)) {
                int intExtra = intent.getIntExtra(GlobalConstant.Action_MSG, -1);
                switch (intExtra) {
                    case GlobalConstant.OPEN_SUC:
                        showToast("身份证读卡器打开成功");
                        break;
                    case IDCardReader.RESULT_OK:

                        showToast(GlobalConstant.getMsg(intExtra));
                        break;
                    case IDCardReader.NO_CARD:
                        // TODO do nothing...
                        break;
                    default:
                        showToast(GlobalConstant.getMsg(intExtra));
                        break;

                }
            } else if (intent.getAction().equals(GlobalConstant.Action_READ_SUC))
                updateUI();
            else if (intent.getAction().equals(GlobalConstant.Action_RESTART_READ))
                reRead();
//            KLog.e("intExt:" + intExtra);
        }
    }
}
