package pro.network.unniss.random;

public class RandomBeen {

    String text;
    String  image;

    public RandomBeen(String text, String image) {
        this.text = text;
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
}
