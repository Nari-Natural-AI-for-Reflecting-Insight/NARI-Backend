package com.naribackend.support;

import org.springframework.restdocs.payload.FieldDescriptor;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ApiResponseDocs {

    /**
     * 성공 응답 필드 설명
     *
     * @return 성공 응답 필드 설명 리스트
     */
    public static List<FieldDescriptor> SUCCESS_FIELDS() {
        return List.of(
                fieldWithPath("result").description("응답 결과 상태"),
                fieldWithPath("data").description("성공 시 반환 데이터").optional(),
                fieldWithPath("error").description("오류 정보").optional()
        );
    }

    /**
     * 성공 응답 필드 설명을 추가하는 메서드
     *
     * @param additionalFields 추가할 필드 설명
     * @return 성공 응답 필드 설명 리스트
     */
    public static List<FieldDescriptor> SUCCESS_FIELDS(FieldDescriptor ... additionalFields) {
        var success_fields = new ArrayList<>(SUCCESS_FIELDS());
        success_fields.addAll(List.of(additionalFields));

        return success_fields;
    }


    /**
     * 오류 응답 필드 설명
     *
     * @return 오류 응답 필드 설명 리스트
     */
    public static List<FieldDescriptor> ERROR_FIELDS() {
        return List.of(
                fieldWithPath("result").description("응답 결과 상태"),
                fieldWithPath("data").description("성공 시 반환 데이터").optional(),
                fieldWithPath("error.code").description("오류 코드"),
                fieldWithPath("error.message").description("오류 메시지"),
                fieldWithPath("error.data").description("오류 데이터").optional()
        );
    }

}
