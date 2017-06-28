package com.wis.module.fragment_read;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.base.BaseFragment;
import com.wis.R;
import com.wis.application.App;
import com.wis.application.AppCore;
import com.wis.config.UserConfig;
import com.wis.utils.GlobalConstant;

import javax.inject.Inject;

import butterknife.BindView;
import cn.com.aratek.idcard.IDCardReader;

/**
 * Created by JamesP949 on 2017/4/27.
 * Function:
 */

public class ReadFragment extends BaseFragment<ReadPresenter> implements ReadContract.View {

    @BindView(R.id.iv_photo)
    ImageView mIvPhoto;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_nation)
    TextView mTvNation;
    @BindView(R.id.tv_birth)
    TextView mTvBirth;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_id)
    TextView mTvId;
    @BindView(R.id.tv_official)
    TextView mTvOfficial;
    @BindView(R.id.tv_valid)
    TextView mTvValid;
    @Inject
    UserConfig mUserConfig;
    @Inject
    ReadPresenter mPresenter;
    @Inject
    App mApp;
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
        mApp.setReStartReader();
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
    protected void injectDagger() {
        DaggerReadComponent.builder()
                .appComponent(AppCore.getAppComponent())
                .readModule(new ReadModule())
                .build()
                .inject(this);
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

    // 读卡成功刷新UI
    private void updateUI() {
        mTvName.setText(mUserConfig.getName());
        mTvSex.setText(mUserConfig.getSex());
        mTvNation.setText(mUserConfig.getNation());
        mTvBirth.setText(mUserConfig.getBirthday());
        mTvAddress.setText(mUserConfig.getAddress());
        mTvId.setText(mUserConfig.getIdNum());
        mTvOfficial.setText(mUserConfig.getOffice());
        mTvValid.setText(mUserConfig.getValidDate());
        if (!TextUtils.isEmpty(mUserConfig.getImagePath())) {
            Bitmap bitmap = BitmapFactory.decodeFile(mUserConfig.getImagePath());
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
