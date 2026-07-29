package kr.adapterz.jpa_practice.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {

    @NotBlank
    private String sender; // TODO: 추후에 jwt 토큰 넣을거라 삭제하기

    @NotBlank
    private String message;



    // private Long roomId; // 이것도 Pathivarable로 받는다
}
