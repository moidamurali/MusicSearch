package com.zify.musicsearch.presenter;

import android.os.AsyncTask;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.google.gson.Gson;
import com.zify.musicsearch.MusicSearchApplication;
import com.zify.musicsearch.contract.DetailsView;
import com.zify.musicsearch.contract.MainContract;
import com.zify.musicsearch.model.ArtistDetails;
import com.zify.musicsearch.model.MainActivityModel;
import com.zify.musicsearch.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DetailsPresenter implements BasePresenter {

    private static DetailsView mView;
    private MainContract.Model mModel;
    String selectedArtist;

    public DetailsPresenter(DetailsView view) {
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
        mView.setViewData(null);
    }

    @Override
    public void fetchDataFromService(String selectedArtist) {
        try {
            String query = URLEncoder.encode(selectedArtist, "utf-8");
            String url = Utils.ARTIST_INFO_ENDPINT_URL + query +"&api_key="+ Utils.API_KEY + "&format=json";
            boolean networkStatus = Utils.checkConnection(MusicSearchApplication.getAppContext());
            LoadArtistDetails mLoadArtistDetails = new LoadArtistDetails(url,this, mView, networkStatus);
            mLoadArtistDetails.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    public static class LoadArtistDetails extends AsyncTask<ArtistDetails, Void, ArtistDetails> {
        private final BasePresenter presenter;
        private final DetailsView view;
        private String endpointURL;
        boolean networkStatus;
        final String fileName = "UserInfo.json";

        public LoadArtistDetails(String URL, BasePresenter presenter, DetailsView view, boolean internetStatus){
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
        protected ArtistDetails doInBackground(ArtistDetails... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            ArtistDetails mArtistDetails = new ArtistDetails();
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
                        Log.v("Response::::", responseString);
                        Utils.writeToFile(MusicSearchApplication.getAppContext(), out.toString(),fileName);
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
                if(Utils.isFilePresent(MusicSearchApplication.getAppContext(),fileName)) {
                    responseString = Utils.readFromFile(MusicSearchApplication.getAppContext(),fileName);
                }

            }

            try {
                String imageURL = null;
                JSONObject obj = new JSONObject(responseString);
                JSONObject artistObject = obj.getJSONObject("artist");
                JSONArray images = artistObject.getJSONArray("image");
                JSONObject bioObject =  artistObject.getJSONObject("bio");

                for (int i=0; i<images.length(); i++) {
                    JSONObject actor = images.getJSONObject(i);
                    imageURL = actor.getString("#text");

                }
                mArtistDetails.setArtistBio(artistObject.getString("url"));
                mArtistDetails.setArtistName(artistObject.getString("name"));
                mArtistDetails.setArtistImage(imageURL);
                mArtistDetails.setArtistsummary(bioObject.getString("summary"));

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + responseString + "\"");
            }



            return mArtistDetails;
        }

        @Override
        protected void onPostExecute(ArtistDetails mArtistDetails) {
            super.onPostExecute(mArtistDetails);
            view.hideProgress();
            mView.setViewData(mArtistDetails);
        }

    }




}
