package kopo.poly.repository;

import kopo.poly.repository.entity.CommentEntity;
import kopo.poly.repository.entity.CommentPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, CommentPK> {

    /*
     *  CrudRepository
     *  단순 CRUD 구현
     *
     *  JpaRepository
     *  조회할 때, 정렬, 페이징 기능 등 사용
     *  왠만해선 JpaRepository 사용 !!
     *
     *  JpaRepository<CommentEntity, Long>
     *  JpaRepository<엔티티명, PK타입>
     *
     */


    /**
     * 댓글 리스트
     *
     * @param noticeSeq 댓글 FK
     *
     * @return noticeSeq 일치하는 댓글 데이터 리스트
     *
     */
    List<CommentEntity> findByNoticeSeqOrderByCommentSeqAsc(Long noticeSeq);

    /**
     * 게시글 댓글 유무 확인
     *
     * @param noticeSeq 댓글 PK
     */
    @Transactional(readOnly = true)
    @Query(value = "SELECT COALESCE(MAX(COMMENT_SEQ), 0)+1 FROM BOARD_COMMENTS WHERE NOTICE_SEQ = ?1",
            nativeQuery = true)
    Long getMaxCommentsSeq(Long noticeSeq);

/*

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE BOARD_COMMENTS A SET A.COMMENT_CONTENTS = ?2, A.COMMENT_CHG_ID = ?3, A.COMMENT_CHG_DT = ?4 WHERE A.COMMENT_SEQ = ?1",
            nativeQuery = true)
    int updateBoardComments(Long commentSeq, String commentContents, String commentChgId, String commentChgDt);
*/






}