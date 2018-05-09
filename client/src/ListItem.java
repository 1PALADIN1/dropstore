public class ListItem {
    private String id;
    private String name;
    private String type;
    private String parentId;

    public ListItem(String id, String name, String type, String parentId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
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
}