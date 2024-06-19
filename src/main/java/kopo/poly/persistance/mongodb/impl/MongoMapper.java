package kopo.poly.persistance.mongodb.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import kopo.poly.dto.MongoDTO;
import kopo.poly.persistance.mongodb.AbstractMongoDBComon;
import kopo.poly.persistance.mongodb.IMongoMapper;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class MongoMapper extends AbstractMongoDBComon implements IMongoMapper {

    private final MongoTemplate mongodb;


    @Override
    public int insertData(MongoDTO pDTO, String colNm) throws Exception {
            log.info(this.getClass().getName() + ".insertData Start!");

            int res = 0;

            // 데이터를 저장할 컬렉션 생성
            super.createCollection(mongodb, colNm);

            // 저장할 컬렉션 객체 생성
            MongoCollection<Document> col = mongodb.getCollection(colNm);

            // DTO를 Map 데이터타입으로 변경 한 뒤, 변경된 Map 데이터타입을 Document로 변경하기
            col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));

            res = 1;

            log.info(this.getClass().getName() + ".insertData End!");

            return res;
    }

    @Override
    public int existsYn(MongoDTO pDTO, String colNm) throws Exception {

        int res = 1;

        String userId = pDTO.userId();
        String prdlstReportNo = pDTO.prdlstReportNo();

        log.info("userId : " + userId);
        log.info("prdlstReportNo : " + prdlstReportNo);

        Document query = new Document();

        query.append("prdlstReportNo", prdlstReportNo);
        query.append("userId", userId);

        Document projection = new Document();

        projection.append("prdlstReportNo", "$prdlstReportNo");
        projection.append("userId", "$userId");

        // 컬렉션 이름이랑 같은 db 데이터 가져오기
        MongoCollection<Document> col = mongodb.getCollection(colNm);
        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs) {
            if (doc != null) {
                res = 2; // 데이터 존재
                break;
            }
        }

        return res;

    }

    @Override
    public List<MongoDTO> idexistsYn(MongoDTO pDTO, String colNm) throws Exception {

        List<MongoDTO> rList = new LinkedList<>();

        int res = 1;

        String userId = pDTO.userId();

        log.info("userId : " + userId);

        Document query = new Document();
        query.append("userId", userId);

        Document projection = new Document();
        projection.append("userId", "$userId");
        projection.append("prdlstReportNo", "$prdlstReportNo");
        projection.append("prdlstNm", "$prdlstNm");
        projection.append("allergy", "$allergy");
        projection.append("result", "$result");

        // 컬렉션 이름이랑 같은 db 데이터 가져오기
        MongoCollection<Document> col = mongodb.getCollection(colNm);
        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs){
            String prdlstReportNo = CmmUtil.nvl(doc.getString("prdlstReportNo"));
            String prdlstNm = CmmUtil.nvl(doc.getString("prdlstNm"));
            String allergy = CmmUtil.nvl(doc.getString("allergy"));
            String result = CmmUtil.nvl(doc.getString("result"));

            log.info("prdlstReportNo : " + prdlstReportNo);
            log.info("prdlstNm : "  + prdlstNm);
            log.info("allergy : " + allergy);
            log.info("result : " + result);

            pDTO = MongoDTO.builder().userId(userId).prdlstReportNo(prdlstReportNo).prdlstNm(prdlstNm)
                    .allergy(allergy).result(result).build();

            rList.add(pDTO);
        }

        return rList;
    }
}