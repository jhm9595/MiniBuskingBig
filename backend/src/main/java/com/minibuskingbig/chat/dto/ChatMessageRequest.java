package com.minibuskingbig.chat.dto;

import com.minibuskingbig.chat.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {

    private Long roomId;
    private MessageType messageType;
    private String content;
}
