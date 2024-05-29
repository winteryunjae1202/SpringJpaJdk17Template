package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.DataDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IDataService;
import kopo.poly.service.impl.UserInfoService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/allergy")
@RequiredArgsConstructor
@Controller
public class MainController {

    private final IDataService dataService;
    private final UserInfoService userInfoService;


    /**
     * 로그인 성공 페이지 이동
     */
    @GetMapping(value = "main")
    public String mainPage(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".mainPage Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");

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
    public String allergySearch() {
        log.info(this.getClass().getName() + ".allergy/allergySearch Start!");

        log.info(this.getClass().getName() + ".allergy/allergySearch End!");

        return "allergy/allergySearch";

    }

    /**
     * 검색 결과 화면으로 이동
     */
    @GetMapping(value = "searchAllergyResult")
    public String searchAllergyResult(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception{

        log.info(this.getClass().getName() + ".searchAllergyResult Start!");

        String data = CmmUtil.nvl(request.getParameter("data"));
        String userId = (String) session.getAttribute("SS_USER_ID");

        UserInfoDTO pDTO = userInfoService.getUserInfo(userId);

        log.info("UserInfo : " + pDTO);

        log.info("data : " + data);

        DataDTO rDTO = Optional.ofNullable(dataService.getProductApiList(data)).orElseGet(() -> DataDTO.builder().build());

        model.addAttribute("rDTO", rDTO);       // 제품명, 알러지정보
        model.addAttribute("data", data);       // 품목보고번호
        model.addAttribute("pDTO", pDTO);       // 사용자정보

        log.info("rDTO : " + rDTO);

        List<String> prd_allergy_list = Arrays.asList(rDTO.allergy().split(","));      // 제품 알러지정보를 리스트 형태로 변환
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

        log.info(this.getClass().getName() + ".searchAllergyResult End!");

        return "allergy/searchAllergyResult";
    }



    /**
     * 제품 검색 화면으로 이동
     */
    @GetMapping(value = "itemSearch")
    public String itemSearch() {

        log.info(this.getClass().getName() + ".allergy/itemSearch Start!");

        log.info(this.getClass().getName() + ".allergy/itemSearch End!");

        return "allergy/itemSearch";
    }

    /**
     * 제품 검색 결과 화면으로 이동
     */
    @GetMapping(value = "searchItemResult")
    public String searchItemResult() {

        log.info(this.getClass().getName() + ".allergy/searchItemResult Start!");

        log.info(this.getClass().getName() + ".allergy/searchItemResult End!");

        return "allergy/searchItemResult";
    }


}