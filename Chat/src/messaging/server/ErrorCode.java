package messaging.server;

public enum ErrorCode {
    PORT_ASSIGN_ERROR(100),
    SERVER_START_ERROR(1000);
    
    private final int code;
    ErrorCode(int code) {
        this.code = code;
    }
    
    public int getCode() {return this.code;}
}
