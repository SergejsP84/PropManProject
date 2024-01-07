package lv.emendatus.Destiny_PropMan.domain.dto;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;

import java.util.Set;

public class CurrencyDTO {
    private Long id;
    private String designation;
    private Set<BillDTO> bills;
    private Set<NumericalConfigDTO> numericalConfigs;

    public CurrencyDTO() {
    }

    public CurrencyDTO(Long id, String designation, Set<BillDTO> bills, Set<NumericalConfigDTO> numericalConfigs) {
        this.id = id;
        this.designation = designation;
        this.bills = bills;
        this.numericalConfigs = numericalConfigs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Set<BillDTO> getBills() {
        return bills;
    }

    public void setBills(Set<BillDTO> bills) {
        this.bills = bills;
    }

    public Set<NumericalConfigDTO> getNumericalConfigs() {
        return numericalConfigs;
    }

    public void setNumericalConfigs(Set<NumericalConfigDTO> numericalConfigs) {
        this.numericalConfigs = numericalConfigs;
    }
}
