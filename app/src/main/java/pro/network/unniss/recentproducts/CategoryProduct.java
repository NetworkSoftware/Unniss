package pro.network.unniss.recentproducts;

import java.util.ArrayList;

import pro.network.unniss.product.ProductListBean;

public class CategoryProduct {
    String title;
    ArrayList<ProductListBean> productListBeans;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ProductListBean> getProductListBeans() {
        return productListBeans;
    }

    public void setProductListBeans(ArrayList<ProductListBean> productListBeans) {
        this.productListBeans = productListBeans;
    }
}
