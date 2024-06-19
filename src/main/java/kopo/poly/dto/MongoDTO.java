package kopo.poly.dto;

import lombok.Builder;

@Builder
public record MongoDTO (

        String userId,  // 유저아이디
        String prdlstNm,    // 제품명
        String prdlstReportNo,  // 품목보고번호
        String allergy,         // 알러지
        String result         // 알러지 유무
){
}
