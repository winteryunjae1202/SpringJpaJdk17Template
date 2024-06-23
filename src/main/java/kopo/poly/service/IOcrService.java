package kopo.poly.service;

import kopo.poly.dto.OcrDTO;

import java.awt.image.BufferedImage;

public interface IOcrService {
    // OCR 카드 정보 가져오기
    OcrDTO cardOcr(BufferedImage highResolutionImage) throws Exception;
}
