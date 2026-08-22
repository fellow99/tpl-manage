package org.fellow99.tpl.manage.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.model.LoginUser;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysPermissionService;
import org.dromara.system.service.ISysUserService;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SaIgnore
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ISysUserService userService;
    private final ISysPermissionService permissionService;

    @Data
    public static class LoginRequest implements Serializable {
        private String username;
        private String password;
        private String code;
        private String uuid;
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest req) {
        if (StringUtils.isBlank(req.getUsername()) || StringUtils.isBlank(req.getPassword())) {
            return R.fail("用户名或密码不能为空");
        }

        SysUserVo user = userService.selectUserByUserName(req.getUsername());
        if (ObjectUtil.isNull(user)) {
            return R.fail("用户不存在");
        }
        if (!BCrypt.checkpw(req.getPassword(), user.getPassword())) {
            return R.fail("密码错误");
        }
        if ("1".equals(user.getStatus())) {
            return R.fail("用户已被停用");
        }

        Long userId = user.getUserId();

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUserType("sys_user");
        loginUser.setClientKey("e5cd7e4891bf95d1d19206ce24a7b32e");
        // 加载角色权限和菜单权限
        loginUser.setRolePermission(permissionService.getRolePermission(userId));
        loginUser.setMenuPermission(permissionService.getMenuPermission(userId));

        SaLoginParameter param = new SaLoginParameter()
            .setDevice("pc")
            .setExtra(LoginHelper.CLIENT_KEY, "e5cd7e4891bf95d1d19206ce24a7b32e");
        LoginHelper.login(loginUser, param);

        Map<String, Object> result = new HashMap<>();
        result.put("access_token", StpUtil.getTokenValue());
        return R.ok(result);
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }
}
