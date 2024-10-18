package kopo.poly.service.impl;

import kopo.poly.dto.LikeDTO;
import kopo.poly.repository.LikeRepository;
import kopo.poly.repository.entity.LikeEntity;
import kopo.poly.repository.entity.LikePK;
import kopo.poly.service.ILikeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
@Service
public class LikeService implements ILikeService {

    private final LikeRepository likeRepository;


    /**
     * 좋아요 여부 확인
     */
    @Override
    public LikeDTO likeExists(LikeDTO pDTO) {

        log.info(this.getClass().getName() + ".likeExists Start!");

        LikeDTO rDTO;

        String userId = CmmUtil.nvl(pDTO.userId());
        long noticeSeq = pDTO.noticeSeq();

        log.info("userId : " + userId);
        log.info("noticeSeq : " + noticeSeq);

        LikePK likePK = LikePK.builder()
                .noticeSeq(noticeSeq)
                .userId(userId)
                .build();

        Optional<LikeEntity> rEntity = likeRepository.findById(likePK);

        if (rEntity.isPresent()) {
            rDTO = LikeDTO.builder().existsYn("Y").build();
        } else {
            rDTO = LikeDTO.builder().existsYn("N").build();
        }

        log.info(this.getClass().getName() + ".likeExists End!");

        return rDTO;
    }


    /**
     * 좋아요 추가
     */
    @Override
    public void insertLike(LikeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertLike Start!");

        String userId = CmmUtil.nvl(pDTO.userId());
        Long noticeSeq = pDTO.noticeSeq();

        log.info("userId : " + userId);
        log.info("noticeSeq : " + noticeSeq);

        LikeEntity pEntity = LikeEntity.builder()
                .noticeSeq(noticeSeq)
                .userId(userId)
                .build();

        likeRepository.save(pEntity);

        log.info(this.getClass().getName() + ".insertLike End!");

    }


    /**
     * 좋아요 삭제
     */
    @Override
    public void deleteLike(LikeDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteLike Start!");

        String userId = CmmUtil.nvl(pDTO.userId());
        Long noticeSeq = pDTO.noticeSeq();

        log.info("userId : " + userId);
        log.info("noticeSeq : " + noticeSeq);

        LikePK likePK = LikePK.builder()
                .noticeSeq(noticeSeq)
                .userId(userId)
                .build();

        // 좋아요 삭제하기
        likeRepository.deleteById(likePK);

        log.info(this.getClass().getName() + ".deleteLike End!");
    }
}
