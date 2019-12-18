package com.zify.musicsearch.model;

import com.zify.musicsearch.contract.MainActivityContract;

public class MainActivityModel implements MainActivityContract.Model {

    @Override
    public String getData() {
        return "Hello World";
    }
}
