package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.domain.entity.Currency;
import lv.emendatus.Destiny_PropMan.domain.entity.Message;
import lv.emendatus.Destiny_PropMan.service.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messaging")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addMessage(@RequestBody Message message) {
        messageService.addMessage(message);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/getall")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
    @GetMapping("/getMessageById")
    public ResponseEntity<Message> getMessageById(@RequestParam Long id) {
        Optional<Message> result = messageService.getMessageById(id);
        return result.map(message -> new ResponseEntity<>(message, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/deleteMessageById/{id}")
    public void deleteByID(@PathVariable Long id) {
        if (messageService.getMessageById(id).isPresent()) {
            messageService.deleteMessage(id);
        } else {
            System.out.println("No message with the ID " + id + "exists in the database!");
        }
    }
}
