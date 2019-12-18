package com.zify.musicsearch.contract;


import com.zify.musicsearch.model.Artist;

import java.util.List;

public interface MainContract {

    /**
     * showProgress() and hideProgress() would be used for displaying and hiding the progressBar
     * while the setDataToRecyclerView and onResponseFailure is fetched from the GetNoticeInteractorImpl class
     **/

    interface View {

        void initView();

        void setViewData(String data);

        void showProgress();

        void hideProgress();
    }

    interface SearchView extends View {

        void setDataToRecyclerView(List<Artist> noticeArrayList);

        void onResponseFailure(Throwable throwable);
    }

    interface Model {

        String getData();
    }


}