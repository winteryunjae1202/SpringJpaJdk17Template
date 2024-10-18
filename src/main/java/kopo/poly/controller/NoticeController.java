package kopo.poly.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.ICommentService;
import kopo.poly.service.ILikeService;
import kopo.poly.service.INoticeService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
// @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
public class NoticeController {

    private final INoticeService noticeService;
    private final ICommentService commentService;
    private final ILikeService likeService;
    private final AmazonS3 s3Client;
    private final String bucketName;

    /**
     * 게시판 리스트 보여주기
     * <p>
     * GetMapping(value = "notice/noticeList") =>  GET방식을 통해 접속되는 URL이 notice/noticeList 경우 아래 함수를 실행함
     */
    @GetMapping(value = "noticeList")
    public String noticeList(HttpSession session, ModelMap model)
            throws Exception {

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeList Start!");

        // 로그인된 사용자 아이디는 Session에 저장함
        // 교육용으로 아직 로그인을 구현하지 않았기 때문에 Session에 데이터를 저장하지 않았음
        // 추후 로그인을 구현할 것으로 가정하고, 공지사항 리스트 출력하는 함수에서 로그인 한 것처럼 Session 값을 생성함

        // 공지사항 리스트 조회하기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<NoticeDTO> rList = Optional.ofNullable(noticeService.getNoticeList())
                .orElseGet(ArrayList::new);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rList", rList);

        // 로그 찍기(추후 찍은 로그를 통해 이 함수 호출이 끝났는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".noticeList End!");

        // 함수 처리가 끝나고 보여줄 HTML (Thymeleaf) 파일명
        // templates/notice/noticeList.html
        return "notice/noticeList";

    }

    /**
     * 게시판 작성 페이지 이동
     * <p>
     * 이 함수는 게시판 작성 페이지로 접근하기 위해 만듬
     * <p>
     * GetMapping(value = "notice/noticeReg") =>  GET방식을 통해 접속되는 URL이 notice/noticeReg 경우 아래 함수를 실행함
     */
    @GetMapping(value = "noticeReg")
    public String noticeReg(HttpSession session) {

        log.info(this.getClass().getName() + ".noticeReg Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info(this.getClass().getName() + ".noticeReg End!");

        if (userId.length() > 0) {
            return "notice/noticeReg";
        } else {
            return "redirect:/user/login";
        }
    }

    /**
     * 게시글 등록
     */
    @Transactional
    @PostMapping(value = "noticeInsert")
    public ResponseEntity<?> noticeInsert(HttpServletRequest request, @RequestParam("images") MultipartFile[] images, HttpSession session) {

        log.info(this.getClass().getName() + ".noticeInsert Start!");

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn")); // 공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용
            List<NoticeImageDTO> imageDTOList = new ArrayList<>();

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("session_user_id : " + userId);
            log.info("title : " + title);
            log.info("noticeYn : " + noticeYn);
            log.info("contents : " + contents);

            // 데이터 저장하기 위해 DTO에 저장하기 NoticeDTO pDTO = new NoticeDTO(); 이방식 못씀
            NoticeDTO pDTO = NoticeDTO.builder().userId(userId).title(title)
                    .noticeYn(noticeYn).contents(contents).images(imageDTOList).build();

            /*
             * 게시글 등록하기위한 비즈니스 로직을 호출
             */
            Long noticeSeq = noticeService.insertNoticeInfo(pDTO);

            // 이미지 처리 및 데이터베이스 저장
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String extension = FilenameUtils.getExtension(image.getOriginalFilename());
                    String fileName = "notices/" + userId + "_" + noticeSeq + "_" + UUID.randomUUID().toString() + "." + extension;

                    // ObjectMetadata 설정
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(image.getSize()); // 이미지 크기를 메타데이터에 설정

                    // ACL 설정 없이 객체 업로드
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), metadata)); // ACL 제거
                    String imageUrl = s3Client.getUrl(bucketName, fileName).toString();

                    NoticeImageDTO noticeImageDTO = new NoticeImageDTO(null, noticeSeq, imageUrl);
                    imageDTOList.add(noticeImageDTO);
                }
            }

            // 이미지 정보 업데이트 (데이터베이스에 이미지 정보 저장)
            noticeService.updateNoticeImages(noticeSeq, imageDTOList);
            // 저장이 완료되면 사용자에게 보여줄 메시지

            return ResponseEntity.ok("게시글 등록에 성공했습니다.");

        } catch (Exception e) {
            log.error("게시글 등록 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록에 실패했습니다. : " + e.getMessage());
        } finally {
            log.info(this.getClass().getName() + ".noticeInsert End!");
        }
    }

    /**
     * 게시판 상세보기
     */
    @GetMapping(value = "noticeInfo")
    public String noticeInfo(HttpSession session, HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".noticeInfo Start!");

        String nSeq = CmmUtil.nvl(request.getParameter("nSeq"), "0"); // 공지글번호(PK)
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("nSeq : " + nSeq);
        log.info("userId : " + userId);

        /*
         * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
         */
        NoticeDTO pDTO = NoticeDTO.builder().noticeSeq(Long.parseLong(nSeq)).build();

        LikeDTO lDTO = LikeDTO.builder()
                .noticeSeq(Long.parseLong(nSeq))
                .userId(userId)
                .build();


        // 공지사항 상세정보 가져오기
        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO, true))
                .orElseGet(() -> NoticeDTO.builder().build());

        CommentDTO cDTO = CommentDTO.builder()
                .noticeSeq(Long.parseLong(nSeq))
                .build();

        List<CommentDTO> cList = Optional.ofNullable(commentService.getCommentList(cDTO))
                .orElseGet(() -> new ArrayList<>());

        List<NoticeImageDTO> iList = Optional.ofNullable(noticeService.getImageList(pDTO))
                .orElseGet(ArrayList::new);

        LikeDTO likeDTO = Optional.ofNullable(likeService.likeExists(lDTO))
                .orElseGet(() -> LikeDTO.builder().build());

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);
        model.addAttribute("iList", iList);
        model.addAttribute("cList", cList);
        model.addAttribute("likeDTO", likeDTO);

        log.info(this.getClass().getName() + ".noticeInfo End!");

        return "notice/noticeInfo";
    }

    /**
     * 게시판 수정 보기
     */
    @GetMapping(value = "noticeEditInfo")
    public String noticeEditInfo(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".noticeEditInfo Start!");

        String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 공지글번호(PK)

        /*
         * ####################################################################################
         * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
         * ####################################################################################
         */
        log.info("nSeq : " + nSeq);

        /*
         * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
         */
        NoticeDTO pDTO = NoticeDTO.builder().noticeSeq(Long.parseLong(nSeq)).build();

        // Java 8부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO, false))
                .orElseGet(() -> NoticeDTO.builder().build());

        List<NoticeImageDTO> iList = Optional.ofNullable(noticeService.getImageList(pDTO))
                .orElseGet(ArrayList::new);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);
        model.addAttribute("iList", iList);

        log.info(this.getClass().getName() + ".noticeEditInfo End!");

        return "notice/noticeEditInfo";
    }

    /**
     * 게시판 글 수정
     */
    @PostMapping(value = "noticeUpdate")
    public ResponseEntity<?> noticeUpdate(HttpServletRequest request, @RequestParam("images") MultipartFile[] images, HttpSession session) {

        log.info(this.getClass().getName() + ".noticeUpdate Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 아이디
            Long noticeSeq = Long.parseLong(CmmUtil.nvl(request.getParameter("nSeq"))); // 글번호(PK)
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn")); // 공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용
            List<NoticeImageDTO> imageDTOList = new ArrayList<>();

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("userId : " + userId);
            log.info("noticeSeq : " + noticeSeq);
            log.info("title : " + title);
            log.info("noticeYn : " + noticeYn);
            log.info("contents : " + contents);

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            NoticeDTO pDTO = NoticeDTO.builder().userId(userId).noticeSeq(noticeSeq)
                    .title(title).noticeYn(noticeYn).contents(contents).images(imageDTOList).build();

            // 게시글 수정하기 DB
            noticeService.updateNoticeInfo(pDTO);

            // 이미지 처리 및 S3 업로드
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String extension = FilenameUtils.getExtension(image.getOriginalFilename());
                    String fileName = "notices/" + userId + "_" + noticeSeq + "_" + UUID.randomUUID().toString() + "." + extension;

                    // ObjectMetadata 설정
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(image.getSize()); // 이미지 크기를 메타데이터에 설정

                    // ACL 설정 없이 객체 업로드
                    s3Client.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), metadata)); // ACL 제거
                    String imageUrl = s3Client.getUrl(bucketName, fileName).toString();

                    NoticeImageDTO noticeImageDTO = new NoticeImageDTO(null, noticeSeq, imageUrl);
                    imageDTOList.add(noticeImageDTO);
                }
            }

            noticeService.updateNoticeImages(noticeSeq, imageDTOList);

            return ResponseEntity.ok("게시글 수정에 성공했습니다.");

        } catch (Exception e) {
            log.error("게시글 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정에 실패했습니다. : " + e.getMessage());
        } finally {
            log.info(this.getClass().getName() + ".noticeUpdate End!");
        }
    }

    /**
     * 게시판 글 삭제
     */
    @ResponseBody
    @PostMapping(value = "noticeDelete")
    public MsgDTO noticeDelete(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".noticeDelete Start!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 글번호(PK)

            /*
             * ####################################################################################
             * 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함 반드시 작성할 것
             * ####################################################################################
             */
            log.info("nSeq : " + nSeq);

            List<NoticeImageDTO> imagePathList = noticeService.getImagePathList(Long.parseLong(nSeq));

            for (NoticeImageDTO noticeImageDTO : imagePathList) {
                if (noticeImageDTO.imagePath() != null && !noticeImageDTO.imagePath().isEmpty()) {
                    URL url = new URL(noticeImageDTO.imagePath());
                    String s3ImagePath = url.getPath().substring(1); // URL에서 객체 키 추출 (앞의 '/' 제거)
                    log.info("s3ImagePath: " + s3ImagePath);
                    s3Client.deleteObject(bucketName, s3ImagePath);
                }
            }

            /*
             * 값 전달은 반드시 DTO 객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
             */
            NoticeDTO pDTO = NoticeDTO.builder().noticeSeq(Long.parseLong(nSeq)).build();

            // 게시글 삭제하기 DB
            noticeService.deleteNoticeInfo(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 전달하기
            dto = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".noticeDelete End!");

        }

        return dto;
    }

    /**
     * 좋아요 기능
     */
    @ResponseBody
    @PostMapping(value="addLike")
    public MsgDTO addLike(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".addLike Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");
        long noticeSeq = Long.parseLong(CmmUtil.nvl(request.getParameter("noticeSeq")));

        log.info("userId : " + userId);
        log.info("noticeSeq : " + noticeSeq);

        LikeDTO pDTO = LikeDTO.builder()
                .userId(userId)
                .noticeSeq(noticeSeq)
                .build();

        likeService.insertLike(pDTO);

        MsgDTO rDTO = MsgDTO.builder()
                .msg("좋아요 하였습니다.")
                .build();

        log.info(this.getClass().getName() + ".addLike End!");

        return rDTO;
    }

    /**
     * 좋아요 취소
     */
    @ResponseBody
    @PostMapping(value="deleteLike")
    public MsgDTO deleteLike(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".deleteLike Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");
        long noticeSeq = Long.parseLong(CmmUtil.nvl(request.getParameter("noticeSeq")));

        log.info("userId : " + userId);
        log.info("noticeSeq : " + noticeSeq);

        LikeDTO pDTO = LikeDTO.builder()
                .userId(userId)
                .noticeSeq(noticeSeq)
                .build();

        likeService.deleteLike(pDTO);

        MsgDTO rDTO = MsgDTO.builder()
                .msg("좋아요 취소 성공하였습니다.")
                .build();

        log.info(this.getClass().getName() + ".deleteLike End!");

        return rDTO;
    }

    /**
     * 게시글 수정 (각 이미지 삭제)
     */
    @DeleteMapping("deleteImage/{imageSeq}")
    public ResponseEntity<?> deleteImage(HttpServletRequest request, @PathVariable("imageSeq") Long imageSeq) {
        try {

            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            log.info("nSeq : " + nSeq);
            log.info("imageSeq : " + imageSeq);

            NoticeImageDTO pDTO = NoticeImageDTO.builder()
                    .imageSeq(imageSeq)
                    .noticeSeq(Long.parseLong(nSeq))
                    .build();

            String imagePath = noticeService.getImagePath(pDTO);

            if (imagePath != null && !imagePath.isEmpty()) {
                URL oldimagePath = new URL(imagePath);
                String substringedimagePath = oldimagePath.getPath().substring(1); // URL에서 객체 키 추출 (앞의 '/' 제거)
                log.info("substringedimagePath: " + substringedimagePath);
                s3Client.deleteObject(bucketName, substringedimagePath);
            }

            noticeService.deleteImageById(pDTO);

            return ResponseEntity.ok().body("이미지 삭제 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이미지 삭제 실패: " + e.getMessage());
        }
    }
}
