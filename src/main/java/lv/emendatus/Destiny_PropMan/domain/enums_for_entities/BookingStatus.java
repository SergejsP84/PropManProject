package lv.emendatus.Destiny_PropMan.domain.enums_for_entities;

public enum BookingStatus {
    PENDING_APPROVAL, // not yet confirmed by the property's manager
    PENDING_PAYMENT, // made, but not yet paid
    CONFIRMED, // and paid, that is
    CURRENT, // the guy's staying on premises right now
    CANCELLED, // says it all per se
    OVER, // successfully completed
    PROPERTY_LOCKED_BY_MANAGER // whenever a Manager wishes to lock a property fo some time
}