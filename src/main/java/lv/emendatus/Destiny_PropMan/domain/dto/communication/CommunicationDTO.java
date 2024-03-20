package lv.emendatus.Destiny_PropMan.domain.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommunicationDTO {
    private Long id;
    private Long senderId; // ID of the sender (tenant)
    private String senderType; // "Tenant" or "Manager"
    private Long receiverId; // ID of the receiver (host/manager)
    private String receiverType;
    private String message;
    private LocalDateTime sentAt;
    private boolean isRead; // Indicates whether the message has been read by the receiver
}