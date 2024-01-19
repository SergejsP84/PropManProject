package lv.emendatus.Destiny_PropMan.domain.dto.reference;


public class NumericalConfigDTO {
    private Long id;
    private String name;
    private Double value;
    private CurrencyDTO currency;

    public NumericalConfigDTO() {
    }

    public NumericalConfigDTO(Long id, String name, Double value, CurrencyDTO currency) {
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

    public CurrencyDTO getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyDTO currency) {
        this.currency = currency;
    }
}
