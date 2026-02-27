package cn.iocoder.yudao.module.system.controller.admin.im.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Schema(description = "管理后台 - 群聊消息已读/未读详情 Response VO")
@Data
public class MessageReadStatusRespVO {

    @Schema(description = "已读人数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer readCount;

    @Schema(description = "未读人数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer unreadCount;

    @Schema(description = "总成员数（不含发送者）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCount;

    @Schema(description = "已读用户列表")
    private List<UserSimpleVO> readUserList;

    @Schema(description = "未读用户列表")
    private List<UserSimpleVO> unreadUserList;

    @Data
    @Schema(description = "用户简要信息")
    public static class UserSimpleVO {

        @Schema(description = "用户ID")
        private Long userId;

        @Schema(description = "昵称")
        private String nickname;

        @Schema(description = "头像")
        private String avatar;

    }

}
