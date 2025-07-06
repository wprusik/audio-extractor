package com.github.wprusik.audioextractor;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.server.types.files.StreamedFile;
import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

@Singleton
public class AudioExtractor {

    @Value("${FFMPEG_EXECUTABLE_COMMAND:ffmpeg}")
    private String ffmpegExecutableCommand;

    public synchronized StreamedFile extractAudio(InputStream videoInputStream, Format format) throws IOException, InterruptedException {
        Path videoFile = saveToTempFile(videoInputStream);
        Path audioFile = extractAudio(videoFile, format);
        InputStream is = new AutoDeleteFileInputStream(audioFile);
        return new StreamedFile(is, format.getMediaType());
    }

    private Path extractAudio(Path videoFile, Format format) throws IOException, InterruptedException {
        Path audioFile = createTempFile(format.getExtension());
        doExtract(videoFile, audioFile, format);
        return audioFile;
    }

    private void doExtract(Path videoFile, Path outputFile, Format format) throws IOException, InterruptedException {
        ProcessBuilder pb = createFfmpegProcess(videoFile, outputFile, format);
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

    private ProcessBuilder createFfmpegProcess(Path videoFile, Path outputFile, Format format) {
        String inputVideo = videoFile.toAbsolutePath().toString();
        String outputAudio = outputFile.toAbsolutePath().toString();
        String command = ffmpegExecutableCommand != null ? ffmpegExecutableCommand : "ffmpeg";

        List<String> args = new ArrayList<>(asList(command, "-y", "-i", inputVideo, "-map", "a"));
        if (format == Format.MP3) {
            args.add("-q:a");
            args.add("0");
        }
        args.add(outputAudio);
        return new ProcessBuilder(args);
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
