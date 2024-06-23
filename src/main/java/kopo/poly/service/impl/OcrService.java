package kopo.poly.service.impl;


import kopo.poly.dto.OcrDTO;
import kopo.poly.service.IOcrService;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@RequiredArgsConstructor
@Service
@Slf4j
public class OcrService implements IOcrService {

    @Value("${naver.service.secretKey}")
    private String ocrSecretKey;

    @Value("${naver.service.url}")
    private String ocrInvokeUrl;

    private final RestTemplate restTemplate;
    /* 여기다간 네가 사용할 매퍼 적어주면 되고 */

    /* URL에서 이미지를 다운로드하고 Base64로 인코딩하는 메소드 */
    public static String encodeImageToBase64(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /* Naver Clova OCR 사용하여 영수증 스캔 */
    @Override
    public OcrDTO cardOcr(BufferedImage fileUrl) throws Exception {

        log.info(this.getClass().getName() + "[service] Naver OCR 사용하여 카드 정보 추출 실행");

        String base64Image = encodeImageToBase64(fileUrl, "png"); // 파일 경로를 전달하여 Base64로 인코딩

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-OCR-SECRET", ocrSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", "V2");
        jsonObject.put("requestId", "string");
        jsonObject.put("timestamp", 0);

        JSONObject data = new JSONObject();
        data.put("format", "png");
        data.put("data", base64Image);
        data.put("name", "CardOcr" + DateUtil.getDateTime("HHmmss")); //시간

        JSONArray img = new JSONArray();
        img.put(data); //todo add   썻었음
        jsonObject.put("images", img);
        log.info("Request Headers: " + headers);

        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response;
        });

        ResponseEntity<String> response = restTemplate.exchange(ocrInvokeUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to get OCR result: " + response.getStatusCode());
        }

        log.info("Response Body: " + response.getBody());

        JSONObject responseBody = new JSONObject(response.getBody());
        JSONArray responseImages = responseBody.getJSONArray("images");
        JSONObject firstImage = responseImages.getJSONObject(0);
        JSONArray fields = firstImage.getJSONArray("fields");

        String prdlstReportNo = null;

// 필드 배열을 순회하면서 "inferText" 값이 "·품목보고번호:" 인 항목을 찾음
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String inferText = field.getString("inferText");
            if ("·품목보고번호:".equals(inferText)) {
                // 해당 항목의 다음 인덱스에 있는 값이 품목보고번호 값임

                JSONObject nextField = fields.getJSONObject(i + 1);
                prdlstReportNo = nextField.getString("inferText");
                break;
            }
        }

        log.info("품목보고번호: " + prdlstReportNo);


            OcrDTO rDTO = OcrDTO.builder().prdlstReportNo(prdlstReportNo).build();
// getPrdlstReportNo
        log.info("품목보고번호: " + rDTO);
            log.info(this.getClass().getName() + "[service] Naver Clova OCR 사용하여 스캔 종료");

            return rDTO;
        }

    }