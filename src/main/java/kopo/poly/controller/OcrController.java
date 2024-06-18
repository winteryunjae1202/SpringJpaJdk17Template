package kopo.poly.controller;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
public class OcrController {

    @Autowired
    private Tesseract tesseract;

    @PostMapping("/api/upload")
    public ModelAndView uploadImage(@RequestParam("file") MultipartFile file, Model model) {

        log.info(this.getClass().getName() + ".uploadImage Start!");

        ModelAndView modelAndView = new ModelAndView();

        try {
            // MultipartFile을 File 객체로 변환
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            // 이미지에서 텍스트 추출
            String extractedText = tesseract.doOCR(convFile);
            log.info("extractedText : " + extractedText);

            // 추출된 텍스트를 모델에 추가
            model.addAttribute("extractedText", extractedText);

            // 뷰 이름 설정
            modelAndView.setViewName("allergy/searchItemResult"); // searchItemResult.html에 대한 뷰 이름

        } catch (IOException | TesseractException e) {
            e.printStackTrace();

            modelAndView.setViewName("error");
            modelAndView.addObject("errorMessage", "이미지 처리 중 오류가 발생했습니다."); // 오류 메시지 전달
        }

        log.info(this.getClass().getName() + ".uploadImage End!");

        return modelAndView;
    }
}
