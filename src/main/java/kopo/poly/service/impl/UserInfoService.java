package kopo.poly.service.impl;

import jakarta.transaction.Transactional;
import kopo.poly.dto.MailDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.repository.UserInfoRepository;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.service.IMailService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserInfoService implements IUserInfoService {

    // RequiredArgsConstructor 어노테이션으로 생성자를 자동 생성함
    // userInfoRepository 변수에 이미 메모리에 올라간 UserInfoRepository 객체를 넣어줌
    // 예전에는 autowired 어노테이션을 통해 설정했었지만, 이젠 생성자를 통해 객체 주입함
    private final UserInfoRepository userInfoRepository;
    private final IMailService mailService;

    @Override
    public UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        UserInfoDTO rDTO;

        String userId = CmmUtil.nvl(pDTO.userId());

        log.info("userId : " + userId);

        // 회원 가입 중복 방지를 위해 DB에서 데이터 조회
        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        // 값이 존재한다면... (이미 회원가입된 아이디)
        if (rEntity.isPresent()) {
            rDTO = UserInfoDTO.builder().existsYn("Y").build();
        } else {
            rDTO = UserInfoDTO.builder().existsYn("N").build();
        }

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    @Override
    public int insertUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        // 회원가입 성공 : 1, 아이디 중복으로인한 가입 취소 : 2, 기타 에러 발생 : 0
        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String userName = CmmUtil.nvl(pDTO.userName());
        String password = CmmUtil.nvl(pDTO.password());
        String email = CmmUtil.nvl(pDTO.email());
        String allergy = CmmUtil.nvl(pDTO.allergy());

        log.info("pDTO : " + pDTO);

        // 회원 가입 중복 방지를 위해 DB에서 데이터 조회
        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        // 값이 존재한다면... (이미 회원가입된 아이디)
        if (rEntity.isPresent()) {
            res = 2;

        } else {

            // 회원가입을 위한 Entity 생성
            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(userId).userName(userName)
                    .password(password)
                    .email(email)
                    .allergy(allergy)
                    .regId(userId).regDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .build();

            // 회원정보 DB에 저장
            userInfoRepository.save(pEntity);

            // JPA의 save 함수는 데이터 값에 따라 등록, 수정을 수행함
            // 물론 잘 저장되겠지만, 내가 실행한 save 함수가 DB에 등록이 잘 수행되었는지 100% 확신이 불가능함
            // 회원 가입후, 혹시 저장 안될 수 있기에 조회 수행함
            // 회원 가입 중복 방지를 위해 DB에서 데이터 조회
            rEntity = userInfoRepository.findByUserId(userId);

            if (rEntity.isPresent()) { // 값이 존재한다면... (회원가입 성공)
                res = 1;

            } else { // 값이 없다면... (회원가입 실패)
                res = 0;

            }
        }

        log.info(this.getClass().getName() + ".insertUserInfo End!");

        return res;
    }

    /**
     * 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
     *
     * @param pDTO 로그인을 위한 회원아이디, 비밀번호
     * @return 로그인된 회원아이디 정보
     */
    @Override
    public int getUserLogin(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserLogin Start!");

        // 로그인 성공 : 1, 실패 : 0
        int res = 0;

        String userId = CmmUtil.nvl(pDTO.userId());
        String password = CmmUtil.nvl(pDTO.password());

        log.info("userId : " + userId);
        log.info("password : " + password);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserIdAndPassword(userId, password);

        if (rEntity.isPresent()) {
            res = 1;
        }

        log.info(this.getClass().getName() + ".getUser End!");

        return res;
    }

    @Override
    public UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getEmailExists Start!");

        UserInfoDTO rDTO;

        String userName = CmmUtil.nvl(pDTO.userName());
        String email = EncryptUtil.encAES128CBC(CmmUtil.nvl(pDTO.email()));

        log.info("userName : " + userName);
        log.info("email : " + email);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserNameAndEmail(userName, email);

        if (rEntity.isPresent()) {

            UserInfoEntity userInfoEntity = rEntity.get();
            String userId = userInfoEntity.getUserId();
            log.info("userId : " + userId);

            rDTO = UserInfoDTO.builder()
                    .existsYn("Y")
                    .userId(userId)
                    .userName(userName)
                    .build();
        } else {
            rDTO = UserInfoDTO.builder().existsYn("N").build();
        }

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

    @Transactional
    @Override
    public UserInfoDTO getEmailOnlyExists(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getEmailOnlyExists Start!");

        UserInfoDTO rDTO;

        String email = EncryptUtil.encAES128CBC(CmmUtil.nvl(pDTO.email()));

        log.info("email : " + email);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByEmail(email);

        if (rEntity.isPresent()) {

            UserInfoEntity userInfoEntity = rEntity.get();
            String userId = userInfoEntity.getUserId();
            log.info("userId : " + userId);

            rDTO = UserInfoDTO.builder()
                    .existsYn("Y")
                    .userId(userId)
                    .build();
        } else {
            rDTO = UserInfoDTO.builder().existsYn("N").build();
        }

        log.info(this.getClass().getName() + ".getEmailOnlyExists End!");

        return rDTO;
    }

    @Override
    public UserInfoDTO getEmailSend(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getEmailSend Start!");

        // 6자리 랜덤 숫자 생성하기
        int mailNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);

        log.info("mailNumber : " + mailNumber);

        // 인증번호 발송 로직
        MailDTO dto = MailDTO.builder().title("이메일 중복 확인 인증번호 발송 메일").contents("인증번호는 " + mailNumber + " 입니다.").toMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.email()))).build();

        mailService.doSendMail(dto); // 이메일 발송

        dto = null;

        UserInfoDTO rDTO = UserInfoDTO.builder().authNumber(mailNumber).build();

//        log.info(rDTO.getMailNumber());

        log.info(this.getClass().getName() + ".getEmailSend End!"); // 인증번호를 결과값에 넣어주기

        return rDTO;
    }

    @Override
    public MsgDTO getUserNameExists(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserNameExists Start!");

        MsgDTO dto = MsgDTO.builder().build();
        int res = 0;

        String userName = CmmUtil.nvl(pDTO.userName());
        String userId = CmmUtil.nvl(pDTO.userId());
        String password = EncryptUtil.encHashSHA256(pDTO.password());

        log.info("userName : " + userName);
        log.info("userId : " + userId);
        log.info("password : " + password);

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserNameAndUserId(userName, userId);

        if (rEntity.isPresent()) {
            String email = rEntity.get().getEmail();
            String allergy = rEntity.get().getAllergy();
            String regDt = rEntity.get().getRegDt();

            // 회원정보 재 기입
            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(userId).userName(userName)
                    .password(password)
                    .email(email)
                    .allergy(allergy)
                    .regId(userId).regDt(regDt)
                    .chgId(userId).chgDt(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"))
                    .build();

            // 회원정보 DB에 저장
            userInfoRepository.save(pEntity);

            res = 1;

            dto = MsgDTO.builder().result(res).build();
        }

        log.info(this.getClass().getName() + ".getUserNameExists End!");

        return dto;
    }

    @Override
    public UserInfoDTO getUserInfo(String userId) throws Exception {

        log.info(this.getClass().getName() + ".getUserInfo Start!");

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        UserInfoDTO rDTO;

        if (rEntity.isPresent()) {
            UserInfoEntity pEntity = rEntity.get();
            rDTO = UserInfoDTO.builder().
                    userId(pEntity.getUserId()).userName(pEntity.getUserName()).allergy(pEntity.getAllergy()).build();
        } else {
            rDTO = UserInfoDTO.builder().build();
        }

        log.info(this.getClass().getName() + ".getUserInfo End!");

        return rDTO;
    }

    @Override
    public int updateUserInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updateUserInfo Start!");

        String userId = pDTO.userId();

        int res = 0;

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        if(rEntity.isPresent()){

            String userName = pDTO.userName();
            String email = EncryptUtil.encAES128CBC(pDTO.email());
            String allergy = pDTO.allergy();

            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("email : " + email);

            // 회원정보 DB에 저장
            userInfoRepository.updateUserInfo(userId,email,userName,allergy);

            res = 1;

        }

        log.info(this.getClass().getName() + ".updateUserInfo END!");

        return res;

    }

    @Override
    public int updatePassword(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".updatePassword Start!");

        String userId = pDTO.userId();
        String password = pDTO.password();
        int res = 0;

        Optional<UserInfoEntity> rEntity = userInfoRepository.findByUserId(userId);

        if(rEntity.isPresent()){

            String userName = rEntity.get().getUserName();
            String email = rEntity.get().getEmail();
            String allergy = rEntity.get().getAllergy();
            String regDt = rEntity.get().getRegDt();

            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("email : " + email);
            log.info("allergy : " + allergy);
            log.info("regDt : " + regDt);

            UserInfoEntity pEntity = UserInfoEntity.builder()
                    .userId(userId)
                    .userName(userName)
                    .password(password)
                    .email(email)
                    .allergy(allergy)
                    .regDt(regDt)
                    .build();

            // 회원정보 DB에 저장
            userInfoRepository.save(pEntity);

            res = 1;
        }

        log.info(this.getClass().getName() + ".updatePassword END!");

        return res;

    }

    @Override
    public void deleteUserInfo(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        String userId = pDTO.userId();

        log.info("userId : " + userId);

        // 데이터 수정하기
        userInfoRepository.deleteById(userId);

        log.info(this.getClass().getName() + ".deleteUserInfo End!");
    }
}
