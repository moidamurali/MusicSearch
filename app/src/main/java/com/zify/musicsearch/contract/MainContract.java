package com.zify.musicsearch.contract;


import com.zify.musicsearch.model.Artist;

import java.util.List;

public interface MainContract {

    interface SearchView extends BaseView {

        void setDataToRecyclerView(List<Artist> noticeArrayList);

        void onResponseFailure(Throwable throwable);
    }

    interface Model {

        String getData();
    }


}
