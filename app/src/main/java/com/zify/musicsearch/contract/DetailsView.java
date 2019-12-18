package com.zify.musicsearch.contract;
import com.zify.musicsearch.model.ArtistDetails;

public interface DetailsView extends BaseView {

    void setViewData(ArtistDetails details);
}
