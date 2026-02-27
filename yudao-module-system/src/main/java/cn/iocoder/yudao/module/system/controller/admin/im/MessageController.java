package cn.iocoder.yudao.module.system.controller.admin.im;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageReadStatusRespVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageRespVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.message.MessageSendReqVO;
import cn.iocoder.yudao.module.system.service.im.message.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 消息")
@RestController
@RequestMapping("/system/im/message")
@Validated
public class MessageController {

    @Resource
    private MessageService messageService;

    @GetMapping("/list/{conversationId}")
    @Operation(summary = "分页查询会话消息列表（最新消息在前）")
    @Parameter(name = "conversationId", description = "会话ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:im:query')")
    public CommonResult<PageResult<MessageRespVO>> listMessages(
            @PathVariable("conversationId") Long conversationId,
            @Valid PageParam pageParam) {
        return success(messageService.listMessages(conversationId, pageParam, getLoginUserId()));
    }

    @PostMapping("/send")
    @Operation(summary = "发送消息")
    @PreAuthorize("@ss.hasPermission('system:im:send')")
    public CommonResult<Long> sendMessage(@Valid @RequestBody MessageSendReqVO reqVO) {
        return success(messageService.sendMessage(reqVO, getLoginUserId()));
    }

    @PutMapping("/recall/{id}")
    @Operation(summary = "撤回消息（仅发送者可操作）")
    @Parameter(name = "id", description = "消息ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:im:send')")
    public CommonResult<Boolean> recallMessage(@PathVariable("id") Long id) {
        messageService.recallMessage(id, getLoginUserId());
        return success(true);
    }

    @GetMapping("/read-status/{messageId}")
    @Operation(summary = "群聊消息已读/未读详情（发送者专用）")
    @Parameter(name = "messageId", description = "消息ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:im:query')")
    public CommonResult<MessageReadStatusRespVO> getReadStatus(@PathVariable("messageId") Long messageId) {
        return success(messageService.getReadStatus(messageId, getLoginUserId()));
    }

}
