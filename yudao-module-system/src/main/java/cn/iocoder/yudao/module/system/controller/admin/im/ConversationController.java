package cn.iocoder.yudao.module.system.controller.admin.im;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation.ConversationCreateGroupReqVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation.ConversationCreateSingleReqVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation.ConversationListRespVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation.ConversationUpdateNameReqVO;
import cn.iocoder.yudao.module.system.service.im.conversation.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 会话")
@RestController
@RequestMapping("/system/im/conversation")
@Validated
public class ConversationController {

    @Resource
    private ConversationService conversationService;

    @GetMapping("/list")
    @Operation(summary = "获取我的会话列表（含未读数）")
    @PreAuthorize("@ss.hasPermission('system:im:query')")
    public CommonResult<List<ConversationListRespVO>> getMyConversationList() {
        return success(conversationService.getMyConversationList(getLoginUserId()));
    }

    @PostMapping("/create-single")
    @Operation(summary = "创建或获取单聊会话")
    @PreAuthorize("@ss.hasPermission('system:im:create')")
    public CommonResult<Long> createSingleConversation(@Valid @RequestBody ConversationCreateSingleReqVO reqVO) {
        return success(conversationService.createOrGetSingleConversation(getLoginUserId(), reqVO.getTargetUserId()));
    }

    @PostMapping("/create-group")
    @Operation(summary = "创建群聊会话")
    @PreAuthorize("@ss.hasPermission('system:im:create-group')")
    public CommonResult<Long> createGroupConversation(@Valid @RequestBody ConversationCreateGroupReqVO reqVO) {
        return success(conversationService.createGroupConversation(getLoginUserId(), reqVO));
    }

    @PutMapping("/update-name")
    @Operation(summary = "修改群聊名称")
    @PreAuthorize("@ss.hasPermission('system:im:query')")
    public CommonResult<Boolean> updateConversationName(@Valid @RequestBody ConversationUpdateNameReqVO reqVO) {
        conversationService.updateConversationName(reqVO.getId(), reqVO.getName(), getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/dismiss/{id}")
    @Operation(summary = "解散群聊")
    @Parameter(name = "id", description = "会话ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:im:query')")
    public CommonResult<Boolean> dismissConversation(@PathVariable("id") Long id) {
        conversationService.dismissConversation(id, getLoginUserId());
        return success(true);
    }

}
