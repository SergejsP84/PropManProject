package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.communication.CommunicationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.communication.SendMessageDTO;
import java.security.Principal;
import java.util.List;

public interface CommunicationService {
    void sendMessage(SendMessageDTO messageDTO, Principal principal);
    List<CommunicationDTO> getMessagesBySender(Long senderId);
    List<CommunicationDTO> getMessagesByReceiver(Long receiverId);
    List<CommunicationDTO> getConversationBetweenUsers(Long user1Id, Long user2Id, Principal principal);
}
