package app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    @Autowired
    private YoutubeService youtubeService;

    public String getVideoInfos(String linkVideo) {
        this.youtubeService.returnVideoInfos();
        return "Chegou aqui";

    }
}
