package pro.network.unniss.wishlist;

import java.util.ArrayList;

import pro.network.unniss.product.ProductListBean;

public class WishListBean {
    ArrayList<ProductListBean> data;

    public WishListBean(ArrayList<ProductListBean> data) {
        this.data = data;
    }


    public ArrayList<ProductListBean> getData() {
        return data;
    }

    public void setData(ArrayList<ProductListBean> data) {
        this.data = data;
    }
}