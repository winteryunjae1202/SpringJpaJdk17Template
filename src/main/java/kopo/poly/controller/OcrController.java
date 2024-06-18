package kopo.poly.controller;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Controller
public class OcrController {

    @Autowired
    private Tesseract tesseract;

    @PostMapping("/api/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, Model model) {

        log.info(this.getClass().getName() + ".uploadImage Start!");

        String fourteenDigits = "";

        try {
            // MultipartFile을 File 객체로 변환
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            // 이미지에서 텍스트 추출
            String extractedText = tesseract.doOCR(convFile);

            // 정규 표현식을 사용하여 숫자 추출
            Pattern pattern = Pattern.compile("\\d{14}");
            Matcher matcher = pattern.matcher(extractedText);


            // 14자리의 연속된 숫자 찾기
            if (matcher.find()) {
                fourteenDigits = matcher.group();
                log.info("14자리 연속된 숫자: " + fourteenDigits);

            } else {
                log.info("품목보고번호를 인식하지 못했습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("품목보고번호를 인식하지 못했습니다.");
            }


        } catch (IOException | TesseractException e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }

        log.info(this.getClass().getName() + ".uploadImage End!");

        return ResponseEntity.ok(fourteenDigits);
    }
}