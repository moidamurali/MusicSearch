package com.zify.musicsearch.view.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zify.musicsearch.R;
import com.zify.musicsearch.contract.BaseView;
import com.zify.musicsearch.model.ArtistDetails;
import com.zify.musicsearch.presenter.BasePresenter;
import com.zify.musicsearch.presenter.DetailsPresenter;
import com.zify.musicsearch.utils.Constants;

public class DetailsActivity  extends Activity implements BaseView {

    private TextView artistName;
    private TextView artistSummary;
    private TextView artistBioData;
    private ImageView artistImage;
    private String artName;
    private ProgressDialog dialog;
    private BasePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);

        Bundle mBundle = getIntent().getExtras();

        if(mBundle!=null){
            artName = mBundle.getString("artist_name");
        }
    }

    @Override
    public void initView() {
        mPresenter = new DetailsPresenter(this);
        artistImage = (ImageView)findViewById(R.id.user_image_view);
        artistName = (TextView)findViewById(R.id.tv_name);
        artistBioData = (TextView) findViewById(R.id.tv_bio_data);
        artistSummary = (TextView)findViewById(R.id.tv_summary);
        dialog = new ProgressDialog(this);
        initProgressBar();
        callFromServices();
    }

    /**
     * Initializing progress dialog
     * */
    private void initProgressBar() {

        dialog.setMessage("Downloading data, please wait....");
    }

    @Override
    public void setViewData(ArtistDetails data) {
        artistName.setText(data.getArtistName());
        artistBioData.setText(data.getArtistBio());
        artistSummary.setText(data.getArtistsummary());

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

    public void callFromServices(){
        if(Constants.checkConnection(this)) {
            mPresenter.fetchDataFromService();
        }else {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

}
