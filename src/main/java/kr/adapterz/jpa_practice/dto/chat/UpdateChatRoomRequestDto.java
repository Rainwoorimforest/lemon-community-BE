package kr.adapterz.jpa_practice.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class UpdateChatRoomRequestDto {

    @Size(max=15, message = "제목은 15자를 넘을 수 없습니다.")
    private String title;

    @Size(max = 30, message = "요약은 30자를 넘을 수 없습니다.")
    private String summary;

    private String notice;

}


