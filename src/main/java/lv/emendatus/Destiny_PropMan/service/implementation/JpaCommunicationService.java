package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.dto.communication.CommunicationDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.communication.SendMessageDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Manager;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import lv.emendatus.Destiny_PropMan.domain.entity.Tenant;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;
import lv.emendatus.Destiny_PropMan.exceptions.EntityNotFoundException;
import lv.emendatus.Destiny_PropMan.mapper.CommunicationMapper;
import lv.emendatus.Destiny_PropMan.mapper.SendMessageMapper;
import lv.emendatus.Destiny_PropMan.repository.interfaces.MessageRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaCommunicationService implements CommunicationService {

    private final MessageRepository messageRepository;

    private final CommunicationMapper communicationMapper;
    private final SendMessageMapper sendMessageMapper;
    private final JpaMessageService messageService;
    private final JpaTenantService tenantService;
    private final JpaManagerService managerService;

    @Autowired
    public JpaCommunicationService(MessageRepository messageRepository, CommunicationMapper communicationMapper, SendMessageMapper sendMessageMapper, JpaMessageService messageService, JpaTenantService tenantService, JpaManagerService managerService) {
        this.messageRepository = messageRepository;
        this.communicationMapper = communicationMapper;
        this.sendMessageMapper = sendMessageMapper;
        this.messageService = messageService;
        this.tenantService = tenantService;
        this.managerService = managerService;
    }
    @Override
    public void sendMessage(SendMessageDTO messageDTO, Principal principal) {
        String authenticatedUserName = principal.getName();
        Tenant tenant = tenantService.getTenantByLogin(authenticatedUserName);
        Manager manager = managerService.getManagerByLogin(authenticatedUserName);
        Message message = sendMessageMapper.toEntity(messageDTO);
        message.setRead(false);
        message.setSentAt(LocalDateTime.now());
        if (tenant != null || manager != null) {
            Long sendingUserId = 0L;
            if (tenant != null) {
                Optional<Manager> checkingManager = managerService.getManagerById(messageDTO.getReceiverId());
                if (checkingManager.isPresent()) {
                    sendingUserId = tenant.getId();
                    message.setReceiverType(UserType.MANAGER);
                    message.setSenderId(tenant.getId());
                    messageRepository.save(message);
                } else {
                    throw new EntityNotFoundException("Cannot find the recipient Manager!");
                }
            } else {
                Optional<Tenant> checkingTenant = tenantService.getTenantById(messageDTO.getReceiverId());
                if (checkingTenant.isPresent()) {
                    sendingUserId = manager.getId();
                    message.setReceiverType(UserType.TENANT);
                    message.setSenderId(manager.getId());
                    messageRepository.save(message);
                } else {
                    throw new EntityNotFoundException("Cannot find the recipient Tenant!");
                }
            }
        } else {
            throw new AccessDeniedException("Invalid login credentials.");
        }
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
    public List<CommunicationDTO> getConversationBetweenUsers(Long user1Id, Long user2Id, Principal principal) {
        String authenticatedUserName = principal.getName();
        Tenant tenant = tenantService.getTenantByLogin(authenticatedUserName);
        Manager manager = managerService.getManagerByLogin(authenticatedUserName);
        if (tenant != null || manager != null) {
            Long viewingUserId = 0L;
            if (tenant != null) {
                viewingUserId = tenant.getId();
            } else {
                viewingUserId = manager.getId();
            }
            if (viewingUserId.equals(user1Id) || viewingUserId.equals(user2Id)) {
                List<Message> messagesFromUser1 = messageService.findBySenderId(user1Id);
                List<Message> messagesFromUser2 = messageService.findBySenderId(user2Id);
                if (viewingUserId.equals(user1Id)) {
                    for (Message message : messagesFromUser2) {
                        if (!message.isRead()) {
                            message.setRead(true);
                            messageService.addMessage(message);
                        }
                    }
                }
                if (viewingUserId.equals(user2Id)) {
                    for (Message message : messagesFromUser1) {
                        if (!message.isRead()) {
                            message.setRead(true);
                            messageService.addMessage(message);
                        }
                    }
                }
                List<Message> combinedMessages = new ArrayList<>();
                combinedMessages.addAll(messagesFromUser1.stream()
                        .filter(message -> message.getReceiverId().equals(user2Id))
                        .toList());
                combinedMessages.addAll(messagesFromUser2.stream()
                        .filter(message -> message.getReceiverId().equals(user1Id))
                        .toList());
                combinedMessages.sort(Comparator.comparing(Message::getSentAt));
                return combinedMessages.stream()
                        .map(CommunicationMapper.INSTANCE::toDTO)
                        .collect(Collectors.toList());
            } else {
                throw new AccessDeniedException("Users can only access their own conversations.");
            }
        } else {
            throw new AccessDeniedException("Users can only access their own conversations.");
        }
    }
}

