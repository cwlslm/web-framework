package com.jfeng.framework.security;

/**
 * 授权类型
 */
public enum AuthzType {
    // 任意访问
    ANON,
    // 需要登陆
    AUTHC,
    // 需要权限
    PERMS
}
