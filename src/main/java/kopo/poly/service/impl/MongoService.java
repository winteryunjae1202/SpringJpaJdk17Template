package kopo.poly.service.impl;

import kopo.poly.dto.MongoDTO;
import kopo.poly.persistance.mongodb.IMongoMapper;
import kopo.poly.service.IMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MongoService implements IMongoService {

    private final IMongoMapper mongoMapper;

    @Override
    public int mongoTest(MongoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".mongotest start");

        String colNm = "Search_History";

        int res = existsYn(pDTO);

        if(res != 2) {
            res = mongoMapper.insertData(pDTO, colNm);
        }

        log.info(this.getClass().getName() + ".mongotest end!");

        return res;
    }

    @Override
    public int existsYn(MongoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".existsYn Start!");

        String colNm = "Search_History";

        int res = mongoMapper.existsYn(pDTO, colNm);

        log.info(this.getClass().getName() + ".existsYn End!");

        return res;
    }

    @Override
    public List<MongoDTO> selectData(MongoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".selectData Start!");

        String colNm = "Search_History";

        List<MongoDTO> rList = mongoMapper.idexistsYn(pDTO, colNm);

        log.info(this.getClass().getName() + ".selectData End!");

        return rList;
    }
}
