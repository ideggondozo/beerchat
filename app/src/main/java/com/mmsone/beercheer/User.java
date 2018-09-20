package  com.mmsone.beercheer;

public class User {

    private String name;
    private String profession;
    private String UID;
    boolean selected;

    public User(String name, String profession, String uid) {
        this.name = name;
        this.profession = profession;
        this.UID = uid;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
