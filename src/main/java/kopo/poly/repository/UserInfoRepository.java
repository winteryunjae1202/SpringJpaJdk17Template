package kopo.poly.repository;

import kopo.poly.repository.entity.UserInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

    Optional<UserInfoEntity> findByEmail(String email);
}
