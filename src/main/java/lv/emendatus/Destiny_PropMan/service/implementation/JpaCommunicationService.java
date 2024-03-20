package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.communication.CommunicationDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import lv.emendatus.Destiny_PropMan.mapper.CommunicationMapper;
import lv.emendatus.Destiny_PropMan.repository.interfaces.MessageRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JpaCommunicationService implements CommunicationService {

    private final MessageRepository messageRepository;

    private final CommunicationMapper communicationMapper;
    private final JpaMessageService messageService;

    @Autowired
    public JpaCommunicationService(MessageRepository messageRepository, CommunicationMapper communicationMapper, JpaMessageService messageService) {
        this.messageRepository = messageRepository;
        this.communicationMapper = communicationMapper;
        this.messageService = messageService;
    }
    @Override
    public void sendMessage(CommunicationDTO messageDTO) {
        Message messageEntity = CommunicationMapper.INSTANCE.toEntity(messageDTO);
        messageRepository.save(messageEntity);
    }
    @Override
    public List<CommunicationDTO> getMessagesBySender(Long senderId) {
        List<Message> messages = messageService.findBySenderId(senderId);
        return messages.stream()
                .map(CommunicationMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<CommunicationDTO> getMessagesByReceiver(Long receiverId) {
        List<Message> messages = messageService.findByReceiverId(receiverId);
        return messages.stream()
                .map(CommunicationMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<CommunicationDTO> getConversationBetweenUsers(Long user1Id, Long user2Id) {
        List<Message> messagesFromUser1 = messageService.findBySenderId(user1Id);
        List<Message> messagesFromUser2 = messageService.findBySenderId(user2Id);
        List<Message> combinedMessages = new ArrayList<>();
        combinedMessages.addAll(messagesFromUser1.stream()
                .filter(message -> message.getReceiverId().equals(user2Id))
                .toList());
        combinedMessages.addAll(messagesFromUser2.stream()
                .filter(message -> message.getReceiverId().equals(user1Id))
                .toList());
        return combinedMessages.stream()
                .map(CommunicationMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }
}

