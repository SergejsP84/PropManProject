package lv.emendatus.Destiny_PropMan.service.implementation;

import lv.emendatus.Destiny_PropMan.service.interfaces.FinancialDataService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
public class JpaFinancialDataService implements FinancialDataService {
    private static final String COMPLETED_PAYMENTS_FILE_PATH = "completed_payments.txt";
    private static final String COMPLETED_REFUNDS_FILE_PATH = "completed_refunds.txt";
    private static final String COMPLETED_PAYOUTS_FILE_PATH = "completed_payouts.txt";
    private final Logger LOGGER = LogManager.getLogger(JpaPropertyService.class);

    @Override
    public List<String> getTenantPaymentsForMonth(Month month) {
        return getRecordsForMonth(COMPLETED_PAYMENTS_FILE_PATH, month);
    }
    @Override
    public List<String> getRefundsForMonth(Month month) {
        return getRecordsForMonth(COMPLETED_REFUNDS_FILE_PATH, month);
    }
    @Override
    public List<String> getPayoutsForMonth(Month month) {
        return getRecordsForMonth(COMPLETED_PAYOUTS_FILE_PATH, month);
    }

    private List<String> getRecordsForMonth(String filePath, Month month) {
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                if (parts.length >= 2) {
                    LocalDateTime timestamp = LocalDateTime.parse(parts[0]);
                    if (timestamp.getMonth() == month) {
                        records.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.ERROR, "Could not read data from the set filepath");
        }
        return records;
    }
}
