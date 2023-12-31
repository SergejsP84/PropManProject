package lv.emendatus.Destiny_PropMan.domain.enums_for_entities;

public enum BookingStatus {
    PENDING_PAYMENT, // made, but not yet paid
    CONFIRMED, // and paid, that is
    CURRENT, // the guy's staying on premises right now
    CANCELLED, // says it all per se
}