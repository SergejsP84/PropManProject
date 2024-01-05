package lv.emendatus.Destiny_PropMan.domain.dto;

import lv.emendatus.Destiny_PropMan.domain.entity.Bill;
import lv.emendatus.Destiny_PropMan.domain.entity.NumericalConfig;

import java.util.Set;

public class CurrencyDTO {
    private Long id;
    private String designation;
    private Set<Bill> bills;
    private Set<NumericalConfig> numericalConfigs;

    public CurrencyDTO() {
    }

    public CurrencyDTO(Long id, String designation, Set<Bill> bills, Set<NumericalConfig> numericalConfigs) {
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

    public Set<Bill> getBills() {
        return bills;
    }

    public void setBills(Set<Bill> bills) {
        this.bills = bills;
    }

    public Set<NumericalConfig> getNumericalConfigs() {
        return numericalConfigs;
    }

    public void setNumericalConfigs(Set<NumericalConfig> numericalConfigs) {
        this.numericalConfigs = numericalConfigs;
    }
}
