package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.MailDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.IMailService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping(value = "/mail")
@RequiredArgsConstructor
@Controller
public class MailController {

    private final IMailService mailService; // 메일 발송을 위한 서비스 객체를 사용하기

    @GetMapping(value = "mailForm")
    public String mailForm() throws Exception {
        log.info(this.getClass().getName() + "mailForm Start!!");

        return "/mail/mailForm";
    }

    /**
     * 메일 발송하기
     */
    @ResponseBody
    @PostMapping(value = "sendMail")
    public MsgDTO sendMail(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + ".sendMail Start!");

        String msg = ""; // 발송 결과 메시지

        // 웹 URL로부터 전달받는 값들
        String to_mail = CmmUtil.nvl(request.getParameter("email"));
        String title = CmmUtil.nvl(request.getParameter("title"));
        String contents = CmmUtil.nvl(request.getParameter("contents"));

        log.info("to_mail : " + to_mail);
        log.info("title : " + title);
        log.info("contents : " + contents);

        // 메일 발송할 정보를 넣기 위한 DTO 객체 생성하기
        MailDTO pDTO = MailDTO.builder().toMail(to_mail).title(title).contents(contents).build();

        // 메일발송하기
        int res = mailService.doSendMail(pDTO);

        if (res == 1) { // 메일발송 성공
            msg = "메일 발송하였습니다.";
        } else {
            msg = "메일 발송 실패하였습니다.";
        }

        log.info(msg);

        // 결과 메시지 전달하기
        MsgDTO dto = MsgDTO.builder().msg(msg).build();

        log.info(this.getClass().getName() + ".sendMail End!");

        return dto;
    }
}
