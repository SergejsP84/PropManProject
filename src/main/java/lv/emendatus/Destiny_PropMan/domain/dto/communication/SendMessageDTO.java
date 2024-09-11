package lv.emendatus.Destiny_PropMan.domain.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageDTO {
    private Long receiverId; // ID of the receiver (of the opposite type)
    private String message;
}
