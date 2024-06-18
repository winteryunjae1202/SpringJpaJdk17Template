package kopo.poly.dto;

import lombok.Builder;

@Builder
public record CommentDTO(

        Long commentSeq,
        Long noticeSeq,
        String userId,
        String commentContents,
        String commentRegId,
        String commentRegDt,
        String commentChgId,
        String commentChgDt

) {
}