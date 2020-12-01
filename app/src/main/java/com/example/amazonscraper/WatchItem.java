package com.example.amazonscraper;

public class WatchItem {
    private String itemTitle;
    private String price;
    private String price_before_discount;
    private String price_after_discount;
    private String url;
    private String image_url;

    public WatchItem(String itemTitle,  String price, String price_before_discount, String price_after_discount, String url, String image_url) {
        this.itemTitle = itemTitle;
        this.price = price;
        this.price_before_discount = price_before_discount;
        this.price_after_discount = price_after_discount;
        this.url = url;
        this.image_url = image_url;
    }
    public String getItemTitle(){return itemTitle;}
    public String getPrice() {
        return price;
    }
    public String getPriceBeforeDiscount() {
        return price_before_discount;
    }
    public String getPriceAFterDiscount() {
        return price_after_discount;
    }
    public String getUrl() { return url; }
    public String getImage_url(){return image_url;}
}
