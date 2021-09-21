package pro.network.unniss.random;

public class RandomBeen {

    String text;
    String id;
    String  image;

    public RandomBeen(String text, String image,String id) {
        this.text = text;
        this.id=id;
        this.image = image;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}