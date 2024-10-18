package kopo.poly.service;
import kopo.poly.dto.LikeDTO;

public interface ILikeService {

    /**
     *  좋아요를 했는지 확인
     *
     * @param pDTO 좋아요 확인을 위한 정보
     */
    LikeDTO likeExists(LikeDTO pDTO);

    /**
     * 좋아요 추가하기
     *
     * @param pDTO 좋아요 추가하기 위한 정보
     */
    void insertLike(LikeDTO pDTO) throws Exception;

    /**
     * 좋아요 삭제하기
     *
     * @param pDTO 좋아요 삭제하기 위한 정보
     */
    void deleteLike(LikeDTO pDTO) throws Exception;
}