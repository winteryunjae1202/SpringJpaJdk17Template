package kopo.poly.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TesseractConfig {

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata"); // tessdata 디렉토리의 경로 설정
        tesseract.setLanguage("kor"); // 한국어 언어 설정
        return tesseract;
    }
}