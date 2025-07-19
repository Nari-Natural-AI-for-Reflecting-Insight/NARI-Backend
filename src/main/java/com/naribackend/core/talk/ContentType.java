package com.naribackend.core.talk;

import com.naribackend.support.error.CoreException;
import com.naribackend.support.error.ErrorType;

import java.util.Locale;

public enum ContentType {
    MESSAGE,
    INPUT_TEXT, // User's input in text format
    INPUT_AUDIO, // User's input in text format
    AUDIO; // Assistant's response in audio format

    public static ContentType from(String contentTypeStr) {
        if (contentTypeStr == null || contentTypeStr.isBlank()) {
            throw new CoreException(ErrorType.INVALID_CONTENT_TYPE);
        }

        try {
            return ContentType.valueOf(contentTypeStr.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new CoreException(ErrorType.INVALID_CONTENT_TYPE);
        }
    }
}
