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
import org.springframework.ui.ModelMap;
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

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("password : " + password);
        log.info("email : " + email);
        log.info("allergy : " + allergy);

        // 웹 (회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .userName(userName)
                .password(EncryptUtil.encHashSHA256(password))
                .email(EncryptUtil.encAES128CBC(email))
                .allergy(allergy)
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

        // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 userInfoService 호출하기
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
     * 비밀번호 찾기로 이동
     */
    @GetMapping(value="searchPassword")
    public String searchPassword() {

        log.info(this.getClass().getName() + ".searchPassword Start!");

        log.info(this.getClass().getName() + ".searchPassword End!");

        return "user/searchPassword";
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

        log.info(this.getClass().getName() + ".logout End!");

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

        userInfoService.updatePassword(pDTO);

        log.info(this.getClass().getName() + ".user/changePassword End!");

        return dto;
    }

    @GetMapping(value="myPage")
    public String myPage(HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".myPage Start!");

        String userId = (String)session.getAttribute("SS_USER_ID");

        if(userId != null) {
            UserInfoDTO pDTO = userInfoService.getUserInfo(userId);

            model.addAttribute("pDTO", pDTO);
        } else {
            return "/user/login";
        }
        log.info(this.getClass().getName() + ".myPage End!");

        return "user/myPage";
    }


    /**
     * 유저 정보 수정 페이지로 이동
     */
    @GetMapping(value="userInfoEdit")
    public String userInfoEdit(HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".userInfoEdit Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");

        if(userId != null) {
            UserInfoDTO pDTO = userInfoService.getUserInfo(userId);

            model.addAttribute("pDTO", pDTO);
        } else {
            return "/user/login";
        }
        log.info(this.getClass().getName() + ".userInfoEdit End!");

        return "user/userInfoEdit";
    }


    /**
     * 유저 정보 수정
     */
    @ResponseBody
    @PostMapping(value = "updateUserInfo")
    public MsgDTO userInfoUpdate(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".updateUserInfo Start!");

        String msg;

        String userId = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));
        String userName = CmmUtil.nvl(request.getParameter("userName"));
        String allergy = CmmUtil.nvl(request.getParameter("allergy"));


        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("allergy : " + allergy);


        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .userName(userName)
                .allergy(allergy)
                .build();

        int res = userInfoService.updateUserInfo(pDTO);

        log.info("회원 정보 수정 결과(res) : " + res);

        if (res == 1) {
            msg = "회원 정보 수정되었습니다..";

        } else {

            msg = "오류로 인해 회원 정보 수정에 실패했습니다.";

        }


        MsgDTO dto = MsgDTO.builder().result(res).msg(msg).build();
        log.info("메시지 : " + dto.msg());

        log.info(this.getClass().getName() + ".updateUserInfo End!");

        return dto;
    }

    /**
     * 비밀번호 변경 페이지 이동
     */
    @GetMapping(value = "myNewPassword")
    public String myNewPassword(HttpSession session) {

        log.info(this.getClass().getName() + ".user/myNewPassword Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        if (userId.length() > 0) {

        } else {


            return "redirect:/user/login";

        }

        return "user/myNewPassword";

    }


    /**
     * 비밀번호 수정하기
     */
    @ResponseBody
    @PostMapping(value = "myUpdatePassword")
    public MsgDTO myUpdatePassword(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".myUpdatePassword Start!");

        String userId = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));

        String msg = ""; // 웹에 보여줄 메세지
        int result = 0;

        if(userId.length() > 0) { //정상접근

            String password = CmmUtil.nvl(request.getParameter("password")); //신규 비밀번호

            log.info("password : " + password);

            UserInfoDTO pDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .password(EncryptUtil.encHashSHA256(password))
                    .build();

            int res = userInfoService.updatePassword(pDTO);


            if(res > 0) {

                msg = "비밀번호가 재설정되었습니다.";
                result = 0;

            } else {

                msg = "비밀번호 변경에 문제가 생겼습니다. 다시 시도해주세요.";

                result = 1;

            }

        } else { //비정상 접근

            msg = "비정상 접근입니다.";
            result = 2;

        }

        MsgDTO mDTO = MsgDTO.builder()
                .msg(msg)
                .result(result)
                .build();

        log.info("msg : " + msg);

        log.info(this.getClass().getName() + ".myUpdatePassword End!");

        return mDTO;
    }


    /**
     * 회원 탈퇴 페이지 이동
     */
    @GetMapping(value="withDraw")
    public String withDraw(HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".withDraw Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");

        if (userId != null) {


        } else {
            return "/user/login";
        }

        log.info(this.getClass().getName() + ".withDraw End!");

        return "user/withDraw";
    }


    /**
     * 유저 정보 삭제
     */
    @ResponseBody
    @PostMapping(value = "deleteUserInfo")
    public MsgDTO userDelete(HttpSession session) {

        log.info(this.getClass().getName() + ".userDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 글번호(PK)

            log.info("userId : " + userId);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            UserInfoDTO pDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .build();

            // 게시글 삭제하기 DB
            userInfoService.deleteUserInfo(pDTO);

            session.invalidate();

            msg = "탈퇴하였습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".userDelete End!");

        }

        return dto;
    }
}
