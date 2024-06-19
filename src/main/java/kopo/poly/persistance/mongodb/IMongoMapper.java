package kopo.poly.persistance.mongodb;

import kopo.poly.dto.MongoDTO;

import java.util.List;

public interface IMongoMapper {

    int insertData(MongoDTO pDTO, String colNm) throws Exception;

    int existsYn(MongoDTO pDTO, String ColNm) throws Exception;

    List<MongoDTO> idexistsYn(MongoDTO pDTO, String ColNm) throws Exception;
}
