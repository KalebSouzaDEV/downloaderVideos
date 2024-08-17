package app.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

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
        System.out.println(API_KEY + " CUZINNN");
        return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, request -> {})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String returnVideoInfos(){
        try {
            YouTube youtubeService = getService();
            YouTube.Videos.List request = youtubeService.videos()
                    .list("snippet,contentDetails,statistics");
            request.setId("VIDEO_ID"); // Substitua pelo ID do vídeo do YouTube
            request.setKey(API_KEY);

            VideoListResponse response = request.execute();
            for (Video video : response.getItems()) {
                System.out.printf("Title: %s%n", video.getSnippet().getTitle());
                System.out.printf("Description: %s%n", video.getSnippet().getDescription());
                System.out.printf("Views: %s%n", video.getStatistics().getViewCount());
                System.out.printf("Likes: %s%n", video.getStatistics().getLikeCount());
            }
            return "Foi";
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return "Nao foi";
        }
    }
}
