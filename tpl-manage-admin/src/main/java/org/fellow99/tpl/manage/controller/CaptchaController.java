package org.fellow99.tpl.manage.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.config.properties.CaptchaProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * tpl-workspace管理后台 - 验证码控制器
 * 测试环境已禁用验证码功能
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
public class CaptchaController {

    private final CaptchaProperties captchaProperties;

    /**
     * 获取图片验证码（测试环境已禁用）
     */
    @GetMapping("/auth/code")
    public R<CaptchaVo> getCode() {
        return R.ok(new CaptchaVo(false, null, null));
    }

    public record CaptchaVo(Boolean captchaEnabled, String uuid, String img) {
    }
}
