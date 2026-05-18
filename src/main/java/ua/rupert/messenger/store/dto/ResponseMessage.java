package ua.rupert.messenger.store.dto;

import java.time.Instant;

public record ResponseMessage(String senderName,
                              String content,
                              Instant date) {
}
