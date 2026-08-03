package kr.adapterz.jpa_practice.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomNoticeRequestDto {

    private String notice;

    @NotNull
    private Long postId;

}

