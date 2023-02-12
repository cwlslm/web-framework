package com.jfeng.framework.security.bean;

import com.jfeng.framework.core.bean.BaseBean;
import com.jfeng.framework.security.AuthzType;

import java.util.regex.Pattern;

/**
 * 规则Bean
 * 保存一个规则的url和所需的授权类型
 */
public class Rule extends BaseBean {

    private String url;

    private AuthzType authzType;

    private Pattern pattern;

    public Rule(String url, AuthzType authzType) {
        this.url = url;
        this.authzType = authzType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrlPattern(String url) {
        this.url = url;
    }

    public AuthzType getAuthzType() {
        return authzType;
    }

    public void setAuthzType(AuthzType authzType) {
        this.authzType = authzType;
    }

    public void compile() {
        pattern = Pattern.compile(url);
    }

    public Pattern getPattern() {
        return pattern;
    }
}
