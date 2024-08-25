package app.controller;

import app.entity.Video;
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

    @GetMapping("/infoVideo/{videoID}")
    public ResponseEntity<Video> getVideoInfo(@PathVariable String videoID){
        try {
            Video videoRetorno = this.videoService.getVideoInfos(videoID);
            return new ResponseEntity<Video>(videoRetorno, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/downloadVideoFull/{videoID}")
    public ResponseEntity<byte[]> downloadVideo(@PathVariable String videoID) {
        try {
            System.out.println("Vai se foder ");
            byte[] bytesFromVideo = this.videoService.downloadFullVideoFromID(videoID);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=video.mp4");
            System.out.println("Vai se foder22 ");

            return new ResponseEntity<>(bytesFromVideo, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/downloadOnlyVideo/{videoID}")
    public ResponseEntity<byte[]> downloadOnlyVideo(@PathVariable String videoID) {
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

    @GetMapping("/downloadOnlyAudio/{videoID}")
    public ResponseEntity<byte[]> downloadOnlyAudio(@PathVariable String videoID) {
        try {
            byte[] bytesFromVideo = this.videoService.downloadAudioFromID(videoID);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=video.mp4");

            return new ResponseEntity<>(bytesFromVideo, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
