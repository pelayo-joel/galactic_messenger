package galactic.client;

public class User {
    private String username;
    private boolean isLoggedIn;

    // Constructor to create a user object with a username
    public User(String username) {
        this.username = username;
        this.isLoggedIn = false;
    }

    // Getter for the username
    public String getUsername() {
        return username;
    }

    // Method to set the user's login status
    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    // Method to check if the user is currently logged in
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}