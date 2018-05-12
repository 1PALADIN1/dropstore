import javafx.scene.image.ImageView;

public class ListItem {
    private ImageView imageView;
    private String id;
    private String name;
    private String type;
    private String parentId;

    public ListItem(String id, String name, String type, String parentId, ImageView imageView) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
        this.imageView = imageView;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getParentId() {
        return parentId;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
