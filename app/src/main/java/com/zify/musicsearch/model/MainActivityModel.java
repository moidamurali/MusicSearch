package com.zify.musicsearch.model;

import com.zify.musicsearch.contract.MainContract;

public class MainActivityModel implements MainContract.Model {

    @Override
    public String getData() {
        return "Hello World";
    }
}
