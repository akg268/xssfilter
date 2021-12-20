package com.example.sanitizeinputdemo.exceptions;

public class XSSException extends RuntimeException{

    public XSSException(String msg){
        super(msg);
    }
    
}
