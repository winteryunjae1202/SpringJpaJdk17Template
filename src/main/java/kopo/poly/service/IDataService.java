package kopo.poly.service;

import kopo.poly.dto.DataDTO;

public interface IDataService {

    String apiURL = "https://apis.data.go.kr/B553748/CertImgListServiceV3/getCertImgListServiceV3";

    DataDTO getProductApiList(String data) throws Exception;

}
