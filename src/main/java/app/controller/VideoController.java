package app.controller;

import app.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/getInfoVideo/{videoID}")
    public ResponseEntity<String> getVideoInfo(@PathVariable String videoID){
        System.out.println("Aqui ne pai");
        try {
            String mensagemRetorno = this.videoService.getVideoInfos(videoID);
            return new ResponseEntity<String>(mensagemRetorno, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Falha ao pegar informações " + e, HttpStatus.BAD_REQUEST);
        }
    }
}
