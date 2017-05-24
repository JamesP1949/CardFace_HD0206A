package com.wis.module.query;

import android.widget.EditText;

import com.common.base.IPresenter;
import com.common.base.IView;
import com.wis.bean.Person;

import java.util.List;

/**
 * Created by JamesP949 on 2017/3/28.
 * Function:
 */

public interface QueryContract {
    interface View extends IView {
        void loadDataFromDB(int startIndex);
        void showProgressDialog(String msg);
        void closeProgressDialog();
        void tipSwitch(boolean isEmpty);
        void updateUI(List<Person> persons);
        void updateUIAfterQuery(List<Person> persons);
        void delete(int pos);
        void clearUI();
    }

    interface Presenter extends IPresenter<View> {
        void loadFromDB(int startIndex);
        void textChangedListener(EditText editText);
        void deletePerson(Person person);
        void deleteAll();
        void export(); // 将数据库数据导出
    }
}
