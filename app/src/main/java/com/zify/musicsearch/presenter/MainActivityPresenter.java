package com.zify.musicsearch.presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.zify.musicsearch.contract.MainActivityContract;
import com.zify.musicsearch.model.Artist;
import com.zify.musicsearch.model.ArtistSearchResponse;
import com.zify.musicsearch.model.MainActivityModel;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivityPresenter implements MainActivityContract.Presenter {

    private MainActivityContract.View mView;
    private MainActivityContract.Model mModel;

    public MainActivityPresenter(MainActivityContract.View view) {
        mView = view;
        initPresenter();
    }

    private void initPresenter() {
        mModel = new MainActivityModel();
        mView.initView();
    }

    @Override
    public void onClick(android.view.View view) {
        String data = mModel.getData();
        mView.setViewData(data);
    }

    @Override
    public void fetchDataFromService() {
        String url = "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist=cher&api_key=fa2e62987b8c372e16daa60331164d12&format=json";
        LoadMusicData mLoadMusicData = new LoadMusicData(url,this, mView);
        mLoadMusicData.execute();
    }


    public static class LoadMusicData extends AsyncTask<ArtistSearchResponse, Void, ArtistSearchResponse> {
        public static final int STATE_LOADING=1;
        public static final int STATE_EMPTY=2;
        public static final int STATE_SHOW_ARTICLE=3;
        private final MainActivityContract.Presenter presenter;
        private final MainActivityContract.View view;
        private String endpointURL;

        public LoadMusicData(String URL,MainActivityContract.Presenter presenter, MainActivityContract.View view){
            this.presenter = presenter;
            this.view=view;
            this.endpointURL=URL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            view.showProgress();
        }

        @Override
        protected ArtistSearchResponse doInBackground(ArtistSearchResponse... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            ArtistSearchResponse mArtistSearchResponse = null;
            try {
                response = httpclient.execute(new HttpGet(endpointURL));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    Gson gson = new Gson();
                    mArtistSearchResponse = gson.fromJson(out.toString(), ArtistSearchResponse.class);
                    Log.v("Response::::", responseString);
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
                view.hideProgress();
            } catch (IOException e) {
                //TODO Handle problems..
                view.hideProgress();
            }
            return mArtistSearchResponse;
        }

        @Override
        protected void onPostExecute(ArtistSearchResponse mArtistSearchResponse) {
            super.onPostExecute(mArtistSearchResponse);
            view.hideProgress();
            List<Artist> artist = mArtistSearchResponse.getResults().getArtistmatches().getArtist();
            view.setDataToRecyclerView(artist);
        }

    }




}
