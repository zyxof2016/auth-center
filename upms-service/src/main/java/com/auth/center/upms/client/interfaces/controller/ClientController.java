package com.auth.center.client.interfaces.controller;

import com.auth.center.client.application.dto.ClientDTO;
import com.auth.center.client.application.service.ClientApplicationService;
import com.auth.center.client.domain.enums.ClientStatus;
import com.auth.center.common.dto.PageResponse;
import com.auth.center.common.dto.Response;
import com.auth.center.common.dto.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 客户端管理控制器
 */
@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientApplicationService clientApplicationService;
    
    /**
     * 创建客户端
     */
    @PostMapping
    public SingleResponse<ClientDTO> createClient(@RequestBody ClientDTO clientDTO) {
        return clientApplicationService.createClient(clientDTO);
    }
    
    /**
     * 更新客户端
     */
    @PutMapping("/{id}")
    public SingleResponse<ClientDTO> updateClient(@PathVariable Long id, @RequestBody ClientDTO clientDTO) {
        return clientApplicationService.updateClient(id, clientDTO);
    }
    
    /**
     * 获取客户端详情
     */
    @GetMapping("/{id}")
    public SingleResponse<ClientDTO> getClientById(@PathVariable Long id) {
        return clientApplicationService.getClientById(id);
    }
    
    /**
     * 分页查询客户端列表
     */
    @GetMapping
    public PageResponse<ClientDTO> getClientPage(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) ClientStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return clientApplicationService.getClientPage(tenantId, clientName, status, page, size);
    }
    
    /**
     * 启用客户端
     */
    @PutMapping("/{id}/enable")
    public Response enableClient(@PathVariable Long id) {
        return clientApplicationService.enableClient(id);
    }
    
    /**
     * 禁用客户端
     */
    @PutMapping("/{id}/disable")
    public Response disableClient(@PathVariable Long id) {
        return clientApplicationService.disableClient(id);
    }
    
    /**
     * 删除客户端
     */
    @DeleteMapping("/{id}")
    public Response deleteClient(@PathVariable Long id) {
        return clientApplicationService.deleteClient(id);
    }
    
    /**
     * 根据客户端ID获取客户端信息
     */
    @GetMapping("/client-id/{clientId}")
    public SingleResponse<ClientDTO> getClientByClientId(@PathVariable String clientId) {
        return clientApplicationService.getClientByClientId(clientId);
    }
}