

//package kopo.poly.service.impl;
//
//
//import kopo.poly.dto.OcrDTO;
//import kopo.poly.service.IOcrService;
//import kopo.poly.util.DateUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Base64;
//
//@RequiredArgsConstructor
//@Service
//@Slf4j
//public class OcrService implements IOcrService {
//
//    @Value("${naver.service.secretKey}")
//    private String ocrSecretKey;
//
//    @Value("${naver.service.url}")
//    private String ocrInvokeUrl;
//
//    private final RestTemplate restTemplate;
//    /* 여기다간 네가 사용할 매퍼 적어주면 되고 */
//
//    /* URL에서 이미지를 다운로드하고 Base64로 인코딩하는 메소드 */
//    public static String encodeImageToBase64(BufferedImage image, String formatName) throws IOException {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ImageIO.write(image, formatName, outputStream);
//        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
//    }
//
//    /* Naver Clova OCR 사용하여 영수증 스캔 */
//    @Override
//    public OcrDTO cardOcr(BufferedImage fileUrl) throws Exception {
//
//        // 파일 경로를 전달하여 이미지를 Base64로 인코딩
//        String base64Image = encodeImageToBase64(fileUrl, "png");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("X-OCR-SECRET", ocrSecretKey); // OCR 비밀 키 추가
//        headers.setContentType(MediaType.APPLICATION_JSON); // 콘텐츠 타입을 JSON으로 설정
//
//        // JSON 객체 생성
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("version", "V2"); // OCR API 버전 설정
//        jsonObject.put("requestId", "string"); // 요청 ID 설정
//        jsonObject.put("timestamp", 0); // 타임스탬프 설정
//
//        // 이미지 데이터를 위한 JSON 객체 생성
//        JSONObject data = new JSONObject();
//        data.put("format", "png"); // 이미지 형식 설정
//        data.put("data", base64Image); // Base64로 인코딩된 이미지 데이터 설정
//        data.put("name", "CardOcr" + DateUtil.getDateTime("HHmmss")); // 이미지 이름 설정 (시간 포함)
//
//        // JSON 배열에 이미지 데이터 추가
//        JSONArray img = new JSONArray();
//        img.put(data);
//        jsonObject.put("images", img);
//
//        log.info("Request Headers: " + headers); // 요청 헤더 로그 출력
//
//        // 요청 엔터티 생성 (JSON 본문과 헤더 포함)
//        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);
//
//        // RestTemplate 인터셉터 추가
//        restTemplate.getInterceptors().add((request, body, execution) -> {
//            ClientHttpResponse response = execution.execute(request, body);
//            response.getHeaders().setContentType(MediaType.APPLICATION_JSON); // 응답의 콘텐츠 타입을 JSON으로 설정
//            return response;
//        });
//
//        // OCR API 호출
//        ResponseEntity<String> response = restTemplate.exchange(ocrInvokeUrl, HttpMethod.POST, entity, String.class);
//
//        // 응답 상태가 OK가 아니면 예외 발생
//        if (response.getStatusCode() != HttpStatus.OK) {
//            throw new RuntimeException("Failed to get OCR result: " + response.getStatusCode());
//        }
//
//        log.info("Response Body: " + response.getBody());
//
//        JSONObject responseBody = new JSONObject(response.getBody());
//        JSONArray responseImages = responseBody.getJSONArray("images");
//        JSONObject firstImage = responseImages.getJSONObject(0);
//        JSONArray fields = firstImage.getJSONArray("fields");
//
//        String prdlstReportNo = null;
//
//        // 필드 배열을 순회하면서 "inferText" 값이 "·품목보고번호:" 인 항목을 찾음
//        for (int i = 0; i < fields.length(); i++) {
//            JSONObject field = fields.getJSONObject(i);
//            String inferText = field.getString("inferText");
//            if ("·품목보고번호:".equals(inferText)) {
//                // 해당 항목의 다음 인덱스에 있는 값이 품목보고번호 값임
//
//                JSONObject nextField = fields.getJSONObject(i + 1);
//                prdlstReportNo = nextField.getString("inferText");
//                break;
//            }
//        }
//
//        log.info("품목보고번호: " + prdlstReportNo);
//
//
//        OcrDTO rDTO = OcrDTO.builder().prdlstReportNo(prdlstReportNo).build();
//        log.info("품목보고번호: " + rDTO);
//
//        log.info(this.getClass().getName() + "[service] Naver Clova OCR 사용하여 스캔 종료");
//
//        return rDTO;
//    }
//
//}