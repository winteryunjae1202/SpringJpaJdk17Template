package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record YoutubeDTO(

        // 비디오 고유 ID
        String videoId,
        // 비디오 제목
        String videoTitle,
        // 비디오 날짜
        String videoPublished,
        // 상세 내용
        String description,
        // 채널 제목
        String channelTitle,
        // 채널 생성일
        String publishTime,
        // 썸네일 데이터 중간 크기 썸네일(w320, h180)
        String url

) {
}