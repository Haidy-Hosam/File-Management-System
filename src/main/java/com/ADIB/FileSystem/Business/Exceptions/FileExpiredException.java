package com.ADIB.FileSystem.Business.Exceptions;

public class FileExpiredException extends RuntimeException {
    public FileExpiredException(String message) {
        super(message);
    }
}
