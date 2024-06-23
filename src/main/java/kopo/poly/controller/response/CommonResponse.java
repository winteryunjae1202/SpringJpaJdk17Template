package kopo.poly.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@Getter
@Setter
    // 제네릭타입으로 객체 받기 최상위 객체인 오브젝트를 사용해도 되나 장단점이기에 사용
public class CommonResponse<T> {

    private HttpStatus httpStatus;
    private String message;
    private T data;


    // 메시지 구조 만들기
    @Builder
    public CommonResponse(HttpStatus httpStatus, String message, T data) {
        this.httpStatus =httpStatus;
        this.message = message;
        this.data = data;
    }

    // API 요청에 대한 Http 상태 정보 포함하여 응답 메시지 생성
    public static <T> CommonResponse<T> of(HttpStatus httpStatus, String message, T data) {
        return new CommonResponse<>(httpStatus, message, data);
    }

    public static ResponseEntity<CommonResponse> getErrors(BindingResult bindingResult){
        return ResponseEntity.badRequest()
                .body(CommonResponse.of(HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.series().name()
                                ,bindingResult.getAllErrors()));
    }

}
