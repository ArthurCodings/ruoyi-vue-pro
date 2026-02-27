package cn.iocoder.yudao.module.system.controller.admin.im.vo.conversation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 我的会话列表 Response VO")
@Data
public class ConversationListRespVO {

    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "类型：1=单聊 2=群聊", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    @Schema(description = "会话名称（单聊时为对方昵称，群聊为群名）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "会话头像（单聊时为对方头像，群聊为群头像）")
    private String avatar;

    @Schema(description = "群主ID（单聊为0）")
    private Long ownerId;

    @Schema(description = "群公告")
    private String notice;

    @Schema(description = "最后一条消息摘要")
    private String lastMessageContent;

    @Schema(description = "最后一条消息时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "未读消息数")
    private Long unreadCount;

    @Schema(description = "单聊时：对方用户ID")
    private Long otherUserId;

    @Schema(description = "是否置顶")
    private Boolean isPinned;

    @Schema(description = "是否免打扰")
    private Boolean isDisturb;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
