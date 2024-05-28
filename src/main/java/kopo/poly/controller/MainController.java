package kopo.poly.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RequestMapping(value = "/allergy")
@RequiredArgsConstructor
@Controller
public class MainController {

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
     * 알러지 검색 결과 화면으로 이동
     */
    @GetMapping(value = "searchAllergyResult")
    public String searchAllergyResult() {

        log.info(this.getClass().getName() + ".allergy/searchResult Start!");

        log.info(this.getClass().getName() + ".allergy/searchResult End!");

        return "searchAllergyResult";
    }

    /**
     * 알러지 검색 화면으로 이동
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
