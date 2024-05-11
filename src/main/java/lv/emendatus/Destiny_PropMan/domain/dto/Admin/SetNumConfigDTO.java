package lv.emendatus.Destiny_PropMan.domain.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetNumConfigDTO {
    private int refundPaymentPeriodDays; // number of days within which to pay Refunds, 15 by default
    private int payoutPaymentPeriodDays; // number of days within which to pay Payouts, 20 by default
    private int claimPeriodDays; // number of days after the end of a Booking within which a Claim can be filed, 7 by default
    private int paymentPeriodDays; // number of days - a Tenant must pay a Booking this number of days in advance, 8 by default
    private int earlyTerminationPenalty; // percentage withheld from a Tenant's Refund as penalty for early termination, 0 by default
    private int systemInterestRate; // percentage of a TenantPayment retained by the system as commission fee, 10 by default
    private int lateCancellationPeriodInDays; // period before a Booking starts, within which cancellation of a Booking is considered a late cancellation
    private int urgentCancellationPeriodInDays; // period before a Booking starts, within which cancellation of a Booking is considered an urgent cancellation
    private int urgentCancellationPenaltyPercentage; // penalty percentage charged in case of an urgent cancellation
    private int lateCancellationPenaltyPercentage; // penalty percentage charged in case of a late cancellation
    private int regularCancellationPenaltyPercentage; // penalty percentage charged in case of a timely cancellation
}
