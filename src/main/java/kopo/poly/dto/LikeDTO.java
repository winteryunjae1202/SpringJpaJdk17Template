package kopo.poly.dto;

import lombok.Builder;

@Builder
public record LikeDTO(
        Long noticeSeq,
        String userId,
        String existsYn
) {
}