package lv.emendatus.Destiny_PropMan.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;


public class FinancialReportGenerator {

    public void generateFinancialReport(String filePath, LocalDate month, List<String> tenantPayments,
                                        List<String> refunds, List<String> payouts) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        int year = month.getYear();
        if (month.getMonth().minus(1).equals(Month.DECEMBER)) year--;
        Sheet sheet = workbook.createSheet("Financial Report " + month.getMonth().minus(1) + " " + year);

        // Create headers for each sheet
        createHeaders(sheet);

        // Write data to each sheet
        writeData(sheet, tenantPayments, refunds, payouts);

        // Write the workbook to a file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } finally {
            // Close the workbook to free resources
            workbook.close();
        }
    }

    private void createHeaders(Sheet sheet) {
        // Write headers for Tenant Payments
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Tenant Payments");

        // Write headers for Refunds
        row = sheet.createRow(2);
        row.createCell(0).setCellValue("Refunds");

        // Write headers for Payouts
        row = sheet.createRow(4);
        row.createCell(0).setCellValue("Payouts");
    }

    private void writeData(Sheet sheet, List<String> tenantPayments, List<String> refunds, List<String> payouts) {
        // Write Tenant Payments data
        writeDataRows(sheet, tenantPayments, 1);

        // Write Refunds data
        writeDataRows(sheet, refunds, 3);

        // Write Payouts data
        writeDataRows(sheet, payouts, 5);
    }

    private void writeDataRows(Sheet sheet, List<String> data, int startingRow) {
        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(startingRow + i);
            row.createCell(0).setCellValue(data.get(i));
        }
    }
}
