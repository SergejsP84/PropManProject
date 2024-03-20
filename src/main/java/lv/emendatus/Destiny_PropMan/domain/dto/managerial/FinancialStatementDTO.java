package lv.emendatus.Destiny_PropMan.domain.dto.managerial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialStatementDTO {
    Map<Long, Double> income; // Property Id as key, total income for the period as value
    List<Map<String, Double>> expenses; // Expense category from bills as key, total costs for that category as value
}
