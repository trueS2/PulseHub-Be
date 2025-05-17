package pulsehub.pulsehubbe.global.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 400 Bad Request
     */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 사용자 조회 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "GitHub 사용자 정보를 찾을 수 없습니다."),

    /**
     * 500 Internal Server Error
      */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String description;

    public int getStatus() {
        return httpStatus.value();
    }
}
