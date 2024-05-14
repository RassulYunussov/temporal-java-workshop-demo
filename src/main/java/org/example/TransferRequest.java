package org.example;

public record TransferRequest(
        String fromAccountId, String toAccountId, double amount) {


    public String toReadableString() {
        return "   FromAccountId = " + fromAccountId
                + System.lineSeparator() +
                "   ToAccountId = " + toAccountId
                + System.lineSeparator() +
                "   Amount=" + amount;
    }
}