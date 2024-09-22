package com.example.finalthesis.Marketplace;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CartItem implements Parcelable{
    private String title;
    private String key;
    private String price;
    private String unit;
    private String vendor;
    private List<String> image;
    private String quantity;
    private int count;
    private int cartCount;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQuantity() {return quantity;}

    public void setQuantity(String quantity) {this.quantity = quantity;}

    public CartItem() {
        // Default constructor required for Firebase
    }

    public CartItem(String title, String key, String price, String unit, String vendor, List<String> image, String quantity, int count, int cartCount, boolean selected) {
        this.title = title;
        this.key = key;
        this.price = price;
        this.unit = unit;
        this.vendor = vendor;
        this.image = image;
        this.count = count;
        this.cartCount = cartCount;
        this.quantity = quantity;
        this.selected = selected;
    }

    protected CartItem(Parcel in) {
        title = in.readString();
        price = in.readString();
        unit = in.readString();
        vendor = in.readString();
        key = in.readString();
        image = in.createStringArrayList();
        quantity = in.readString();
        selected = in.readByte() != 0;
        count = in.readInt();
        cartCount = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(unit);
        dest.writeString(vendor);
        dest.writeString(key);
        dest.writeStringList(image);
        dest.writeString(quantity);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(count);
        dest.writeInt(cartCount);
    }
}
