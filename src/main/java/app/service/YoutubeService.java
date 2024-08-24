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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
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
    public byte[] downloadVideoAndAudioFromID(String videoID) throws Exception {
        String directoryAudio = downloadAudioFromID(videoID);
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

            System.out.println("Maior mesmo: " + highestResolutionStream +  " | " + highestResolutionStream.getResolution() + ": " + highestResolutionStream.getCodecs());
            if (highestResolutionStream != null) {
                File tempVideoFile = new File("video");
                highestResolutionStream.download(tempVideoFile.getAbsolutePath(), "");

                File convertedFile = new File(yt.getTitle());
                List<String> ffmpegCommand = Arrays.asList(
                        "ffmpeg",
                        "-i", tempVideoFile.getAbsolutePath() + ".mp4",
                        "-i", directoryAudio + ".mp4",    // Caminho do arquivo de áudio
                        "-c:v", "libx264",      // Codec de vídeo
                        "-preset", "superfast", // Configuração de codificação
                        "-b:v", "1M",           // Taxa de bits para vídeo
                        "-vf", "scale=1280:-1", // Redimensiona para 1280 pixels de largura, mantendo a proporção
                        "-c:a", "aac",          // Codec de áudio
                        "-b:a", "128k",         // Taxa de bits para áudio
                        "-map", "0:v:0",        // Mapeia o vídeo do primeiro arquivo de entrada
                        "-map", "1:a:0",        // Mapeia o áudio do segundo arquivo de entrada
                        convertedFile.getAbsolutePath() + ".mp4"          // Caminho do arquivo de saída
                );
                // Executa o comando FFmpeg
                ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                // Imprime a saída do processo FFmpeg
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                // Espera o processo FFmpeg terminar
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Combinação de áudio e vídeo concluída com sucesso!");
                } else {
                    System.err.println("Erro durante a combinação de áudio e vídeo.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro " + e);
        }
        return null;
    }

    public String downloadAudioFromID(String videoID) throws Exception {
        String videoUrl = "https://www.youtube.com/watch?v=" + videoID;
        Youtube yt = new Youtube(videoUrl);

        List<Stream> streams = yt.streams().getAll()
                .stream().toList();

        Stream highestQualityAudioStream = streams.stream()
                .filter(s -> s.getAudioCodec() != null && s.getAudioCodec().startsWith("mp4a"))
                .findFirst()
                .orElse(null);

        if (highestQualityAudioStream != null) {
            File audioFile = new File("audio"); // ou outro formato apropriado
            highestQualityAudioStream.download(audioFile.getAbsolutePath(), "");
            System.out.println("Áudio baixado: " + audioFile.getAbsolutePath());
            return audioFile.getAbsolutePath();
        }
        return null;
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

            System.out.println("Maior mesmo: " + highestResolutionStream +  " | " + highestResolutionStream.getResolution() + ": " + highestResolutionStream.getCodecs());
            if (highestResolutionStream != null) {
                File tempVideoFile = new File("video");
                highestResolutionStream.download(tempVideoFile.getAbsolutePath(), "");

                File convertedFile = new File(yt.getTitle());
                List<String> ffmpegCommand = Arrays.asList(
                        "ffmpeg",
                        "-i",  tempVideoFile.getAbsolutePath() + ".mp4",
                        "-c:v", "libx264",
                        "-preset", "superfast", // Define a configuração de codificação como "rápida"
                        "-b:v", "1M", // Define a taxa de bits para 1 Mbps
                        "-vf", "scale=1280:-1", // Redimensiona para 1280 pixels de largura, mantendo a proporção
                        "-c:a", "aac",
                        convertedFile.getAbsolutePath() + ".mp4"
                );
                // Executa o comando FFmpeg
                ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                // Imprime a saída do processo FFmpeg
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                // Espera o processo FFmpeg terminar
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Conversão concluída com sucesso!");
                } else {
                    System.err.println("Erro durante a conversão do vídeo.");
                }
                downloadAudioFromID(videoID);
            }
        } catch (Exception e) {
            System.out.println("Erro " + e);
        }
        return null;
    }
}
