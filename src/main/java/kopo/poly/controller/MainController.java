package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.DataDTO;
import kopo.poly.dto.MongoDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IDataService;
import kopo.poly.service.IMongoService;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping(value = "/allergy")
@RequiredArgsConstructor
@Controller
public class MainController {

    private final IDataService dataService;
    private final IUserInfoService userInfoService;
    private final IMongoService mongoService;


    /**
     * 로그인 성공 페이지 이동
     */
    @GetMapping(value = "main")
    public String mainPage(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".mainPage Start!");

        String userId = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));

        UserInfoDTO pDTO = userInfoService.getUserInfo(userId);

        model.addAttribute("pDTO", pDTO);

        log.info("userName : " + pDTO);

        log.info(this.getClass().getName() + ".mainPage End!");

        return "main";
    }


    /**
     * 알러지 검색 화면으로 이동
     */
    @GetMapping(value = "allergySearch")
    public String allergySearch(HttpSession session) {

        log.info(this.getClass().getName() + ".allergy/allergySearch Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info(this.getClass().getName() + ".allergy/allergySearch End!");

        if (userId.length() > 0) {

            return "allergy/allergySearch";

        } else {

            return "redirect:/user/login";
        }
    }

    /**
     * 검색 결과 화면으로 이동
     */
    @GetMapping(value = "searchAllergyResult")
    public String searchAllergyResult(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".searchAllergyResult Start!");

        String data = CmmUtil.nvl(request.getParameter("data"));
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        if (userId.length() > 0) {

            UserInfoDTO pDTO = userInfoService.getUserInfo(userId);

            log.info("UserInfo : " + pDTO);

            log.info("data : " + data);

            DataDTO rDTO = Optional.ofNullable(dataService.getProductApiList(data)).orElseGet(() -> DataDTO.builder().build());

            model.addAttribute("rDTO", rDTO);       // 제품명, 알러지정보
            model.addAttribute("data", data);       // 품목보고번호
            model.addAttribute("pDTO", pDTO);       // 사용자정보

            log.info("rDTO : " + rDTO);

            List<String> prd_allergy_list = Arrays.stream(rDTO.allergy().split(","))
                    .map(String::trim)
                    .map(s -> s.endsWith(" 함유") ? s.substring(0, s.length() - 3) : s)
                    .collect(Collectors.toList());
            List<String> user_allergy_list = Arrays.asList(pDTO.allergy().split(","));     // 사용자 알러지 정보를 리스트 형태로 변환

            log.info("prd_allergy_list : " + prd_allergy_list);
            log.info("user_allergy_list : " + user_allergy_list);

            int res = 0;    // 비교용 변수

            for (String temp : prd_allergy_list) {
                if (user_allergy_list.contains(temp)) {
                    res = 1;        // 겹치는 알러지 있으면 res 값 1로 설정
                    break;
                }
            }

            log.info("res : " + res);
            model.addAttribute("res", res);


        } else {

            return "redirect:/user/login";
        }


        log.info(this.getClass().getName() + ".searchAllergyResult End!");

        return "allergy/searchAllergyResult";
    }


    /**
     * 제품 검색 화면으로 이동
     */
    @GetMapping(value = "itemSearch")
    public String itemSearch(HttpSession session) {

        log.info(this.getClass().getName() + ".allergy/itemSearch Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info(this.getClass().getName() + ".allergy/itemSearch End!");

        if (userId.length() > 0) {

            return "allergy/itemSearch";

        } else {
            return "redirect:/user/login";
        }


    }

    /**
     * 제품 검색 결과 화면으로 이동
     */
    @GetMapping(value = "searchItemResult")
    public String searchItemResult(HttpSession session, HttpServletRequest request, ModelMap model) throws Exception{

        log.info(this.getClass().getName() + ".allergy/searchItemResult Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));


        if (userId.length() > 0) {

            String data = CmmUtil.nvl(request.getParameter("result"));

            model.addAttribute("data", data);

            log.info("data : " + data);

        } else {
            return "redirect:/user/login";
        }

        log.info(this.getClass().getName() + ".allergy/searchItemResult End!");

        return "allergy/searchItemResult";
    }


    @ResponseBody
    @PostMapping(value="searchHistory") // mongodb에 검색기록 저장
    public MsgDTO searchHistory(HttpSession session, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".searchHistory Start!");

        MsgDTO dto;

        String userId = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));
        String prdlstReportNo = CmmUtil.nvl(request.getParameter("prdlstReportNo"));
        String prdlstNm = CmmUtil.nvl(request.getParameter("prdlstNm"));
        String allergy = CmmUtil.nvl(request.getParameter("allergy"));
        String result = CmmUtil.nvl(request.getParameter("result"));

        log.info("제품번호 : " + prdlstReportNo);
        log.info("제품명 : " + prdlstNm);
        log.info("allergy : " + allergy);
        log.info("알러지유무 : " + result);

        if(userId.length() > 0) {
            MongoDTO pDTO = MongoDTO.builder().userId(userId).prdlstReportNo(prdlstReportNo).prdlstNm(prdlstNm)
                    .allergy(allergy).result(result).build();

            int res = mongoService.mongoTest(pDTO);

            dto = MsgDTO.builder().result(res).build();

        } else {
            int res = 3;

            dto = MsgDTO.builder().result(res).build();
        }

        log.info(this.getClass().getName() + ".searchHistory End!");

        return dto;
    }

    @GetMapping(value="history")
    public String allergyHistory(HttpSession session, ModelMap model) throws Exception{

        log.info(this.getClass().getName() + ".allergyhistory Start!");

        String userId = CmmUtil.nvl((String)session.getAttribute("SS_USER_ID"));

        log.info(this.getClass().getName() + ".allergyhistory End!");

        if(userId.length() > 0) {

            MongoDTO pDTO = MongoDTO.builder().userId(userId).build();

            List<MongoDTO> rList = Optional.ofNullable(mongoService.selectData(pDTO)).orElseGet(LinkedList::new);

            model.addAttribute("rList", rList);
            log.info("rList :" + rList);

            return "allergy/history";
        } else {
            return "redirect:/user/login";
        }
    }
}