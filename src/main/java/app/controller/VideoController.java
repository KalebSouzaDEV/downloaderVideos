package app.controller;

import app.service.VideoService;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/getInfoVideo/{videoID}")
    public ResponseEntity<VideoListResponse> getVideoInfo(@PathVariable String videoID){
        try {
            VideoListResponse videoRetorno = this.videoService.getVideoInfos(videoID);
            return new ResponseEntity<VideoListResponse>(videoRetorno, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/downloadVideo/{videoID}")
    public ResponseEntity<byte[]> downloadVideo(@PathVariable String videoID) {
        try {
            byte[] bytesFromVideo = this.videoService.downloadVideoFromID(videoID);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=video.mp4");

            return new ResponseEntity<>(bytesFromVideo, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
