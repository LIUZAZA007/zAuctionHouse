package fr.maxlego08.zauctionhouse.api.storage.dto;

import fr.maxlego08.zauctionhouse.api.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record TransactionDTO(int id, UUID player_unique_id, String economy_name, BigDecimal before,
                             BigDecimal after, BigDecimal value, TransactionStatus status, Date created_at,
                             Date updated_at) {
}