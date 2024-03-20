package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.dto.communication.CommunicationDTO;
import lv.emendatus.Destiny_PropMan.service.implementation.JpaCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/communication")
public class CommunicationController {
    private final JpaCommunicationService communicationService;

    @Autowired
    public CommunicationController(JpaCommunicationService communicationService) {
        this.communicationService = communicationService;
    }
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody CommunicationDTO messageDTO) {
        communicationService.sendMessage(messageDTO);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getBySender/{senderId}")
    public ResponseEntity<List<CommunicationDTO>> getMessagesBySender(@PathVariable Long senderId) {
        List<CommunicationDTO> messages = communicationService.getMessagesBySender(senderId);
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/getByReceiver/{receiverId}")
    public ResponseEntity<List<CommunicationDTO>> getMessagesByReceiver(@PathVariable Long receiverId) {
        List<CommunicationDTO> messages = communicationService.getMessagesByReceiver(receiverId);
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/getConversation/{user1Id}/{user2Id}")
    public ResponseEntity<List<CommunicationDTO>> getConversationBetweenUsers(
            @PathVariable Long user1Id, @PathVariable Long user2Id) {
        List<CommunicationDTO> conversation = communicationService.getConversationBetweenUsers(user1Id, user2Id);
        return ResponseEntity.ok(conversation);
    }
}
