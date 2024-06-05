package kopo.poly.dto;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record FileDTO (
        MultipartFile file
){
}
