package com.cram.backend.chat.meetingroom.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MEETING_ROOM_CHAT_MESSAGE_ATTACHMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomChatMessageAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Column(name = "original_name", columnDefinition = "TEXT")
    private String originalName;

    @Column(name = "saved_name", columnDefinition = "TEXT")
    private String savedName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_room_chat_message_id")
    private MeetingRoomChatMessage meetingRoomChatMessage;
}
