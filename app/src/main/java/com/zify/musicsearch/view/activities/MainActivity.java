package com.zify.musicsearch.view.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zify.musicsearch.R;
import com.zify.musicsearch.contract.MainContract;
import com.zify.musicsearch.model.Artist;
import com.zify.musicsearch.presenter.MainPresenter;
import com.zify.musicsearch.presenter.BasePresenter;
import com.zify.musicsearch.view.adapter.SearchListAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.SearchView {

    private EditText mSearcEditText;
    private RecyclerView mRecyclerView;
    private BasePresenter mPresenter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainPresenter(this);
        dialog = new ProgressDialog(this);
        mPresenter.fetchDataFromService();
        initProgressBar();

    }

    @Override
    public void initView() {
        //progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mSearcEditText = (EditText) findViewById(R.id.search_music_et);
        mRecyclerView = (RecyclerView) findViewById(R.id.data_recycler_view);
    }

    @Override
    public void setViewData(String data) {
        //mSearcEditText.setText(data);
    }

    /**
     * Initializing progress dialog
     * */
    private void initProgressBar() {

        dialog.setMessage("Downloading data, please wait....");
    }

    @Override
    public void showProgress() {
        dialog.show();
    }

    @Override
    public void hideProgress() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void setDataToRecyclerView(List<Artist> noticeArrayList) {

        SearchListAdapter adapter = new SearchListAdapter(noticeArrayList, this /*, recyclerItemClickListener*/);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        searchFilterData(adapter);
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(MainActivity.this, "Please try again after some time " + throwable.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * RecyclerItem click event listener
     * */
/*    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener() {
        @Override
        public void onItemClick(Artist mArtist) {

            Toast.makeText(MainActivity.this,
                    "List title:  " + mArtist.getName(),
                    Toast.LENGTH_LONG).show();

        }
    };*/

    private void searchFilterData(final SearchListAdapter adapter){
        mSearcEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing
            }
        });
    }
}