package org.example.mindlab.global.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.mindlab.global.exception.MindLabException;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    INVALID_IMAGE_EXTENSION(ErrorStatus.BAD_REQUEST, "이미지 확장자가 올바르지 않습니다.", 1),

    PASSWORD_MISMATCH(ErrorStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.", 1),
    TOKEN_EXPIRED(ErrorStatus.UNAUTHORIZED, "토큰이 만료되었습니다.", 2),
    INVALID_TOKEN(ErrorStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다.", 3),

    POST_DELETE_FORBIDDEN(ErrorStatus.FORBIDDEN, "삭제 권한이 없습니다.", 1),
    POST_UPDATE_FORBIDDEN(ErrorStatus.FORBIDDEN, "수정 권한이 없습니다.", 1),

    USER_NOT_FOUND(ErrorStatus.NOT_FOUND, "유저를 찾지 못했습니다.", 1),
    POST_NOT_FOUND(ErrorStatus.NOT_FOUND, "게시물을 찾지 못했습니다.", 2),

    EMAIL_ALREADY_EXIST(ErrorStatus.CONFLICT, "이메일이 중복됩니다.", 1),
    ALREADY_LIKED(ErrorStatus.CONFLICT, "이미 좋아요를 눌렀습니다.", 2),

    INTERNAL_SERVER_ERROR(ErrorStatus.INTERNAL_SERVER_ERROR, "서버 에러", 1),

    CLIENT_CONNECT_FAILED(ErrorStatus.SERVICE_UNAVAILABLE, "서비스 통신 오류", 1);

    private final int status;

    private final String message;

    private final int sequence;

    public MindLabException throwException() {
        return new MindLabException(this);
    }
}
