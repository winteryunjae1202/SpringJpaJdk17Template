//
//import lombok.extern.slf4j.Slf4j;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.*;
//import java.net.URL;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Slf4j
//@Controller
//public class OcrController {
//
//    private static final String RELATIVE_PATH = "/uploads/";
//
//    @Value("${tess4j.datapath}")
//    private String tessDataPath;
//
//    @Autowired
//    private Tesseract tesseract;
//
//    @PostMapping("/api/upload")
//    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, Model model){
//
//        log.info(this.getClass().getName() + ".uploadImage Start!");
//
//        String fourteenDigits = "";
//
//        try {
//
//            File convFile = downloadFile("https://orogno24buckets3.s3.ap-northeast-2.amazonaws.com/notices/orogno24_63_a122e623-7f44-4138-88c4-dce5618d14ec.jpg");
////            file.transferTo(convFile);
//
//            // 이미지에서 텍스트 추출
//            String extractedText = tesseract.doOCR(convFile);
//
//            // 정규 표현식을 사용하여 숫자 추출
//            Pattern pattern = Pattern.compile("\\b\\d{12,15}\\b");
//            Matcher matcher = pattern.matcher(extractedText);
//
//
//            // 14자리의 연속된 숫자 찾기
//            if (matcher.find()) {
//                fourteenDigits = matcher.group();
//                log.info("품목보고번호: " + fourteenDigits);
//
//            } else {
//                log.info("품목보고번호를 인식하지 못했습니다.");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("품목보고번호를 인식하지 못했습니다.");
//            }
//
//            // 파일 삭제
////            if (convFile.delete()) {
////                log.info("File deleted successfully: " + finalPath);
////            } else {
////                log.info("Failed to delete file: " + finalPath);
////            }
//
//
//        } catch (IOException | TesseractException e) {
//            e.printStackTrace();
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
//        }
//
//        log.info(this.getClass().getName() + ".uploadImage End!");
//
//        return ResponseEntity.ok(fourteenDigits);
//    }
//
//    private File downloadFile(String fileUrl) throws IOException {
//        URL url = new URL(fileUrl);
//        InputStream inputStream = url.openStream();
//        String tempDir = System.getProperty("java.io.tmpdir");
//        File tempFile = new File(tempDir + File.separator + "downloadedFile.jpg");
//
//        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, length);
//            }
//        }
//
//        return tempFile;
//    }
//}
package kopo.poly.controller;//package kopo.poly.controller;


import kopo.poly.dto.OcrDTO;
import kopo.poly.service.IOcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api/card-info")
@RequiredArgsConstructor
@Slf4j
public class OcrController {

    private final IOcrService ocrService;

    @PostMapping(value = "/upload")
    @ResponseBody
    public ResponseEntity<String> upload(@RequestParam(value = "cardImage") MultipartFile mf, ModelMap model)
            throws Exception {

        OcrDTO rDTO = null;
        try {

            log.info("받은 파일 : " + mf);
            String fileSeq = "1";

            BufferedImage originalImage = loadImageFromUrl(mf);
            log.info("BufferedImage : " + originalImage);

//            BufferedImage highResolutionImage = resizeImageWithHigherResolution(originalImage, 1200,
//                    1800); // 예시 해상도: 1200x1800

            rDTO = ocrService.cardOcr(originalImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(rDTO.getPrdlstReportNo());

    }

    public static BufferedImage loadImageFromUrl(MultipartFile image) throws IOException {
        BufferedImage file = ImageIO.read(image.getInputStream());
        return file;
    }



}
