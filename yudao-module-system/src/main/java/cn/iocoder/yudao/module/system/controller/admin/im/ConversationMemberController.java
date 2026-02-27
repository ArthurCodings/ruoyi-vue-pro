package cn.iocoder.yudao.module.system.controller.admin.im;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.member.ConversationMemberInviteReqVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.member.ConversationMemberListRespVO;
import cn.iocoder.yudao.module.system.controller.admin.im.vo.member.ConversationMemberRemoveReqVO;
import cn.iocoder.yudao.module.system.service.im.conversationmember.ConversationMemberService;
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

@Tag(name = "管理后台 - IM 会话成员")
@RestController
@RequestMapping("/system/im/conversation-member")
@Validated
public class ConversationMemberController {

    @Resource
    private ConversationMemberService conversationMemberService;

    @GetMapping("/list/{conversationId}")
    @Operation(summary = "查询群聊成员列表")
    @Parameter(name = "conversationId", description = "会话ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:im:query')")
    public CommonResult<List<ConversationMemberListRespVO>> listMembers(
            @PathVariable("conversationId") Long conversationId) {
        return success(conversationMemberService.listMembers(conversationId, getLoginUserId()));
    }

    @PostMapping("/invite")
    @Operation(summary = "邀请成员加入群聊")
    @PreAuthorize("@ss.hasPermission('system:im:manage-member')")
    public CommonResult<Boolean> inviteMembers(@Valid @RequestBody ConversationMemberInviteReqVO reqVO) {
        conversationMemberService.inviteMembers(reqVO.getConversationId(), reqVO.getUserIds(), getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/remove")
    @Operation(summary = "踢出群聊成员（群主/管理员）")
    @PreAuthorize("@ss.hasPermission('system:im:manage-member')")
    public CommonResult<Boolean> removeMember(@Valid @RequestBody ConversationMemberRemoveReqVO reqVO) {
        conversationMemberService.removeMember(reqVO.getConversationId(), reqVO.getUserId(), getLoginUserId());
        return success(true);
    }

    @PutMapping("/read/{conversationId}")
    @Operation(summary = "标记会话消息全部已读")
    @Parameter(name = "conversationId", description = "会话ID", required = true)
    @PreAuthorize("@ss.hasPermission('system:im:query')")
    public CommonResult<Boolean> markRead(@PathVariable("conversationId") Long conversationId) {
        conversationMemberService.markRead(conversationId, getLoginUserId());
        return success(true);
    }

}
