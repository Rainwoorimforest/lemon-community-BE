package kr.adapterz.jpa_practice.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetparticipantListDto {
    private List<GetParticitpantDto> participants;

    public GetparticipantListDto(List<GetParticitpantDto> participants) {
        this.participants = participants;
    }
}
