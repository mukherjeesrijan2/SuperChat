package messaging.server;

class Authorizer {
    // TODO: validate user from db
    
    // dummy method for testing
    public static boolean authorize(String username, String password) {
        if (password == null || password.isEmpty()) return false;
        return true;
    }
}
