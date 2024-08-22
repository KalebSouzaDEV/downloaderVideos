package app.service;

import com.github.felipeucelli.javatube.Stream;
import com.github.felipeucelli.javatube.Youtube;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YoutubeService {
    @Autowired
    private static final String APPLICATION_NAME = "YouTube Data API Example";
    @Autowired
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    @Autowired
    private static final String API_KEY = LoaderConfig.getApiKey("api_key");

    @Autowired
    private static YouTube getService() throws GeneralSecurityException, IOException {
        return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, request -> {})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public VideoListResponse returnVideoInfos(String videoID){
        try {
            YouTube youtubeService = getService();
            YouTube.Videos.List request = youtubeService.videos()
                    .list("snippet,contentDetails,statistics");
            request.setId(videoID); // Substitua pelo ID do vídeo do YouTube
            request.setKey(API_KEY);

            VideoListResponse response = request.execute();
            for (Video video : response.getItems()) {
                System.out.printf("Title: %s%n", video.getSnippet().getTitle());
                System.out.printf("Description: %s%n", video.getSnippet().getDescription());
                System.out.printf("Views: %s%n", video.getStatistics().getViewCount());
                System.out.printf("Likes: %s%n", video.getStatistics().getLikeCount());
            }
            return response;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] downloadVideoFromID(String videoID) throws InterruptedException, IOException {
        try {
            String videoUrl = "https://www.youtube.com/watch?v=" + videoID;
            Youtube yt = new Youtube(videoUrl);

            // Obtém todas as streams de vídeo
            List<Stream> streams = yt.streams().getAll()
                .stream().toList();

            // Seleciona a maior resolução disponível
            Stream highestResolutionStream = streams.stream()
                    .max(Comparator.comparingInt(s -> {
                        String resolution = s.getResolution();
                        if (resolution != null && resolution.matches("\\d+p")) {
                            return Integer.parseInt(resolution.replace("p", ""));
                        }
                        return 0; // Se não tiver resolução, considere 0
                    }))
                    .orElse(null);

            if (highestResolutionStream != null) {
                File videoFile = new File("video.mp4");
                highestResolutionStream.download(videoFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Erro " + e);
        }
        return null;
    }
}
