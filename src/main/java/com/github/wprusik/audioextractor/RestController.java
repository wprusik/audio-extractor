package com.github.wprusik.audioextractor;

import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Controller("/api")
public class RestController {

    private final static Logger log = LoggerFactory.getLogger(RestController.class);

    @Inject
    private AudioExtractor extractor;

    @Post(value = "/extract-audio", consumes = "multipart/form-data")
    public StreamedFile extractAudio(@Part CompletedFileUpload file) throws IOException, InterruptedException {
        log.info("Extracting audio from {} bytes of video", file.getSize());
        return extractor.extractAudio(file.getInputStream());
    }
}
