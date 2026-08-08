package kr.adapterz.jpa_practice.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {

    @NotBlank
    private String messageId;

    @NotBlank
    private String message;

    private Long senderId; // 웹소켓 우회 등으로 Principal이 없을 때를 대비한 폴백용

}
