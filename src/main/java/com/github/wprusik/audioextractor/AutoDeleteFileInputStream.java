package com.github.wprusik.audioextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class AutoDeleteFileInputStream extends FileInputStream {

    private final File file;
    private boolean deleted = false;

    public AutoDeleteFileInputStream(File file) throws IOException {
        super(file);
        this.file = file;
        this.file.deleteOnExit();
    }

    public AutoDeleteFileInputStream(Path path) throws IOException {
        this(path.toFile());
    }

    private void deleteFileSafely() throws IOException {
        if (!deleted && available() == 0) {
            try {deleted = file.delete();} catch (Exception ignored) {}
        }
    }

    @Override
    public int read() throws IOException {
        int result = super.read();
        if (result == -1) {
            deleteFileSafely();
        }
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = super.read(b, off, len);
        if (bytesRead == -1) {
            deleteFileSafely();
        }
        return bytesRead;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int bytesRead = super.read(b);
        if (bytesRead == -1) {
            deleteFileSafely();
        }
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        super.close();
        deleteFileSafely();
    }
}
