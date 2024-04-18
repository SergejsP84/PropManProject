package lv.emendatus.Destiny_PropMan.util;

import lv.emendatus.Destiny_PropMan.service.implementation.JpaFinancialDataService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

public class FinancialReportScheduler {

    private FinancialReportGenerator reportGenerator;
    private JpaFinancialDataService financialDataService;

    public FinancialReportScheduler(FinancialReportGenerator reportGenerator, JpaFinancialDataService financialDataService) {
        this.reportGenerator = reportGenerator;
        this.financialDataService = financialDataService;
    }

    public void generateFinancialReportForCurrentMonth() {
        // Get the past month and year
        Month pastMonth = LocalDate.now().getMonth().minus(1);
        int year = LocalDate.now().getYear();
        if (pastMonth.equals(Month.DECEMBER)) year--;

        // Get the data for tenant payments, refunds, and payouts for the current month
        List<String> tenantPayments = financialDataService.getTenantPaymentsForMonth(pastMonth);
        List<String> refunds = financialDataService.getRefundsForMonth(pastMonth);
        List<String> payouts = financialDataService.getPayoutsForMonth(pastMonth);

        // Specify the file path where the report will be saved
        String filePath = "Financial_Report_" + pastMonth.toString() + "_" + year + ".xlsx";

        // Generate the financial report
        try {
            reportGenerator.generateFinancialReport(filePath, LocalDate.now(), tenantPayments, refunds, payouts);
            System.out.println("Financial report generated successfully for " + pastMonth.toString() + " " + year);
        } catch (IOException e) {
            System.out.println("Error generating financial report: " + e.getMessage());
        }
    }
}