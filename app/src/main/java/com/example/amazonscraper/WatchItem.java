package com.example.amazonscraper;

public class WatchItem {
    private String itemTitle;
    private String price;
    private String url;
    private String image_url;

    public WatchItem(String itemTitle,  String price, String url, String image_url) {
        this.itemTitle = itemTitle;
        this.price = price;
        this.url = url;
        this.image_url = image_url;
    }
    public String getItemTitle(){return itemTitle;}
    public String getPrice() {
        return price;
    }
    public String getUrl() {
        return url;
    }
    public String getImage_url(){return image_url;}
}
