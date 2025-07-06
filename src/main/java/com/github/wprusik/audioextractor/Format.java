package com.github.wprusik.audioextractor;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;

public enum Format {
    MP3("audio/mpeg"),
    WAV("audio/wav");

    private final String mimeType;

    Format(String mimeType) {
        this.mimeType = mimeType;
    }

    public MediaType getMediaType() {
        return MediaType.of(mimeType);
    }

    public String getExtension() {
        return name().toLowerCase();
    }

    public static Format parse(@Nullable String format, String def) {
        return format != null ? valueOf(format.toUpperCase()) : valueOf(def);
    }
}
