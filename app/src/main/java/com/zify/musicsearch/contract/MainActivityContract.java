package com.zify.musicsearch.contract;


import com.zify.musicsearch.model.Artist;

import java.util.ArrayList;
import java.util.List;

public interface MainActivityContract {

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

        void initView();

        void setViewData(String data);

        void showProgress();

        void hideProgress();

        void setDataToRecyclerView(List<Artist> noticeArrayList);

        void onResponseFailure(Throwable throwable);
    }

    interface Model {

        String getData();
    }

    interface Presenter {

        void onClick(android.view.View view);
        void fetchDataFromService();
    }

    /**
     * Intractors are classes built for fetching data from your database, web services, or any other data source.
     **/
    interface GetNoticeIntractor {

        interface OnFinishedListener {
            void onFinished(ArrayList<Artist> noticeArrayList);
            void onFailure(Throwable t);
        }

        void getNoticeArrayList(OnFinishedListener onFinishedListener);
    }



}
