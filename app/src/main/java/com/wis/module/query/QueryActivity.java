package com.wis.module.query;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.common.base.BaseToolBarActivity;
import com.common.utils.Utils;
import com.common.widget.CustomProgressDialog;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.socks.library.KLog;
import com.wis.R;
import com.wis.application.AppCore;
import com.wis.bean.Person;
import com.wis.module.adapter.PersonAdapter;
import com.wis.widget.PhotoPopWin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by JamesP949 on 2017/3/28.
 * Function:
 */

public class QueryActivity extends BaseToolBarActivity<QueryPresenter> implements QueryContract
        .View {

    @BindView(R.id.et_search)
    AppCompatEditText mEtSearch;
    @BindView(R.id.iv_tips)
    ImageView mIvTips;
    @BindView(R.id.recyclerView)
    EasyRecyclerView mRecyclerView;

    @Inject
    QueryPresenter mPresenter;

    @Inject
    @Named("query")
    PersonAdapter mAdapter;
    @Inject
    @Named("query")
    PhotoPopWin mPopWin;
    @Inject
    DividerDecoration decoration;

    private CustomProgressDialog mProgressDialog;
    private int startIndex = 0;
    private boolean hasMore;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_query;
    }

    @Override
    protected void injectDagger() {
        DaggerQueryComponent.builder()
                .appComponent(AppCore.getAppComponent())
                .queryModule(new QueryModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setToolbarTitle("查询");
        setToolbarIndicator(true);
        menuId = R.menu.menu_query;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setErrorView(R.layout.x_common_query_error_view);
        mRecyclerView.setEmptyView(R.layout.x_common_view_empty);

        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //TODO 显示证件照 现场照 姓名
                Person record = mAdapter.getItem(position);
                mPopWin.setImageData(record.getDetectPhotoPath(), record.getIdCardPhotoPath());
                mPopWin.show(mRecyclerView);
            }
        });

        mAdapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                // TODO 显示删除该条记录
                delete(position);
                return false;
            }
        });

        mAdapter.setMore(
                R.layout.x_common_view_more, new RecyclerArrayAdapter.OnMoreListener() {

                    @Override
                    public void onMoreShow() {
                        // TODO 加载下一页
                        KLog.e("startIndex----" + startIndex);
                        if (hasMore)
                            loadDataFromDB(startIndex);
                        else mAdapter.stopMore();
                    }

                    @Override
                    public void onMoreClick() {
                        // do nothing...
                    }
                });

        mAdapter.setNoMore(R.layout.x_common_view_nomore);
        mPresenter.textChangedListener(mEtSearch);
//        compare();
    }

    private void compare() {
        Schedulers.newThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                /*List<Person> persons = mDaoManager.queryAllPersons();
                if (persons.isEmpty()) return;
                Person person = persons.get(0);
                float similarity = App.getInstance().getWisMobile().calculate2ImageSimilarity(person
                        .getIdCardPhotoPath(), person.getDetectPhotoPath());
                KLog.e("比对结果---" + similarity);*/
            }
        });
    }

    public void delete(final int pos) {
        final Person person = mAdapter.getItem(pos);
        new AlertDialog.Builder(this)
                .setTitle("删除记录")
                .setMessage("姓名：" + person.getName() + "\n" + "比对时间：" + Utils.dateformat_2String
                        (person.getDetectTime()))
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mAdapter.remove(person);
                        mPresenter.deletePerson(person);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

    }


    @Override
    public void loadDataFromDB(int startIndex) {
        mPresenter.loadFromDB(startIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDB(startIndex);
    }

    @Override
    public void showProgressDialog(String msg) {
        mProgressDialog = new CustomProgressDialog.Builder(QueryActivity.this)
                .setMessage(msg).create();
        Utils.hideSoftInput(this, mEtSearch);
        mProgressDialog.show();
    }

    @Override
    public void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }


    @Override
    public void tipSwitch(boolean isEmpty) {
        if (isEmpty)
            mIvTips.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search));
        else
            mIvTips.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete));
    }

    @Override
    public void updateUI(List<Person> persons) {
        KLog.e("size-----" + persons.size());
        if (persons.isEmpty()) {
            hasMore = false;
            if (startIndex == 0) {
                mRecyclerView.showEmpty();
            }
            mAdapter.pauseMore();
        } else {
            if (persons.size() < 10) {
                hasMore = false;
                mAdapter.stopMore();
            } else {
                hasMore = true;
            }
            startIndex += (persons.size() - 1);
            mAdapter.addAll(persons);
        }
        Utils.hideSoftInput(QueryActivity.this, mEtSearch);
    }

    @Override
    public void updateUIAfterQuery(List<Person> persons) {
        mAdapter.clear();
        if (persons.isEmpty())
            mRecyclerView.showError();
        mAdapter.addAll(persons);
    }

    @Override
    public void updateUIAfterExport() {
        startIndex = 0;
        // 清空数据
        mAdapter.clear();
    }


    @Override
    public void clearUI() {
        mEtSearch.setText(null);
        mAdapter.clear();
        mIvTips.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search));
        // 重新查询
        startIndex = 0;
        loadDataFromDB(startIndex);
    }

    @OnClick(R.id.iv_tips)
    public void onClick() {
        if (!TextUtils.isEmpty(mEtSearch.getEditableText().toString()))
            clearUI();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            mPresenter.deleteAll();
        } else if (item.getItemId() == R.id.action_export) {
            // TODO 导出为.csv文件
            mPresenter.export();
        }
        return super.onOptionsItemSelected(item);
    }
}
