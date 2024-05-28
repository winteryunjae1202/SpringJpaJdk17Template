package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.DataDTO;
import kopo.poly.service.IDataService;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataService implements IDataService {

    @Value("${product.api.key}")
    private String apiKey;

    private Map<String, String> setProductInfo() {
        Map<String, String> requestHeader = new HashMap<>();
        requestHeader.put("accept", "application/xml;charset=UTF-8");
        requestHeader.put("Content-Type", "application/xml;charset=UTF-8");
        requestHeader.put("apikey", apiKey);

        return requestHeader;
    }

    /**
     * 데이터를 받아서 API 호출
     *
     * @param data API명
     *
     * @return API 호출 결과
     */
    public DataDTO getProductApiList(String data) throws Exception {

        log.info(this.getClass().getName() + ".getProductApiList Start!");

        String apiUrl = IDataService.apiURL;

        String apiParam = "?ServiceKey=" + apiKey + "&prdlstReportNo=" + data;

        log.info("apiParam : " + apiParam);

        String xml = NetworkUtil.get(apiUrl + apiParam, this.setProductInfo());

        log.info("xml : " + xml);

        // XML을 JSON으로 변환
        JSONObject jsonObject = XML.toJSONObject(xml);

        // JSONObject를 문자열로 변환
        String json = jsonObject.toString();

        // ObjectMapper를 사용하여 JSON 문자열 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> tMap = objectMapper.readValue(json.getBytes("UTF-8"), LinkedHashMap.class);

        log.info("tMap : " + tMap);

        // body 안에 items가 있는지 확인
        Map<String, Object> responseBody = (Map<String, Object>) ((Map<String, Object>) tMap.get("response")).get("body");
        Map<String, Object> items = (Map<String, Object>) responseBody.get("items");

        // item 가져오기
        Map<String, Object> item = (Map<String, Object>) items.get("item");


        // allergy와 prdlstNm 추출
        String allergy = new String(((String) item.get("allergy")));        // 알러지 유발물질
        String prdlstNm = new String(((String) item.get("prdlstNm")));        // 제품명

        log.info("allergy : " + allergy);
        log.info("prdlstNm : " + prdlstNm);

        DataDTO rDTO = DataDTO.builder().allergy(allergy).prdlstNm(prdlstNm).build();

        log.info(this.getClass().getName() + ".getProductApiList End!");

        return rDTO;
    }
}

