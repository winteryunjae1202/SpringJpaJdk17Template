package kopo.poly.service;

import kopo.poly.dto.YoutubeDTO;

import java.util.List;

public interface IYoutubeService {

    String apiURL = "https://www.googleapis.com/youtube/v3/search";

    List<YoutubeDTO> getYoutubeInfo() throws Exception;


}