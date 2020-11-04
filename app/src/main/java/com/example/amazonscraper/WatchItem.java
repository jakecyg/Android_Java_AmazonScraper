package com.example.amazonscraper;

public class WatchItem {
    private String itemTitle;
    private String price;
    private String url;

    public WatchItem(String itemTitle,  String price, String url) {
        this.itemTitle = itemTitle;
        this.price = price;
        this.url = url;
    }
    public String getItemTitle(){return itemTitle;}
    public String getPrice() {
        return price;
    }
    public String getUrl() {
        return url;
    }
}
