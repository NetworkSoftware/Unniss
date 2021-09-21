package pro.network.unniss.product;

import java.io.Serializable;

public class Size implements Serializable {

    String size,size_price,id;


    public Size() {
    }

    public Size(String id,String size, String price) {
        this.id=id;
        this.size=size;
        this.size_price=price;
    }


    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize_price() {
        return size_price;
    }

    public void setSize_price(String size_price) {
        this.size_price = size_price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
