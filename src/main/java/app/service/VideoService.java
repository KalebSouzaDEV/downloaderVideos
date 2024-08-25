package app.service;

import app.entity.Video;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class VideoService {

    @Autowired
    private YoutubeService youtubeService;

    public Video getVideoInfos(String linkVideo) {
        Video videoYoutube = this.youtubeService.returnVideoInfos(linkVideo);
        return videoYoutube;
    }

    public byte[] downloadFullVideoFromID(String linkVideo) throws Exception {
        byte[] bytesVideo = this.youtubeService.downloadVideoAndAudioFromID(linkVideo);
        return bytesVideo;
    }

    public byte[] downloadVideoFromID(String linkVideo) throws Exception {
        byte[] bytesVideo = this.youtubeService.downloadVideoFromID(linkVideo);
        return bytesVideo;
    }

    public byte[] downloadAudioFromID(String linkVideo) throws Exception {
        File audioVideo = this.youtubeService.downloadAudioFromID(linkVideo);
        Path pathToVideo = Path.of(audioVideo.getAbsolutePath() + ".mp4");
        byte[] audioBytes = Files.readAllBytes(pathToVideo);
        return audioBytes;
    }
}
