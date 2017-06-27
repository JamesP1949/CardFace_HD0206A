package com.wis.module.fragment_compare;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentManager;

import com.common.cache.ImageCache;
import com.common.cache.ImageReSize;
import com.common.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@Module
public class CompareModule {
    private CompareFragment mFragment;

    public CompareModule(CompareFragment fragment) {
        mFragment = fragment;
    }

    @FragmentScope
    @Provides
    public ComparePresenter_ providePresenter() {
        return new ComparePresenter_();
    }

    @FragmentScope
    @Provides
    public SensorManager provideSensorManager() {
        return (SensorManager) mFragment.getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @FragmentScope
    @Provides
    public Sensor provideSensor() {
        return provideSensorManager().getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @FragmentScope
    @Provides
    public ImageReSize provideImageReSize() {
        FragmentManager fragmentManager = mFragment.getActivity().getSupportFragmentManager();
        ImageCache.ImageCacheParams imageCacheParams = new ImageCache.ImageCacheParams();
        ImageReSize reSize = new ImageReSize(mFragment.getActivity());
        reSize.addImageCache(fragmentManager, imageCacheParams);
        return reSize;
    }
}
