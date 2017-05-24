package com.wis.module.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.wis.R;
import com.wis.bean.Person;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by JamesP949 on 2017/5/5.
 * Function:
 */

public class PersonViewHolder extends BaseViewHolder<Person> {
    TextView mItemTime;
    TextView mItemName;
    TextView mItemSex;
    TextView mItemNation;
    TextView mItemId;
    TextView mItemAddress;

    public PersonViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_person);
        mItemTime = $(R.id.item_time);
        mItemName = $(R.id.item_name);
        mItemSex = $(R.id.item_sex);
        mItemNation = $(R.id.item_nation);
        mItemId = $(R.id.item_id);
        mItemAddress = $(R.id.item_address);
    }

    @Override
    public void setData(Person data) {
        super.setData(data);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = new Date(data.getDetectTime());
        mItemTime.setText(format.format(date));
        mItemName.setText(data.getName());
        mItemSex.setText(data.getSex());
        mItemNation.setText(data.getNation());
        mItemId.setText(data.getCardId());
        mItemAddress.setText(data.getAddress());
    }
}
