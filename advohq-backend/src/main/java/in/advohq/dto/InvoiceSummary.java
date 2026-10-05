package in.advohq.dto;

import in.advohq.domain.Invoice;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Slim row shape for the Settings history list — no snapshot JSON so the
 * response stays small even after hundreds of invoices.
 */
public record InvoiceSummary(
        UUID id,
        String invoiceNo,
        String customerName,
        LocalDate invoiceDate,
        LocalDate dueDate,
        long totalPaise,
        String status,
        long sizeBytes,
        Instant createdAt
) {
    public static InvoiceSummary from(Invoice inv) {
        return new InvoiceSummary(
                inv.getId(), inv.getInvoiceNo(), inv.getCustomerName(),
                inv.getInvoiceDate(), inv.getDueDate(),
                inv.getTotalPaise(), inv.getStatus(),
                inv.getSizeBytes(), inv.getCreatedAt());
    }
}
