package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.service.INoticeJoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/*
 * Controller 선언해야만 Spring 프레임워크에서 Controller인지 인식 가능
 * 자바 서블릿 역할 수행
 *
 * slf4j는 스프링 프레임워크에서 로그 처리하는 인터페이스 기술이며,
 * 로그처리 기술인 log4j와 logback과 인터페이스 역할 수행함
 * 스프링 프레임워크는 기본으로 logback을 채택해서 로그 처리함
 * */
@Slf4j
@RequestMapping(value = "/notice")
@RequiredArgsConstructor
@Controller
public class NoticeJoinController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final INoticeJoinService noticeJoinService;

    /**
     * 게시판 리스트 보여주기
     * <p>
     * GetMapping(value = "notice/noticeListUsingJoinColumn") =>
     * GET방식을 통해 접속되는 URL이 notice/noticeList 경우 아래 함수를 실행함
     */
    @GetMapping(value = "noticeListUsingJoinColumn")
    public String noticeListUsingJoinColumn(HttpSession session, ModelMap model)
            throws Exception {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeListUsingJoinColumn Start!");

        // 로그인된 사용자 아이디는 Session에 저장함
        // 교육용으로 아직 로그인을 구현하지 않았기 때문에 Session에 데이터를 저장하지 않았음
        // 추후 로그인을 구현할 것으로 가정하고, 공지사항 리스트 출력하는 함수에서 로그인 한 것처럼 Session 값을 생성함

        // 공지사항 리스트 조회하기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<NoticeDTO> rList = Optional.ofNullable(noticeJoinService.getNoticeListUsingJoinColumn())
                .orElseGet(ArrayList::new);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeListUsingJoinColumn End!");

        // 함수 처리가 끝나고 보여줄 HTML (Thymeleaf) 파일명
        // templates/notice/noticeList.html
        return "notice/noticeListUsingJoinColumn";

    }

    @GetMapping(value = "noticeListUsingNativeQuery")
    public String noticeListUsingNativeQuery(HttpSession session, ModelMap model)
            throws Exception {

        log.info(this.getClass().getName() + ".noticeListUsingNativeQuery Start!");

        List<NoticeDTO> rList = Optional.ofNullable(noticeJoinService.getNoticeListUsingNativeQuery())
                .orElseGet(ArrayList::new);

        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".noticeList End!");

        return "notice/noticeListUsingNativeQuery";
    }

    @GetMapping(value = "noticeListUsingJPQL")
    public String noticeListUsingJPQL(HttpSession session, ModelMap model)
            throws Exception {

        log.info(this.getClass().getName() + ".noticeListUsingJPQL Start!");

        List<NoticeDTO> rList = Optional.ofNullable(noticeJoinService.getNoticeListUsingJPQL())
                .orElseGet(ArrayList::new);

        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".noticeListUsingJPQL End!");

        return "notice/noticeListUsingJPQL";
    }

    @GetMapping(value = "noticeListUsingQueryDSL")
    public String noticeListUsingQueryDSL(HttpSession session, ModelMap model)
            throws Exception {

        log.info(this.getClass().getName() + ".noticeListUsingQueryDSL Start!");

        List<NoticeDTO> rList = Optional.ofNullable(noticeJoinService.getNoticeListForQueryDSL())
                .orElseGet(ArrayList::new);

        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".noticeListUsingQueryDSL End!");

        return "notice/noticeListUsingQueryDSL";
    }
}
