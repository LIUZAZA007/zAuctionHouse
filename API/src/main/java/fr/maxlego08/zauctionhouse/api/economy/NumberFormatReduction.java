package fr.maxlego08.zauctionhouse.api.economy;

import java.math.BigDecimal;

public record NumberFormatReduction(String format, BigDecimal maxAmount, String display) {
}