package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record DataDTO (
        String prdlstReportNo,  // 품목보고번호

        String prdlstNm,        // 제품명

        String allergy          // 알레르기 유발물질
){

}
