// package com.auth.center.common.web.feign;
//
// import com.auth.center.common.dto.MultiResponse;
// import com.auth.center.common.dto.SingleResponse;
// import com.auth.center.role.application.dto.RoleDTO;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.*;
//
// import java.util.List;
//
///**
// * 角色服务Feign客户端
// */
//@FeignClient(name = "role-service", path = "/api/role")
//public interface RoleServiceClient {
//    
//    /**
//     * 根据用户ID获取角色列表
//     */
//    @GetMapping("/user/{userId}")
//    MultiResponse<RoleDTO> getRolesByUserId(@PathVariable("userId") Long userId);
//    
//    /**
//     * 根据角色ID获取角色信息
//     */
//    @GetMapping("/{roleId}")
//    SingleResponse<RoleDTO> getRoleById(@PathVariable("roleId") Long roleId);
//    
//    /**
//     * 根据角色ID获取权限列表
//     */
//    @GetMapping("/{roleId}/permissions")
//    MultiResponse<String> getPermissionsByRoleId(@PathVariable("roleId") Long roleId);
//}