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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
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

    public app.entity.Video returnVideoInfos(String videoID){
        try {
            YouTube youtubeService = getService();
            YouTube.Videos.List request = youtubeService.videos()
                    .list("snippet,contentDetails,statistics");
            request.setId(videoID); // Substitua pelo ID do vídeo do YouTube
            request.setKey(API_KEY);

            VideoListResponse response = request.execute();
            app.entity.Video videoRetorno = new app.entity.Video();
            for (Video video : response.getItems()) {
                videoRetorno.setTitleVideo(video.getSnippet().getTitle());
                videoRetorno.setChannelName(video.getSnippet().getChannelTitle());
                videoRetorno.setImageLink(video.getSnippet().getThumbnails().getDefault().getUrl());
                videoRetorno.setLikes(video.getStatistics().getLikeCount());
                videoRetorno.setComments(video.getStatistics().getCommentCount());
                videoRetorno.setViews(video.getStatistics().getViewCount());
            }

            return videoRetorno;
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public byte[] downloadVideoAndAudioFromID(String videoID) throws Exception {
        System.out.println("Vai se foder2446161 " + videoID);

        File audioFile = downloadAudioFromID(videoID); // Supondo que downloadAudioFromID também detecta e usa a extensão correta.
        try {
            System.out.println("Vai se foder244 ");

            String videoUrl = "https://www.youtube.com/watch?v=" + videoID;
            Youtube yt = new Youtube(videoUrl);

            List<Stream> streams = yt.streams().getAll().stream().toList();

            Stream highestResolutionStream = streams.stream()
                    .max(Comparator.comparingInt(s -> {
                        String resolution = s.getResolution();
                        if (resolution != null && resolution.matches("\\d+p")) {
                            return Integer.parseInt(resolution.replace("p", ""));
                        }
                        return 0;
                    }))
                    .orElse(null);

            if (highestResolutionStream != null) {
                String videoDownloadUrl = highestResolutionStream.getUrl();

                String videoExtension = getExtensionFromUrl(videoDownloadUrl);
                if (videoExtension == null || videoExtension.isEmpty()) {
                    videoExtension = "mp4";
                }

                File tempVideoFile = new File("video");
                highestResolutionStream.download(tempVideoFile.getAbsolutePath(), "");

                String sanitizedTitle = yt.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
                File convertedFile = new File(sanitizedTitle);

                File ffmpegFile = extractFfmpegFromClasspath();

                List<String> ffmpegCommand = Arrays.asList(
                        ffmpegFile.getAbsolutePath(),
                        "-i", tempVideoFile.getAbsolutePath() + '.' + videoExtension, // Usa a extensão detectada
                        "-i", audioFile.getAbsolutePath() + ".mp4",    // Caminho do arquivo de áudio (assumindo que já está correto)
                        "-c:v", "libx264",      // Codec de vídeo
                        "-preset", "superfast", // Configuração de codificação
                        "-b:v", "1M",           // Taxa de bits para vídeo
                        "-vf", "scale=1280:-1", // Redimensiona para 1280 pixels de largura, mantendo a proporção
                        "-c:a", "aac",          // Codec de áudio
                        "-b:a", "128k",         // Taxa de bits para áudio
                        "-map", "0:v:0",        // Mapeia o vídeo do primeiro arquivo de entrada
                        "-map", "1:a:0",        // Mapeia o áudio do segundo arquivo de entrada
                        convertedFile.getAbsolutePath() + ".mp4" // Caminho do arquivo de saída final em .mp4
                );

                ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Combinação de áudio e vídeo concluída com sucesso! " + convertedFile.toPath());
                    Path pathToVideo = Path.of(convertedFile.getAbsolutePath() + ".mp4");
                    byte[] videoBytes = Files.readAllBytes(pathToVideo);
                    Files.deleteIfExists(pathToVideo);

                    return videoBytes;
                } else {
                    System.err.println("Erro durante a combinação de áudio e vídeo.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro " + e);
        }
        return null;
    }




    public File downloadAudioFromID(String videoID) throws Exception {
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
            return audioFile;
        }
        return null;
    }

    private String getExtensionFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();
            int lastDotIndex = path.lastIndexOf('.');
            if (lastDotIndex >= 0) {
                return path.substring(lastDotIndex + 1);
            }
        } catch (MalformedURLException e) {
            System.err.println("Erro ao analisar a URL: " + e.getMessage());
        }
        return null;
    }

    public byte[] downloadVideoFromID(String videoID) throws InterruptedException, IOException {
        try {
            String videoUrl = "https://www.youtube.com/watch?v=" + videoID;
            Youtube yt = new Youtube(videoUrl);

            List<Stream> streams = yt.streams().getAll()
                    .stream().toList();

            Stream highestResolutionStream = streams.stream()
                    .max(Comparator.comparingInt(s -> {
                        String resolution = s.getResolution();
                        if (resolution != null && resolution.matches("\\d+p")) {
                            return Integer.parseInt(resolution.replace("p", ""));
                        }
                        return 0;
                    }))
                    .orElse(null);

            if (highestResolutionStream != null) {
                File tempVideoFile = new File("video.mp4");
                highestResolutionStream.download(tempVideoFile.getAbsolutePath(), "");

                if (!tempVideoFile.exists() || tempVideoFile.length() == 0) {
                    System.err.println("Erro: o vídeo não foi baixado corretamente.");
                    return null;
                }

                String sanitizedTitle = yt.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
                File convertedFile = new File(sanitizedTitle + "_converted.mp4");

                File ffmpegFile = extractFfmpegFromClasspath();

                List<String> ffmpegCommand = Arrays.asList(
                        ffmpegFile.getAbsolutePath(),
                        "-i", tempVideoFile.getAbsolutePath(),
                        "-c:v", "libx264",
                        "-preset", "superfast",
                        "-b:v", "1M",
                        "-vf", "scale=1280:-1",
                        "-c:a", "aac",
                        convertedFile.getAbsolutePath()
                );

                ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    System.out.println("Conversão concluída com sucesso!");

                    Path pathToVideo = Path.of(convertedFile.getAbsolutePath());
                    byte[] videoBytes = Files.readAllBytes(pathToVideo);
                    Files.deleteIfExists(tempVideoFile.toPath());  // Limpa o arquivo temporário
                    return videoBytes;
                } else {
                    System.err.println("Erro durante a conversão do vídeo.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro " + e);
        }
        return null;
    }


    private static File extractFfmpegFromClasspath() throws IOException {
        String ffmpegFileName = System.getProperty("os.name").toLowerCase().contains("win") ? "ffmpeg.exe" : "ffmpeg";
        try (InputStream inputStream = YoutubeService.class.getClassLoader().getResourceAsStream("ffmpeg/" + ffmpegFileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("O arquivo ffmpeg não foi encontrado no classpath.");
            }

            File tempFfmpegFile = File.createTempFile("ffmpeg", ffmpegFileName);
            tempFfmpegFile.deleteOnExit();

            try (FileOutputStream outputStream = new FileOutputStream(tempFfmpegFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return tempFfmpegFile;
        }
    }
}
