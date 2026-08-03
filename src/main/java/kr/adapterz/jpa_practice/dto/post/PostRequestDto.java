package kr.adapterz.jpa_practice.dto.post;

import jakarta.validation.constraints.NotBlank;
import kr.adapterz.jpa_practice.dto.chat.CreateChatRoomRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostRequestDto {
   @NotBlank(message = "제목이 비었습니다.")
   private String title;

   @NotBlank(message = "내용을 입력해주세요")
   private String content;

   private List<@NotBlank(message = "이미지 URL은 비어있을 수 없습니다.") String> images;

   private CreateChatRoomRequestDto chatRoom; // null이 들어올 수 있음

}
