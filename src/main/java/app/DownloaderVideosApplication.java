package app;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
public class DownloaderVideosApplication {

	public static void main(String[] args) {
		String fileUrl = "https://drive.usercontent.google.com/download?id=1Mbd2CxcaeSizoV2mcxhXbMpYpZPcL0Ym&export=download&authuser=0&confirm=t&uuid=3027ff5b-e871-4dfe-bf22-c0e7f1f40d7f&at=AO7h07dyZoSMphWNbkiWnWIcH3Dq%3A1724551917513";
		String outputFilePath = "src/main/resources/ffmpeg/ffmpeg.exe";

		// Crie a pasta se ela não existir
		File outputFile = new File(outputFilePath);
		outputFile.getParentFile().mkdirs();

		try {
			downloadFile(fileUrl, outputFilePath);
			System.out.println("Download concluído com sucesso.");

			// Verifique se o arquivo realmente foi salvo
			if (outputFile.exists() && !outputFile.isDirectory()) {
				System.out.println("Arquivo encontrado em: " + outputFile.getAbsolutePath());
			} else {
				System.err.println("Arquivo não encontrado em: " + outputFile.getAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Erro ao fazer o download do arquivo.");
		}
		File diretorioRaiz = new File("src");
		listarArquivosEDiretoriosRecursivamente(diretorioRaiz, "");


		SpringApplication.run(DownloaderVideosApplication.class, args);
	}

	private static void downloadFile(String fileUrl, String outputFilePath) throws IOException {
		URL url = new URL(fileUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.connect();

		try (InputStream in = connection.getInputStream();
			 FileOutputStream out = new FileOutputStream(outputFilePath)) {

			System.out.println("Iniciando download...");

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}

			System.out.println("Download concluído.");
		} catch (IOException e) {
			System.err.println("Erro ao baixar o arquivo: " + e.getMessage());
			throw e;
		}
	}

	public static void listarArquivosEDiretoriosRecursivamente(File diretorio, String indentacao) {
		if (diretorio.exists() && diretorio.isDirectory()) {
			File[] arquivos = diretorio.listFiles();
			if (arquivos != null) {
				for (File arquivo : arquivos) {
					System.out.println(indentacao + (arquivo.isDirectory() ? "[D]" : "[F]") + " " + arquivo.getName());
					// Se for um diretório, listar recursivamente seu conteúdo
					if (arquivo.isDirectory()) {
						listarArquivosEDiretoriosRecursivamente(arquivo, indentacao + "    ");
					}
				}
			}
		} else {
			System.out.println("O diretório não existe ou não é um diretório válido: " + diretorio.getAbsolutePath());

		}
	}
}
