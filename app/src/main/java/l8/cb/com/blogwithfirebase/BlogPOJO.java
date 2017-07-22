package l8.cb.com.blogwithfirebase;

/**
 * Created by ip510 feih on 21-07-2017.
 */

public class BlogPOJO {

    String title, post;
    String imageUri;

    public BlogPOJO() {

    }

    public BlogPOJO(String title, String post, String image) {
        this.title = title;
        this.post = post;
        this.imageUri = image;
    }

    public String getImageUri() {

        return imageUri;
    }

    public void setImageUri(String imageUri) {

        this.imageUri = imageUri;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPost() {

        return post;
    }

    public void setPost(String post) {

        this.post = post;
    }
}
