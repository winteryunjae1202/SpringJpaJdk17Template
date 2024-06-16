package kopo.poly.repository;

import kopo.poly.repository.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoEntity, String> {

    // 회원 존재 여부 체크
    // java.util.Optional 객체는 자바의 NullPointer 에러에 대응하기 위해 1.8버전부터 추가된 자바 객체
    // 객체에 값이 존재하는지 체크할 때 활용 가능한
    // 쿼리 예 : SELECT * FROM USER_INFO WHERE USER_ID = 'aaa'
    Optional<UserInfoEntity> findByUserId(String userId);

    // 로그인
    // 쿼리 예 : SELECT * FROM USER_INFO WHERE USER_ID = 'aaa' AND PASSWORD = '1234'
    Optional<UserInfoEntity> findByUserIdAndPassword(String userId, String password);

    // 이메일, 이름 바탕으로 정보 찾아오기
    Optional<UserInfoEntity> findByUserNameAndEmail(String userName, String email);

    // 이름, 아이디 바탕으로 정보 찾아오기
    Optional<UserInfoEntity> findByUserNameAndUserId(String userName, String userId);

    // 이메일 바탕으로 정보 찾아오기
    Optional<UserInfoEntity> findByEmail(String email);


    /**
     * 유저 정보 업데이트
     *
     * @param userId 유저정보 PK
     */

    /**
     *
     * @Query 네이티브 쿼리 사용시 DB값은 변경되나 Entity값(캐시값)은 변경 x
     * @Modifying(clearAutomatically = true) 사용해서 객체 정보 초기화(Entity 정보 삭제) 후 캐시 갱신
     *
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE USER_INFO A SET A.email = ?2, A.USER_NAME = ?3 WHERE A.USER_ID = ?1",
            nativeQuery = true)
    int updateUserInfo(String userId, String email, String userName, String allergy);

}

