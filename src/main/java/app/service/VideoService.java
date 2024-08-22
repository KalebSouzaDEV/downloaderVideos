package app.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VideoService {

    @Autowired
    private YoutubeService youtubeService;

    public VideoListResponse getVideoInfos(String linkVideo) {
        VideoListResponse videoYoutube = this.youtubeService.returnVideoInfos(linkVideo);
        return videoYoutube;
    }

    public byte[] downloadVideoFromID(String linkVideo) throws IOException, InterruptedException {
        byte[] bytesVideo = this.youtubeService.downloadVideoFromID(linkVideo);
        return bytesVideo;
    }
}
