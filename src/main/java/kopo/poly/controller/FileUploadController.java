package kopo.poly.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3 s3Client;
    private final String bucketName;

    /**
     * 게시글에 이미지 업로드
     */
    @PostMapping("/uploadNoticeImage")
    public ResponseEntity<?> uploadNoticeImage(@RequestParam("file") MultipartFile file, @RequestParam("noticeSeq") Long noticeSeq, HttpSession session) {
        log.info(this.getClass().getName() + ".uploadNoticeImage Start!");

        try {
            String userId = (String) session.getAttribute("SS_USER_ID");
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileName = "notices/" + userId + "_" + noticeSeq + "_" + UUID.randomUUID().toString() + "." + extension;

            // ObjectMetadata 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize()); // MultipartFile의 크기를 설정

            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            String imageUrl = s3Client.getUrl(bucketName, fileName).toString();

            return ResponseEntity.ok("게시글 이미지 등록에 성공했습니다.");
        } catch (Exception e) {
            log.error("게시글 이미지 등록에 실패했습니다.", e);
            return ResponseEntity.badRequest().body("게시글 이미지 등록에 실패했습니다.");
        }
    }


}