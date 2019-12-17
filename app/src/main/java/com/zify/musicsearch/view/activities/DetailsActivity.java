package com.zify.musicsearch.view.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zify.musicsearch.R;
import com.zify.musicsearch.contract.MainActivityContract;
import com.zify.musicsearch.model.Artist;

import java.util.List;

public class DetailsActivity  extends AppCompatActivity implements MainActivityContract.View {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initView() {

    }

    @Override
    public void setViewData(String data) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void setDataToRecyclerView(List<Artist> noticeArrayList) {

    }

    @Override
    public void onResponseFailure(Throwable throwable) {

    }
}
