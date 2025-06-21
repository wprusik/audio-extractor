package com.github.wprusik.audioextractor;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.MediaType;
import io.micronaut.http.server.types.files.StreamedFile;
import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

@Singleton
public class AudioExtractor {

    @Value("${FFMPEG_EXECUTABLE_COMMAND}")
    private String ffmpegExecutableCommand;

    public synchronized StreamedFile extractAudio(InputStream videoInputStream) throws IOException, InterruptedException {
        Path videoFile = saveToTempFile(videoInputStream);
        Path audioFile = extractAudio(videoFile);
        InputStream is = new AutoDeleteFileInputStream(audioFile);
        return new StreamedFile(is, MediaType.of("audio/mpeg"));
    }

    private Path extractAudio(Path videoFile) throws IOException, InterruptedException {
        Path audioFile = createTempFile("mp3");
        doExtract(videoFile, audioFile);
        return audioFile;
    }

    private void doExtract(Path videoFile, Path outputFile) throws IOException, InterruptedException {
        String inputVideo = videoFile.toAbsolutePath().toString();
        String outputAudio = outputFile.toAbsolutePath().toString();
        String command = ffmpegExecutableCommand != null ? ffmpegExecutableCommand : "ffmpeg";
        ProcessBuilder pb = new ProcessBuilder(
                command, "-y", "-i", inputVideo, "-q:a", "0", "-map", "a", outputAudio
        );
        pb.inheritIO();
        pb.redirectErrorStream(true);

        Process process = pb.start();

        boolean finished = process.waitFor(3, TimeUnit.MINUTES);
        if (finished && process.exitValue() == 0) {
            System.out.println("Audio extracted.");
        } else {
            throw new IllegalStateException("Audio extraction failed");
        }
    }

    private Path saveToTempFile(InputStream videoInputStream) throws IOException {
        Path temp = createTempFile("mp4");
        try (InputStream in = videoInputStream) {
            Files.copy(in, temp, StandardCopyOption.REPLACE_EXISTING);
        }
        return temp;
    }

    private Path createTempFile(String extension) throws IOException {
        File tempFile = Files.createTempFile("tmp_", "." + extension).toFile();
        tempFile.deleteOnExit();
        return tempFile.toPath();
    }
}
