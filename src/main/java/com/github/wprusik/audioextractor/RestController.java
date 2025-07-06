package com.github.wprusik.audioextractor;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Requires(property = "audioextractor.rest.enabled", value = "true", defaultValue = "false")
@Controller("/api")
public class RestController {

    private final static Logger log = LoggerFactory.getLogger(RestController.class);

    @Inject
    private AudioExtractor extractor;

    @Get("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String healthcheck() {
        log.trace("Received healthcheck");
        return "ok";
    }

    @Post(value = "/video/extract-audio{?format}", consumes = "multipart/form-data")
    public StreamedFile extractAudio(@Part CompletedFileUpload file, @Nullable @QueryValue String format) throws IOException, InterruptedException {
        log.info("Extracting audio from {} bytes of video", file.getSize());
        return extractor.extractAudio(file.getInputStream(), format);
    }
}
