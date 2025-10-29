// package com.auth.center.common.web.feign;
//
// import com.auth.center.common.dto.Response;
// import com.auth.center.log.application.dto.LoginLogDTO;
// import com.auth.center.log.application.dto.OperationLogDTO;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.*;
//
///**
// * 日志服务Feign客户端
// */
//@FeignClient(name = "log-service", path = "/api/log")
//public interface LogServiceClient {
//    
//    /**
//     * 记录登录日志
//     */
//    @PostMapping("/login")
//    Response recordLoginLog(@RequestBody LoginLogDTO loginLogDTO);
//    
//    /**
//     * 记录操作日志
//     */
//    @PostMapping("/operation")
//    Response recordOperationLog(@RequestBody OperationLogDTO operationLogDTO);
//    
//    /**
//     * 根据用户ID查询登录日志
//     */
//    @GetMapping("/login/user/{userId}")
//    Response getLoginLogsByUserId(@PathVariable("userId") Long userId,
//                                  @RequestParam(value = "page", defaultValue = "1") int page,
//                                  @RequestParam(value = "size", defaultValue = "10") int size);
//}