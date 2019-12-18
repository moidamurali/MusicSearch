package com.zify.musicsearch.view.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zify.musicsearch.R;
import com.zify.musicsearch.contract.DetailsView;
import com.zify.musicsearch.model.ArtistDetails;
import com.zify.musicsearch.presenter.BasePresenter;
import com.zify.musicsearch.presenter.DetailsPresenter;
import com.zify.musicsearch.utils.Utils;
import com.zify.musicsearch.utils.thumbnailutils.BitmapCache;
import com.zify.musicsearch.utils.thumbnailutils.ThumbnailCreateor;

public class DetailsActivity  extends Activity implements DetailsView {

    private TextView artistName;
    private TextView artistSummary;
    private TextView artistBioData;
    private ImageView artistImage;
    private String artName;
    private ProgressDialog mDialog;
    private BasePresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);

        Bundle mBundle = getIntent().getExtras();

        if(mBundle!=null){
            artName = mBundle.getString("artist_name");
        }
        mPresenter = new DetailsPresenter(this);
        callFromServices();
    }

    @Override
    public void initView() {

        artistImage = (ImageView)findViewById(R.id.user_image_view);
        artistName = (TextView)findViewById(R.id.tv_name);
        artistBioData = (TextView) findViewById(R.id.tv_bio_data);
        artistSummary = (TextView)findViewById(R.id.tv_summary);
        mDialog = null;
        initProgressBar();
    }

    /**
     * Initializing progress dialog
     * */
    private void initProgressBar() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Downloading data, please wait....");
    }

    @Override
    public void setViewData(ArtistDetails data) {
        artistName.setText(data.getArtistName());
        artistBioData.setText(data.getArtistBio());
        Spanned htmlAsSpanned = Html.fromHtml(data.getArtistsummary());
        artistSummary.setText(htmlAsSpanned);


        //For Setting the Image
        if(data.getArtistImage()!=null){
            Bitmap found = BitmapCache.GetInstance().GetBitmapFromMemoryCache(data.getArtistImage());
            if (found != null) {
                artistImage.setImageBitmap(found);
            }else{
                ThumbnailCreateor.BitmapWorkerTask task = new ThumbnailCreateor.BitmapWorkerTask(artistImage,  data.getArtistImage());

                ThumbnailCreateor.AsyncDrawable downloadedDrawable = new ThumbnailCreateor.AsyncDrawable(getResources(), Utils.getResizedBitMap() ,task);
                artistImage.setImageDrawable(downloadedDrawable);
                task.execute(String.valueOf(data.getArtistImage()));
            }

        }

        if(!data.getArtistBio().isEmpty())
            Utils.clickURL(this,artistBioData, data.getArtistBio(),data.getArtistBio(),0,false);
    }

    @Override
    public void showProgress() {
        mDialog.show();
    }

    @Override
    public void hideProgress() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void callFromServices(){
        if(Utils.checkConnection(this)) {
            mPresenter.fetchDataFromService(artName);
        }else {
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDialog.dismiss();
    }
}
