package com.zify.musicsearch.view.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zify.musicsearch.R;
import com.zify.musicsearch.contract.MainActivityContract;
import com.zify.musicsearch.model.Artist;

import java.util.List;

public class DetailsActivity  extends Activity implements MainActivityContract.View {

    private TextView artistName;
    private TextView artistSummary;
    private TextView artistBioData;
    private ImageView artistImage;
    private String artName;
    private ProgressDialog dialog;

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
        artistImage = (ImageView)findViewById(R.id.user_image_view);
        artistName = (TextView)findViewById(R.id.tv_name);
        artistBioData = (TextView) findViewById(R.id.tv_bio_data);
        artistSummary = (TextView)findViewById(R.id.tv_summary);
        dialog = new ProgressDialog(this);
    }

    /**
     * Initializing progress dialog
     * */
    private void initProgressBar() {

        dialog.setMessage("Downloading data, please wait....");
    }

    @Override
    public void setViewData(String data) {

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

}
