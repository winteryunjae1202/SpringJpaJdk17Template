package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final IUserInfoService userInfoService;

    /**
     * 회원가입 화면으로 이동
     */
    @GetMapping(value = "userRegForm")
    public String userRegForm() {

        log.info(this.getClass().getName() + ".user/userRegForm Start!");

        log.info(this.getClass().getName() + ".user/userRegForm End!");

        return "user/userRegForm";

    }

    /**
     * 회원가입 전 아이디 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     */
    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("userId : " + userId);

        // Builder 통한 값 저장
        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        // 회원아이디를 통해 중복된 아이디인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    /**
     * 이메일 중복체크
     */
    @ResponseBody
    @PostMapping(value = "getEmailOnlyExists")          // 이메일 중복찾기
    public UserInfoDTO getEmailOnlyExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailOnlyExists Start!");

        String email = CmmUtil.nvl(request.getParameter("email"));

        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder().email(EncryptUtil.encAES128CBC(email)).build();

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailOnlyExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        String existsYn = rDTO.existsYn();

        if (existsYn.equals("N")) {
            int authNumber = userInfoService.getEmailSend(pDTO).authNumber();
            log.info("authNumber : " + authNumber);

            rDTO = UserInfoDTO.builder().authNumber(authNumber).existsYn(existsYn).build();
        }

        log.info(this.getClass().getName() + ".getEmailOnlyExists End!!");

        return rDTO;
    }



    /**
     * 회원가입 로직 처리
     */
    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo Start!");

        String msg; // 회원가입 결과에 대한 메시지를 전달할 변수

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String password = CmmUtil.nvl(request.getParameter("password"));
        String email = CmmUtil.nvl(request.getParameter("email"));
        String allergy = CmmUtil.nvl(request.getParameter("allergy"));
        String nickname = CmmUtil.nvl(request.getParameter("nickname"));

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("password : " + password);
        log.info("email : " + email);
        log.info("allergy : " + allergy);
        log.info("nickname : " + nickname);

        // 웹 (회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .userName(userName)
                .password(EncryptUtil.encHashSHA256(password))
                .email(EncryptUtil.encAES128CBC(email))
                .allergy(allergy)
                .nickname(nickname)
                .regId(userId)
                .chgId(userId)
                .build();

        // 회원가입 서비스 호출하여 결과 받기
        int res = userInfoService.insertUserInfo(pDTO);

        log.info("회원가입 결과(res) : " + res);

        if (res == 1) {
            msg = "회원가입되었습니다.";
        } else if (res == 2) {
            msg = "이미 가입된 아이디입니다.";
        } else {
            msg = "오류로 인해 회원가입이 실패하였습니다.";
        }

        // 결과 메시지 전달하기
        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();

        log.info(this.getClass().getName() + ".insertUserInfo End!");

        return dto;
    }

    /**
     * 로그인을 위한 입력 화면으로 이동
     */
    @GetMapping(value = "login")
    public String login() {
        log.info(this.getClass().getName() + ".user/login Start!");

        log.info(this.getClass().getName() + ".user/login End!");

        return "user/login";
    }

    /**
     * 로그인 처리 및 결과 알려주는 화면으로 이동
     */
    @ResponseBody
    @PostMapping(value = "loginProc")
    public MsgDTO loginProc(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".loginProc Start!");

        String msg; // 로그인 결과에 대한 메시지를 전달할 변수

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String password = CmmUtil.nvl(request.getParameter("password"));

        log.info("userId : " + userId);
        log.info("password : " + password);

        // 웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .password(EncryptUtil.encHashSHA256(password)).build();

        // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 usreInfoService 호춣하기
        int res = userInfoService.getUserLogin(pDTO);

        log.info("res : " + res);

        /*
         * 스프링에서 세션을 사용하기 위해서는 함수명의 파라미터에 HttpSession session 존재해야 한다.
         * 세션은 톰켓의 메모리에 저장되기 때문에 url마다 전달하는게 필요하지 않고,
         * 그냥 메모리에서 부르면 되기 때문에 jsp, controller에서 쉽게 불러서 쓸 수 있다.
         */

        if (res == 1) { // 로그인 성공

            /*
             * 세션에 회원아이디 저장하기, 추후 로그인여부를 체크하기 위해 세션에 값이 존재하는지 체크한다.
             * 일반적으로 세션에 저장되는 키는 대문자로 입력하며, 앞에 SS를 붙인다.
             *
             * Session 단어에서 SS를 가져온 것이다.
             */
            msg = "로그인이 성공했습니다.";
            session.setAttribute("SS_USER_ID", userId);

        } else {
            msg = "아이디와 비밀번호가 올바르지 않습니다.";

        }

        // 결과 메시지 전달하기
        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();
        log.info(this.getClass().getName() + "loginProc End!");

        return dto;
    }

    /**
     * 로그인 성공 페이지 이동
     */
    @GetMapping(value = "loginSuccess")
    public String loginSuccess() {
        log.info(this.getClass().getName() + ".user/loginSuccess Start!");

        log.info(this.getClass().getName() + ".user/loginSuccess End!");

        return "page/main";
    }

    /**
     * 로그아웃 처리하기
     */
    @ResponseBody
    @PostMapping(value = "logout")
    public MsgDTO logout(HttpSession session) {

        log.info(this.getClass().getName() + ".logout Start!");

        session.setAttribute("SS_USER_ID", ""); // 세션 값 빈값으로 변경
        session.removeAttribute("SS_USER_ID"); // 세션 값 지우기

        // 결과 메시지 전달하기
        MsgDTO dto = MsgDTO.builder().result(1).msg("로그아웃하였습니다.").build();

        log.info(this.getClass().getName() + "logout End!");

        return dto;
    }

    /**
     * 아이디 찾기
     */
    @ResponseBody
    @PostMapping(value = "searchId")
    public UserInfoDTO searchId(HttpServletRequest request) throws Exception{

        log.info(this.getClass().getName() + ".user/searchId Start!");

        int res = 0;

        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String email = CmmUtil.nvl(request.getParameter("email"));

        log.info("userName : " + userName);
        log.info("email : " + email);


        UserInfoDTO pDTO = UserInfoDTO.builder().userName(userName).email(email).build();

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());


        log.info(this.getClass().getName() + ".user/searchId End!");

        return rDTO;
    }

    @GetMapping(value = "searchUserId")
    public String searchId() {
        log.info(this.getClass().getName() + ".searchUserId Start!");

        log.info(this.getClass().getName() + ".searchUserId End!");

        return "user/searchUserId";
    }

    /**
     * 비밀번호 변경하기
     */
    @ResponseBody
    @PostMapping(value="changePassword")
    public MsgDTO changePassword(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".user/changePassword Start!");

        int res = 0;

        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String userId = CmmUtil.nvl(request.getParameter("userId"));
        String password = CmmUtil.nvl(request.getParameter("password"));

        log.info("userName : " + userName);
        log.info("userId : " + userId);
        log.info("password : " + password);

        UserInfoDTO pDTO = UserInfoDTO.builder().userName(userName).userId(userId).password(EncryptUtil.encHashSHA256(password)).build();

        MsgDTO dto = Optional.ofNullable(userInfoService.getUserNameExists(pDTO))
                .orElseGet(() -> MsgDTO.builder().build());

        log.info(this.getClass().getName() + ".user/changePassword End!");

        return dto;
    }

}
