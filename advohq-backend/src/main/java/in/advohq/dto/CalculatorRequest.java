package in.advohq.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Input to the Karnataka suit-valuation / court-fee calculator. Fields are
 * optional except {@code suitType}; which ones are actually required depends
 * on the suit type, and is enforced inside {@link in.advohq.service.SuitCalculatorService}
 * (mirrors the client-side field-visibility rules in calculator.html).
 */
public record CalculatorRequest(
        @NotBlank String suitType,

        Double claimAmount,
        Double reliefValue,
        Double durationYears,

        String propertyValuationMethod,
        Double propertyMarketValue,
        Double annualLandRevenue,
        Double priorYearNetProfits,
        Double comparableLandRevenue,

        Double plaintiffSharePercent,
        Boolean excludedFromPossession,

        Double annualRent,
        Double premiumAmount,

        Double securedAmount,
        String reliefVariant,
        Double secondaryAmount,

        Double estateValue,
        Double lowerCourtSuitValue,
        Double considerationAmount,

        Boolean immovableProperty,
        Boolean titleDenied,
        String propertyLocation,
        String defendantLocation,
        String causeOfActionLocation,

        Boolean commercialDispute,
        Double localLowerCourtLimit,

        String district
) {
}
