public class User {
    private String username;
    private String password;
    private boolean op;

    public User(String username, String password, boolean op) {
        this.username = username;
        this.password = password;
        this.op = op;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isOp() {
        return op;
    }
}
