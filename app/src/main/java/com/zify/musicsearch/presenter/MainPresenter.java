package com.zify.musicsearch.presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.zify.musicsearch.MusicSearchApplication;
import com.zify.musicsearch.contract.MainContract;
import com.zify.musicsearch.model.Artist;
import com.zify.musicsearch.model.ArtistSearchResponse;
import com.zify.musicsearch.model.MainActivityModel;
import com.zify.musicsearch.utils.Constants;

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

public class MainPresenter implements BasePresenter {

    private static MainContract.SearchView mView;
    private MainContract.Model mModel;

    public MainPresenter(MainContract.SearchView view) {
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
        //String url = "http://ws.audioscrobbler.com/2.0/?method=artist.search&artist=cher&api_key=fa2e62987b8c372e16daa60331164d12&format=json";
        String url = Constants.ARTIST_SEARCH_ENDPINT_URL+"cher&api_key="+Constants.API_KEY + "&format=json";
        boolean networkStatus = Constants.checkConnection(MusicSearchApplication.getAppContext());
        LoadMusicData mLoadMusicData = new LoadMusicData(url,this, mView, networkStatus);
        mLoadMusicData.execute();
    }


    public static class LoadMusicData extends AsyncTask<ArtistSearchResponse, Void, ArtistSearchResponse> {
        public static final int STATE_LOADING=1;
        public static final int STATE_EMPTY=2;
        public static final int STATE_SHOW_ARTICLE=3;
        private final BasePresenter presenter;
        private final MainContract.SearchView view;
        private String endpointURL;
        boolean networkStatus;
        final String fileName = "SearchMusic.json";

        public LoadMusicData(String URL, BasePresenter presenter, MainContract.SearchView view, boolean internetStatus){
            this.presenter = presenter;
            this.view=view;
            this.endpointURL=URL;
            this.networkStatus = internetStatus;
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
            StatusLine statusLine;
            Gson gson = new Gson();
            if(networkStatus){
                try {
                    response = httpclient.execute(new HttpGet(endpointURL));
                    statusLine = response.getStatusLine();
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        responseString = out.toString();
                        mArtistSearchResponse = gson.fromJson(out.toString(), ArtistSearchResponse.class);
                        Log.v("Response::::", responseString);
                        Constants.writeToFile(MusicSearchApplication.getAppContext(), out.toString(),fileName);
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
            }else {
                if(Constants.isFilePresent(MusicSearchApplication.getAppContext(),fileName)) {
                    mArtistSearchResponse = gson.fromJson(Constants.readFromFile(MusicSearchApplication.getAppContext(),fileName),ArtistSearchResponse.class);
                }

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
