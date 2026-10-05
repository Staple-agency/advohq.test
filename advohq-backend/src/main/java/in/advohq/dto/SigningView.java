package in.advohq.dto;

import java.util.List;
import java.util.UUID;

/** What the public signing page (sign.html) shows for one signer's token. */
public record SigningView(UUID requestId, String documentName, String requestStatus,
                           String signerName, String signerStatus, boolean isCompany,
                           List<FieldResponse> fields) {
}
