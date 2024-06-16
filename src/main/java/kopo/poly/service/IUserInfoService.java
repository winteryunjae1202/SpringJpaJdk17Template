package kopo.poly.service;

import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;

public interface IUserInfoService {

    /**
     * 아이디 중복 체크
     *
     * @param pDTO 회원 가입을 위한 아이디
     * @return 아이디 중복 여부 결과
     */
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    /**
     * 회원 가입하기(회원정보 등록하기)
     *
     * @param pDTO 회원 가입을 위한 회원정보
     * @return 회원가입 결과
     */
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    /**
     * 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
     *
     * @param pDTO 로그인을 위한 회원정보
     * @return 회원가입 결과
     */
    int getUserLogin(UserInfoDTO pDTO) throws Exception;


    /**
     * 아이디 찾기용 이메일 조회
     */
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    /**
     * 비밀번호 찾기용 아이디, 이름 조회
     */
    MsgDTO getUserNameExists(UserInfoDTO pDTO) throws Exception;

    /**
     * 이메일 보내기
     */
    UserInfoDTO getEmailSend(UserInfoDTO pDTO) throws Exception;

    /**
     * 이메일 조회
     */
    UserInfoDTO getEmailOnlyExists(UserInfoDTO pDTO) throws Exception;

    /**
     * 유저 정보 조회
     */
    UserInfoDTO getUserInfo(String userId) throws Exception;

    /**
     * 회원 정보 수정
     */
    int updateUserInfo(UserInfoDTO pDTO) throws Exception;

    /**
     * 비밀번호 수정
     */
    int updatePassword(UserInfoDTO pDTO) throws Exception;

    /**
     * 회원 탈퇴
     */
    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;

}
