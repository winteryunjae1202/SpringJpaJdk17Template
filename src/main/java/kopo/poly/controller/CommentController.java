package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.CommentDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.ICommentService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping(value = "/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final ICommentService commentService;

    /**
     * 게시판 글 등록
     * <p>
     * 게시글 등록은 Ajax로 호출되기 때문에 결과는 JSON 구조로 전달해야만 함
     * JSON 구조로 결과 메시지를 전송하기 위해 @ResponseBody 어노테이션 추가함
     */
    @ResponseBody
    @PostMapping(value = "commentInsert")
    public MsgDTO commentInsert(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".commentInsert Start!");

        String msg = ""; // 메시지 내용

        MsgDTO dto; // 결과 메시지 구조

        try {
            // 로그인된 사용자 아이디를 가져오기
            // 로그인을 아직 구현하지 않았기에 공지사항 리스트에서 로그인 한 것처럼 Session 값을 저장함
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String content = CmmUtil.nvl(request.getParameter("content")); // 제목
            String noticeSeq = CmmUtil.nvl(request.getParameter("noticeSeq")); // 내용

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("ss_user_id : " + userId);
            log.info("content : " + content);
            log.info("noticeSeq : " + noticeSeq);

            // 데이터 저장하기 위해 DTO에 저장하기
            CommentDTO pDTO = CommentDTO
                    .builder()
                    .userId(userId)
                    .commentContents(content)
                    .noticeSeq(Long.valueOf(noticeSeq))
                    .build();

            /*
             * 게시글 등록하기위한 비즈니스 로직을 호출
             */
            commentService.insertComment(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";

        } catch (Exception e) {

            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".commentInsert End!");
        }

        return dto;
    }

    /**
     * 게시판 글 삭제
     */

    @ResponseBody
    @PostMapping(value = "commentDelete")
    public MsgDTO commentDelete(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".commentDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {

            String noticeSeq = CmmUtil.nvl(request.getParameter("noticeSeq")); // 댓글번호(PK)
            String commentSeq = CmmUtil.nvl(request.getParameter("commentSeq")); // 댓글번호(PK)

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("noticeSeq : " + noticeSeq);
            log.info("commentSeq : " + commentSeq);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            CommentDTO pDTO = CommentDTO.builder()
                    .noticeSeq(Long.parseLong(noticeSeq))
                    .commentSeq(Long.parseLong(commentSeq))
                    .build();

            // 게시글 삭제하기 DB
            commentService.deleteComment(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".commentDelete End!");

        }

        return dto;
    }

    // 댓글 수정
    @ResponseBody
    @PostMapping(value = "commentUpdate")
    public MsgDTO commentUpdate(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".commentUpdate Start!");

        String msg;

        String userId = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));
        String commentSeq = CmmUtil.nvl(request.getParameter("commentSeq"));
        String noticeSeq = CmmUtil.nvl(request.getParameter("noticeSeq"));
        String commentContents = CmmUtil.nvl(request.getParameter("commentContents"));
        String chgDt = DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss");

        log.info("userId : " + userId);
        log.info("commentSeq : " + commentSeq);
        log.info("noticeSeq : " + noticeSeq);
        log.info("commentContents : " + commentContents);
        log.info("chgDt : " + chgDt);


        CommentDTO pDTO = CommentDTO
                .builder()
                .userId(userId)
                .commentSeq(Long.parseLong(commentSeq))
                .noticeSeq(Long.parseLong(noticeSeq))
                .commentContents(commentContents)
                .commentChgId(userId)
                .commentChgDt(chgDt)
                .build();

        int res = commentService.updateComment(pDTO);

        log.info("댓글 수정 결과(res) : " + res);

        if (res == 1) {
            msg = "댓글 수정되었습니다..";

        } else {

            msg = "오류로 인해 댓글 수정에 실패했습니다.";

        }

        MsgDTO dto = MsgDTO.builder()
                .result(res)
                .msg(msg)
                .build();

        log.info(this.getClass().getName() + ".commentUpdate End!");

        return dto;
    }
}