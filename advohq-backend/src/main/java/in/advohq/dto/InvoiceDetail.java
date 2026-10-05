package in.advohq.dto;

import in.advohq.domain.Invoice;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Full invoice payload — same as summary, plus the JSON snapshot for re-opening. */
public record InvoiceDetail(
        UUID id,
        String invoiceNo,
        String customerName,
        LocalDate invoiceDate,
        LocalDate dueDate,
        long totalPaise,
        String status,
        String snapshot,
        long sizeBytes,
        Instant createdAt
) {
    public static InvoiceDetail from(Invoice inv) {
        return new InvoiceDetail(
                inv.getId(), inv.getInvoiceNo(), inv.getCustomerName(),
                inv.getInvoiceDate(), inv.getDueDate(),
                inv.getTotalPaise(), inv.getStatus(),
                inv.getSnapshot(),
                inv.getSizeBytes(), inv.getCreatedAt());
    }
}
