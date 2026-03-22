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
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class JpaBillService implements BillService {
    private final BillRepository billRepository;
    private final PropertyRepository propertyRepository;
    private final Logger LOGGER = LogManager.getLogger(JpaBillService.class);

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
    public void addBill(Bill bill) {
        billRepository.save(bill);
    }

    @Override
    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }

    @Override
    public List<Bill> getBillsByProperty(Property property) {
        return billRepository.findByProperty(property);
    }

    @Override
    public List<Bill> getBillsByDueDateRange(LocalDate startDate, LocalDate endDate, Property property) {
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());
        return billRepository.findByPropertyAndDueDateRange(property, startTimestamp, endTimestamp);
    }

    @Override
    public List<Bill> getUnpaidBills(Property property) {
        return billRepository.findByPropertyAndPaidStatus(property, false);
    }

    @Override
    public List<Bill> getPaidBills(Property property) {
        return billRepository.findByPropertyAndPaidStatus(property, true);
    }

    @Override
    public List<Bill> getBillsByExpenseCategory(Property property, String expenseCategory) {
        return billRepository.findByPropertyAndExpenseCategory(property, expenseCategory);
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
                return null;
            }
            return propertyBills.stream()
                    .max((a, b) -> a.getAddedAt().compareTo(b.getAddedAt()))
                    .orElse(null);
        } else {
            LOGGER.log(Level.ERROR, "No property with the ID {} exists in the database.", propertyId);
            throw new PropertyNotFoundException("Property with the ID " + propertyId + " could not be found");
        }
    }
}
