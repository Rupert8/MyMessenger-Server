package ua.rupert.messenger.store.dto;

import lombok.Builder;
import ua.rupert.messenger.store.entities.MessageStatus;

import java.time.Instant;

@Builder
public record ChatMessageDto(String senderName,
                             String recipientName,
                             String content,
                             Instant timestamp,
                             MessageStatus status) {}
