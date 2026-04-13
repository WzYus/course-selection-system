package cn.detect.cs.common;

public class MessageConstants {
    // 认证相关
    public static final String USERNAME_EXISTS = "用户名已存在";
    public static final String USERNAME_OR_PASSWORD_ERROR = "用户名或密码错误";
    public static final String ACCESS_DENIED = "权限不足";

    // 学生相关
    public static final String STUDENT_NOT_FOUND = "学生不存在";
    public static final String STUDENT_NAME_REQUIRED = "学生姓名不能为空";
    public static final String STUDENT_AGE_INVALID = "年龄必须大于0";

    // 通用
    public static final String SERVER_ERROR = "服务器内部错误";
    public static final String USERNAME_NOT_FOUND ="用户不存在: ";
    /** 旧密码不能为空 */
    public static final String OLD_PASSWORD_NOT_BLANK = "旧密码不能为空";

    /** 新密码不能为空 */
    public static final String NEW_PASSWORD_NOT_BLANK = "新密码不能为空";

    /** 新密码至少6位 */
    public static final String NEW_PASSWORD_SIZE = "新密码至少6位";
}