package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import lv.emendatus.Destiny_PropMan.repository.interfaces.MessageRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JpaMessageService implements MessageService {
    private final MessageRepository messageRepository;
    @Autowired
    public JpaMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
    @Override
    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }
    @Override
    public void addMessage(Message message) {
        messageRepository.save(message);
    }
    @Override
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
    @Override
    public List<Message> findBySenderId(Long senderId) {
        return getAllMessages().stream()
                .filter(message -> message.getSenderId().equals(senderId)).toList();
    }
    @Override
    public List<Message> findByReceiverId(Long receiverId) {
        return getAllMessages().stream()
                .filter(message -> message.getReceiverId().equals(receiverId)).toList();
    }
}
