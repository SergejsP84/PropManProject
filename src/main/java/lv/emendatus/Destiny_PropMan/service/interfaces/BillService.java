package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillService {
    List<Bill> getAllBills();
    Optional<Bill> getBillById(Long id);
    void addBill(Bill bill);
    void deleteBill(Long id);
    List<Bill> getBillsByProperty(Property property);
    List<Bill> getBillsByDueDateRange(LocalDate startDate, LocalDate endDate, Property property);
    List<Bill> getUnpaidBills(Property property);
    List<Bill> getPaidBills(Property property);
    List<Bill> getBillsByExpenseCategory(Property property, String expenseCategory);
    void togglePaidStatus(Long id);
}
