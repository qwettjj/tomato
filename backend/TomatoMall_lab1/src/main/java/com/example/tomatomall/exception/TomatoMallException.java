
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

    public static TomatoMallException cartItemNotFound() {return new TomatoMallException("未在购物车中找到该物品");}

    public static TomatoMallException alreadyJoinCircle() {return new TomatoMallException("您已加入该圈子");}

    public static TomatoMallException circleNotExist() {return new TomatoMallException("圈子不存在");}

    public static TomatoMallException NotCircleMember() {return new TomatoMallException("您不是该圈子成员");}

    public static TomatoMallException postNotExist() {return new TomatoMallException("帖子不存在或已经被删除");}

    public static TomatoMallException alreadyLike() {return new TomatoMallException("您已点赞过该帖子");}

    public static TomatoMallException notAlreadyLike() {return new TomatoMallException("您未点赞过该帖子");}

    public static TomatoMallException commentNotExist() {return new TomatoMallException("评论不存在");}

    public static TomatoMallException userNotInCircle() {return new TomatoMallException("用户不是该圈子的成员");}

    public static TomatoMallException operatorNotInCircle() {return new TomatoMallException("操作者不属于这个圈子");}

    public static TomatoMallException noAuthority() {return new TomatoMallException("无权操作");}

    public static TomatoMallException isNotAdmin() {return new TomatoMallException("目标不是管理员");}

    public static TomatoMallException orderNotExists() { return new TomatoMallException("订单不存在");}

    public static TomatoMallException historyNotExists() { return new TomatoMallException("购买历史不存在");}

    public static TomatoMallException permissionDenied() { return new TomatoMallException("该用户无权访问");
    }

    public static TomatoMallException insufficientStock(String s) {return new TomatoMallException(s);}
}

