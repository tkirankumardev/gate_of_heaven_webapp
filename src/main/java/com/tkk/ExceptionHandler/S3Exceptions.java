package com.tkk.ExceptionHandler;

public class S3Exceptions extends RuntimeException{

    public S3Exceptions(String message, Throwable cause){
        super(message, cause);
    }

    public S3Exceptions(String message) {
        super(message);
    }

}
