package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.DataDTO;
import kopo.poly.service.IDataService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/allergy")
@RequiredArgsConstructor
@Controller
public class MainController {

    private final IDataService dataService;

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
    public String searchAllergyResult(HttpServletRequest request, ModelMap model) throws Exception{

        log.info(this.getClass().getName() + ".searchAllergyResult Start!");

        String data = CmmUtil.nvl(request.getParameter("data"));

        log.info("data : " + data);

        DataDTO rDTO = Optional.ofNullable(dataService.getProductApiList(data)).orElseGet(() -> DataDTO.builder().build());

        model.addAttribute("rDTO", rDTO);
        model.addAttribute("data", data);

        log.info("rDTO : " + rDTO);

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