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
//package kopo.poly.controller;//package kopo.poly.controller;
//
//
//import kopo.poly.dto.OcrDTO;
//import kopo.poly.service.IOcrService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/api/card-info")
//@RequiredArgsConstructor
//@Slf4j
//public class OcrController {
//
//    private final IOcrService ocrService;
//
//    @PostMapping(value = "/upload")
//    @ResponseBody
//    public ResponseEntity<String> upload(@RequestParam(value = "cardImage") MultipartFile mf, ModelMap model)
//            throws Exception {
//
//        OcrDTO rDTO = null;
//        try {
//
//            log.info("받은 파일 : " + mf);
//            String fileSeq = "1";
//
//            BufferedImage originalImage = loadImageFromUrl(mf);
//            log.info("BufferedImage : " + originalImage);
//
////            BufferedImage highResolutionImage = resizeImageWithHigherResolution(originalImage, 1200,
////                    1800); // 예시 해상도: 1200x1800
//
//            rDTO = ocrService.cardOcr(originalImage);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ResponseEntity.ok(rDTO.getPrdlstReportNo());
//
//    }
//
//    public static BufferedImage loadImageFromUrl(MultipartFile image) throws IOException {
//        BufferedImage file = ImageIO.read(image.getInputStream());
//        return file;
//    }
//
//
//
//}
