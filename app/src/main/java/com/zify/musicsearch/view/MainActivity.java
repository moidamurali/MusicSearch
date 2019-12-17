package com.zify.musicsearch.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zify.musicsearch.R;
import com.zify.musicsearch.contract.MainActivityContract;
import com.zify.musicsearch.model.Artist;
import com.zify.musicsearch.presenter.MainActivityPresenter;
import com.zify.musicsearch.view.adapter.SearchListAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {

    private EditText mSearcEditText;
    private RecyclerView mRecyclerView;
    private MainActivityContract.Presenter mPresenter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPresenter = new MainActivityPresenter(this);
        mPresenter.fetchDataFromService();
        initProgressBar();

    }

    @Override
    public void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mSearcEditText = (EditText) findViewById(R.id.search_music_et);
        mRecyclerView = (RecyclerView) findViewById(R.id.data_recycler_view);
    }

    @Override
    public void setViewData(String data) {
        //mSearcEditText.setText(data);
    }

    /**
     * Initializing progressbar programmatically
     * */
    private void initProgressBar() {
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setGravity(Gravity.CENTER);
        relativeLayout.addView(progressBar);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        progressBar.setVisibility(View.INVISIBLE);

        this.addContentView(relativeLayout, params);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setDataToRecyclerView(List<Artist> noticeArrayList) {

        SearchListAdapter adapter = new SearchListAdapter(noticeArrayList, this , recyclerItemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        searchFilterData(adapter);
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Toast.makeText(MainActivity.this,
                "Something went wrong...Error message: " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    /**
     * RecyclerItem click event listener
     * */
    private RecyclerItemClickListener recyclerItemClickListener = new RecyclerItemClickListener() {
        @Override
        public void onItemClick(Artist mArtist) {

            Toast.makeText(MainActivity.this,
                    "List title:  " + mArtist.getName(),
                    Toast.LENGTH_LONG).show();

        }
    };

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