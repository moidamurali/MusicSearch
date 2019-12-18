package com.zify.musicsearch.contract;


import com.zify.musicsearch.model.ArtistDetails;

/**
 * showProgress() and hideProgress() would be used for displaying and hiding the progressBar
 * while the setDataToRecyclerView and onResponseFailure is fetched from the GetNoticeInteractorImpl class
 **/


public interface BaseView {

    void initView();

    void setViewData(ArtistDetails details);

    void showProgress();

    void hideProgress();
}
