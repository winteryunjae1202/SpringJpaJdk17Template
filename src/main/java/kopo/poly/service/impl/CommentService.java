package kopo.poly.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.CommentDTO;
import kopo.poly.repository.CommentRepository;
import kopo.poly.repository.entity.CommentEntity;
import kopo.poly.repository.entity.CommentPK;
import kopo.poly.service.ICommentService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;


    @Override
    public List<CommentDTO> getCommentList(CommentDTO pDTO) {

        log.info(this.getClass().getName() + ".getCommentList Start!");

        Long noticeSeq = pDTO.noticeSeq();

        log.info("noticeSeq : " + noticeSeq);

        List<CommentEntity> rList = commentRepository.findByNoticeSeqOrderByCommentSeqAsc(noticeSeq);

        List<CommentDTO> nList = new ObjectMapper().convertValue(rList,
                new TypeReference<>() {
                });

        log.info(this.getClass().getName() + ".getCommentList End!");

        return nList;
    }

    @Override
    public int updateComment(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateComment Start!");

        Long noticeSeq = pDTO.noticeSeq();
        Long commentSeq = pDTO.commentSeq();

        CommentPK commentPK = CommentPK.builder()
                .noticeSeq(noticeSeq)
                .commentSeq(commentSeq)
                .build();

        int res = 0;

        Optional<CommentEntity> rEntity = commentRepository.findById(commentPK);

        if(rEntity.isPresent()){

            String userId = pDTO.userId();
            String commentContents = pDTO.commentContents();
            String commentChgId = pDTO.commentChgId();
            String commentChgDt = pDTO.commentChgDt();
            String regId = rEntity.get().getCommentRegId();
            String regDt = rEntity.get().getCommentRegDt();

            log.info("noticeSeq : " + noticeSeq);
            log.info("commentSeq : " + commentSeq);
            log.info("userId : " + userId);
            log.info("commentContents : " + commentContents);
            log.info("commentChgId : " + commentChgId);
            log.info("commentChgDt : " + commentChgDt);

            // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
            // JPA에 자동 증가 설정을 해놨음
            CommentEntity pEntity = CommentEntity.builder()
                    .noticeSeq(noticeSeq)
                    .commentSeq(commentSeq)
                    .userId(userId)
                    .commentContents(commentContents)
                    .commentRegId(regId)
                    .commentRegDt(regDt)
                    .commentChgId(commentChgId)
                    .commentChgDt(commentChgDt)
                    .build();

            // 댓글 내용 DB에 저장 Update
            commentRepository.save(pEntity);


            // commentRepository.updateBoardComments(commentSeq, commentContents, commentChgId, commentChgDt);

            res = 1;

        }

        log.info(this.getClass().getName() + ".updateComment END!");

        return res;


    }

    @Override
    public void deleteComment(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteComment Start!");

        Long noticeSeq = pDTO.noticeSeq();
        Long commentSeq = pDTO.commentSeq();

        log.info("noticeSeq : " + noticeSeq);
        log.info("commentSeq : " + commentSeq);

        CommentPK commentPK = CommentPK.builder()
                .noticeSeq(noticeSeq)
                .commentSeq(commentSeq)
                .build();

        // 데이터 삭제하기
        commentRepository.deleteById(commentPK);

        log.info(this.getClass().getName() + ".deleteComment End!");

    }

    @Override
    public void insertComment(CommentDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertComment Start!");

        String userId = CmmUtil.nvl(pDTO.userId());
        String contents = CmmUtil.nvl(pDTO.commentContents());
        Long noticeSeq = pDTO.noticeSeq();
        Long commentSeq = commentRepository.getMaxCommentsSeq(noticeSeq);

        log.info("userId : " + userId);
        log.info("contents : " + contents);
        log.info("noticeSeq : " + noticeSeq);
        log.info("commentSeq : " + commentSeq);

        // 공지사항 저장을 위해서는 PK 값은 빌더에 추가하지 않는다.
        // JPA에 자동 증가 설정을 해놨음
        CommentEntity pEntity = CommentEntity.builder()
                .noticeSeq(noticeSeq)
                .commentSeq(commentSeq)
                .userId(userId)
                .commentContents(contents)
                .commentRegId(userId)
                .commentRegDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .commentChgId(userId)
                .commentChgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                .build();

        // 공지사항 저장하기
        commentRepository.save(pEntity);

        log.info(this.getClass().getName() + ".insertComment End!");

    }

}