package in.advohq.dto;

/**
 * Used both to add a signer (editor adds an empty card immediately, before a
 * name is typed) and to update one afterward — so {@code name} is
 * deliberately not {@code @NotBlank} here. {@link in.advohq.service.SignatureService#send}
 * refuses to send while any signer's name is still blank.
 */
public record SignerRequest(String name, String email, boolean isCompany) {
}
