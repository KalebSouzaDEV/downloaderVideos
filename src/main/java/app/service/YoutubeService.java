package app.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.stereotype.Service;

@Service
public class YoutubeService {
    private static final String APPLICATION_NAME = "YouTube Data API Example";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String API_KEY = "YOUR_API_KEY"; // Substitua pela sua chave de API

    private static YouTube getService() throws GeneralSecurityException, IOException {
        return new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, request -> {})
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
