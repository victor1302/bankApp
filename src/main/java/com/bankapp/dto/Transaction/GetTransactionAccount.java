package com.bankapp.dto.Transaction;

import java.util.UUID;

public record GetTransactionAccount(UUID accountId, int accountNumber, String name) {
}
