package com.wis.module.query;

import com.common.scope.ActivityScope;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.wis.R;
import com.wis.module.adapter.PersonAdapter;
import com.wis.widget.PhotoPopWin;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by JamesP949 on 2017/5/24.
 * Function:
 */
@Module
public class QueryModule {
    private QueryActivity mActivity;

    public QueryModule(QueryActivity activity) {
        mActivity = activity;
    }

    /*private QueryContract.View view;

        public QueryModule(QueryContract.View view) {
            this.view = view;
        }
        @ActivityScope
        @Provides
        QueryContract.View provideView() {
            return view;
        }*/
    @ActivityScope
    @Provides
    QueryPresenter providePresenter() {
        return new QueryPresenter();
    }

    @ActivityScope
    @Provides
    @Named(value = "query")
    PersonAdapter provideAdapter() {
        return new PersonAdapter(mActivity);
    }

    @ActivityScope
    @Provides
    @Named(value = "query")
    PhotoPopWin providePopWin() {
        return new PhotoPopWin(mActivity);
    }

    @ActivityScope
    @Provides
    DividerDecoration provideDividerDecoration() {
        return new DividerDecoration(R.color.grey_600, 1, 0, 0);
    }


}
