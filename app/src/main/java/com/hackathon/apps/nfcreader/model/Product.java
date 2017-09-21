package com.hackathon.apps.nfcreader.model;

/**
 * Created by ruthvik on 21/09/2017.
 */

public class Product {

    public String title;

    public String unitPrice, unitOfMeasure;

    public String defaultImageUrl;

    public Product(String title, String unitPrice, String unitOfMeasure, String defaultImageUrl) {
        this.title = title;
        this.unitPrice = unitPrice;
        this.unitOfMeasure = unitOfMeasure;
        this.defaultImageUrl = defaultImageUrl;
    }
}
