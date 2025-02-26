package gachonproject.mobile.api.em;

public enum ResponseCode {
    SUCCESS(200), // 성공
    BAD_REQUEST(400); // 잘못된 요청

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
