package in.advohq.dto;

import jakarta.validation.constraints.NotBlank;

/** The signer's typed full name, taken as their electronic signature consent. */
public record SignConsentRequest(@NotBlank String typedName) {
}
