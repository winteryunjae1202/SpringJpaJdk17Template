package kopo.poly.service;

import kopo.poly.dto.MongoDTO;

import java.util.List;

public interface IMongoService {

    int mongoTest(MongoDTO pDTO) throws Exception;

    int existsYn(MongoDTO pDTO) throws Exception;

    List<MongoDTO> selectData(MongoDTO pDTO) throws Exception;
}
