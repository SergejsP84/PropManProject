package lv.emendatus.Destiny_PropMan.service.interfaces;

import java.time.Month;
import java.util.List;

public interface FinancialDataService {
    List<String> getTenantPaymentsForMonth(Month month);
    List<String> getRefundsForMonth(Month month);
    List<String> getPayoutsForMonth(Month month);
}