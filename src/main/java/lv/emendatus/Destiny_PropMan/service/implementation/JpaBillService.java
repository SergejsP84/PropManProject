package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.exceptions.BillNotFoundException;
import lv.emendatus.Destiny_PropMan.exceptions.PropertyNotFoundException;
import lv.emendatus.Destiny_PropMan.repository.interfaces.BillRepository;
import lv.emendatus.Destiny_PropMan.repository.interfaces.PropertyRepository;
import lv.emendatus.Destiny_PropMan.service.interfaces.BillService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class JpaBillService implements BillService {
    private final BillRepository billRepository;
    private final PropertyRepository propertyRepository;

    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);
    public JpaBillService(BillRepository billRepository, PropertyRepository propertyRepository) {
        this.billRepository = billRepository;
        this.propertyRepository = propertyRepository;
    }
    @Override
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }
    @Override
    public Optional<Bill> getBillById(Long id) {
        return billRepository.findById(id);
    }
    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void addBill(Bill bill) {
        billRepository.save(bill);
    }
    @Override
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }
    @Override
    public List<Bill> getBillsByProperty(Property property) {
        return getAllBills().stream()
                .filter(bill -> bill.getProperty().equals(property)).collect(Collectors.toList());
    }
    @Override
    public List<Bill> getBillsByDueDateRange(LocalDate startDate, LocalDate endDate, Property property) {
        List<Bill> forGivenProperty = getBillsByProperty(property);
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());
        return forGivenProperty.stream()
                .filter(bill -> {
                    Timestamp billDueDate = bill.getDueDate();
                    long billTime = billDueDate.getTime();
                    long startTime = startTimestamp.getTime();
                    long endTime = endTimestamp.getTime();
                    return (billTime >= startTime && billTime < endTime);
                })
                .collect(Collectors.toList());
    }
    @Override
    public List<Bill> getUnpaidBills(Property property) {
        List<Bill> forGivenProperty = getBillsByProperty(property);
        return forGivenProperty.stream().filter(bill -> !bill.isPaid()).toList();
    }
    @Override
    public List<Bill> getPaidBills(Property property) {
        List<Bill> forGivenProperty = getBillsByProperty(property);
        return forGivenProperty.stream().filter(Bill::isPaid).toList();
    }
    @Override
    public List<Bill> getBillsByExpenseCategory(Property property, String expenseCategory) {
        List<Bill> forGivenProperty = getBillsByProperty(property);
        return forGivenProperty.stream().filter(bill -> bill.getExpenseCategory().equals(expenseCategory)).toList();
    }

    @Override
    public void togglePaidStatus(Long id) {
        Optional<Bill> optionalBill = billRepository.findById(id);
        if (optionalBill.isPresent()) {
            Bill bill = optionalBill.get();
            bill.setPaid(!bill.isPaid());
            billRepository.save(bill);
        } else {
            LOGGER.log(Level.ERROR, "No bill with the {} ID exists in the database.", id);
            throw new BillNotFoundException("Bill with the ID " + id + " could not be found");
        }
    }

    // AUXILIARY METHOD
    public Bill getLatestBill(Long propertyId) {
        Optional<Property> property = propertyRepository.findById(propertyId);
        if (property.isPresent()) {
            List<Bill> propertyBills = getBillsByProperty(property.get());
            if (propertyBills.isEmpty()) {
                return null; // shouldn't happen if things go normal
            } else if (propertyBills.size() == 1) {
                return propertyBills.get(0);
            } else {
                Bill returnedBill = propertyBills.get(0);
                for (Bill bill : propertyBills) {
                    if (bill.getAddedAt().after(returnedBill.getAddedAt())) {
                        returnedBill = bill;
                    }
                }
                return returnedBill;
            }
        } else {
            LOGGER.log(Level.ERROR, "No property with the ID {} exists in the database.", propertyId);
            throw new PropertyNotFoundException("Property with the ID " + propertyId + " could not be found");
        }
    }

}
