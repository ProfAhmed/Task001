package com.pro.ahmed.hardtask001.models;

public class ModelCategory {
    private String TitleEn;
    private String TitleAr;
    private String photo;
    private String haveModel;
    private int id;
    private int productCount;

    public ModelCategory() {
    }

    public ModelCategory(String titleEn, String titleAr, String photo,int productCount, int id) {
        TitleEn = titleEn;
        TitleAr = titleAr;
        this.photo = photo;
        this.productCount = productCount;
        this.id = id;
    }

    public String getTitleEn() {
        return TitleEn;
    }

    public String getTitleAr() {
        return TitleAr;
    }

    public String getPhoto() {
        return photo;
    }

    public int getId() {
        return id;
    }

    public int getProductCount() {
        return productCount;
    }
}
