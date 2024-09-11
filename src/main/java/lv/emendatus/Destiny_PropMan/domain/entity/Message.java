package lv.emendatus.Destiny_PropMan.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.emendatus.Destiny_PropMan.domain.enums_for_entities.UserType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", length = 9000)
    private String content;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;
    @Column(name = "receiver_type")
    private UserType receiverType;
    @Column(name = "sending_time")
    private LocalDateTime sentAt;
    @Column(name = "is_read")
    private boolean isRead; // Indicates whether the message has been read by the receiver
}
