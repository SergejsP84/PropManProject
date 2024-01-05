package lv.emendatus.Destiny_PropMan.domain.dto;


import lv.emendatus.Destiny_PropMan.domain.entity.Currency;

public class NumericalConfigDTO {
    private Long id;
    private String name;
    private Double value;
    private Currency currency;

    public NumericalConfigDTO() {
    }

    public NumericalConfigDTO(Long id, String name, Double value, Currency currency) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
