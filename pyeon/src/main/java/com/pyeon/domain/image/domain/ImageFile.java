package com.pyeon.domain.image.domain;

import com.pyeon.global.exception.ImageException;
import com.pyeon.global.exception.ErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ImageFile {

    private static final String EXTENSION_DELIMITER = ".";

    private final MultipartFile file;
    private final String hashedName;

    public ImageFile(final MultipartFile file) {
        validateNullImage(file);
        this.file = file;
        this.hashedName = hashName(file);
    }

    private void validateNullImage(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageException(ErrorCode.NULL_IMAGE);
        }
    }

    private String hashName(final MultipartFile image) {
        final String name = image.getOriginalFilename();
        final String filenameExtension = EXTENSION_DELIMITER + getFilenameExtension(name);
        final String nameAndDate = name + LocalDateTime.now();
        try {
            final MessageDigest hashAlgorithm = MessageDigest.getInstance("SHA3-256");
            final byte[] hashBytes = hashAlgorithm.digest(nameAndDate.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes) + filenameExtension;
        } catch (final NoSuchAlgorithmException e) {
            throw new ImageException(ErrorCode.FAIL_IMAGE_NAME_HASH);
        }
    }

    private String bytesToHex(final byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02x", bytes[i] & 0xff))
                .collect(Collectors.joining());
    }

    private String getFilenameExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    public String getContentType() {
        return this.file.getContentType();
    }

    public long getSize() {
        return this.file.getSize();
    }

    public InputStream getInputStream() throws IOException {
        return this.file.getInputStream();
    }
} 