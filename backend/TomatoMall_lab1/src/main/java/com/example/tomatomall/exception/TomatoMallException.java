
package com.example.tomatomall.exception;

public class TomatoMallException extends RuntimeException {
    public TomatoMallException(String message) {
        super(message);
    }

    public static TomatoMallException phoneAlreadyExists() { return new TomatoMallException("手机号已经存在"); }

    public static TomatoMallException phoneOrPasswordError() { return new TomatoMallException("手机号或密码错误");}

    public static TomatoMallException accountNotFound() {return new TomatoMallException("未找到该账号");}

    public static TomatoMallException productAlreadyExists() { return new TomatoMallException("该商品已经存在");}

    public static TomatoMallException productToChangeNotFound() {return new TomatoMallException("未找到要修改或删除的商品");}

    public static TomatoMallException productNotFound() { return new TomatoMallException("商品不存在");}

    public static TomatoMallException fileUploadFail() { return new TomatoMallException("图片文件上传失败");}

    public static TomatoMallException notLogin() { return new TomatoMallException("未登录");}

    public static TomatoMallException test() {return new TomatoMallException("test");}
}

