package com.example.tomatomall.exception;

public class TomatoMallException extends RuntimeException {
    public TomatoMallException(String message) {
        super(message);
    }

    public static TomatoMallException phoneAlreadyExists() { return new TomatoMallException("手机号已经存在"); }

    public static TomatoMallException phoneOrPasswordError() { return new TomatoMallException("手机号或密码错误");}

    public static TomatoMallException accountNotFound() {return new TomatoMallException("未找到该账号");}
}
