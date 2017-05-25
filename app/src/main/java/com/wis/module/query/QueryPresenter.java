package com.wis.module.query;

import android.text.TextUtils;
import android.widget.EditText;

import com.common.base.BasePresenter;
import com.common.rx.SchedulersCompat;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.socks.library.KLog;
import com.wis.application.AppCore;
import com.wis.bean.DaoManager;
import com.wis.bean.Person;
import com.wis.utils.ExportCsv;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by JamesP949 on 2017/3/28.
 * Function:
 */

public class QueryPresenter extends BasePresenter<QueryContract.View> implements
        QueryContract.Presenter {

    @Inject
    DaoManager mDaoManager;

    @Override
    protected void injectDagger() {
        AppCore.getAppComponent().inject(this);
    }

    @Override
    public void loadFromDB(int startIndex) {
        Disposable disposable = Observable.just(startIndex)
                .map(new Function<Integer, List<Person>>() {
                    @Override
                    public List<Person> apply(@NonNull Integer integer) throws Exception {
                        return mDaoManager.queryPersons(integer);
                    }
                })
                .compose(SchedulersCompat.<List<Person>>applyObservable_IoSchedulers())
                .subscribe(new Consumer<List<Person>>() {
                    @Override
                    public void accept(@NonNull List<Person> persons) throws Exception {
                        mView.updateUI(persons);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        KLog.e("Error：" + throwable.getMessage());
                    }
                });
        addSubscribe(disposable);
    }

    @Override
    public void textChangedListener(EditText editText) {
        Disposable disposable = RxTextView.textChanges(editText)
                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(@NonNull CharSequence charSequence) throws Exception {
                        KLog.e("用户输入==" + charSequence.toString());
                        boolean isEmpty = TextUtils.isEmpty(charSequence.toString());
                        mView.tipSwitch(isEmpty);
                        return !isEmpty;
                    }
                })

                .switchMap(new Function<CharSequence, ObservableSource<CharSequence>>() {
                    @Override
                    public ObservableSource<CharSequence> apply(
                            @NonNull CharSequence charSequence) throws Exception {
                        if (!TextUtils.isEmpty(charSequence.toString())) {
                            KLog.e("执行了switchMap方法");
                            mView.showProgressDialog("查询中...");
                            return Observable.just(charSequence);
                        } else
                            return null;
                    }
                })
                .map(new Function<CharSequence, List<Person>>() {
                    @Override
                    public List<Person> apply(@NonNull CharSequence charSequence) throws Exception {
                        return mDaoManager.fuzzyQueryPersons(charSequence
                                .toString().trim());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Person>>() {
                    @Override
                    public void accept(@NonNull List<Person> persons) throws Exception {
                        mView.closeProgressDialog();
                        mView.updateUIAfterQuery(persons);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mView.closeProgressDialog();
                        KLog.e("onErr:" + throwable.getMessage());
                    }
                });
        addSubscribe(disposable);
    }

    @Override
    public void deletePerson(Person person) {
        String detectPhotoPath = person.getDetectPhotoPath();
        KLog.e("被删除的图片路径--" + detectPhotoPath);
        File file = new File(detectPhotoPath);
        if (file.exists())
            file.delete();
        mDaoManager.deletePerson(person);

    }


    @Override
    public void deleteAll() {
        Observable
                .create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                        if (e.isDisposed()) return;
                        mDaoManager.deleteAllPersons();
                        e.onNext("");
                        e.onComplete();
                    }
                })
                .compose(SchedulersCompat.applyObservable_IoSchedulers())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addSubscribe(d);
                        mView.showProgressDialog("正在删除记录...");
                    }

                    @Override
                    public void onNext(@NonNull Object o) {
                        mView.closeProgressDialog();
                        mView.clearUI();
                        mView.showToast("删除完毕");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void export() {
        Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                        if (e.isDisposed()) return;
                        List<Person> persons = mDaoManager.queryAllPersons();
                        KLog.e("数据-----" + persons.size());
                        String xlsPath = ExportCsv.exportCsv(persons);
                        mDaoManager.deleteAllPersons();
                        e.onNext(xlsPath);
                        e.onComplete();
                    }
                }).compose(SchedulersCompat.<String>applyObservable_IoSchedulers())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addSubscribe(d);
                        mView.showProgressDialog("正在导出数据,请稍后...");
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        mView.closeProgressDialog();
                        if (TextUtils.isEmpty(s))
                            mView.showLongToast("请确保数据库内存储有效数据");
                        else if (s.endsWith(".csv")) {
                            mView.showLongToast("文件导出成功, 存储路径：" + s);
                            mView.updateUIAfterExport();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.closeProgressDialog();
                        mView.showLongToast("导出异常：" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
