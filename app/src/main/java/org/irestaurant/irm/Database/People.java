package org.irestaurant.irm.Database;

public class People extends PeopleId {
    String name, email, position, token_id, image;

    public People() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getToken_id() { return token_id; }

    public void setToken_id(String token_id) { this.token_id = token_id; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }
}
