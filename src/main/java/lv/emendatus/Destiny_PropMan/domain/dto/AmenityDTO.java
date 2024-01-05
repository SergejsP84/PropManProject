package lv.emendatus.Destiny_PropMan.domain.dto;

public class AmenityDTO {

    private Long id;
    private String name;

    public AmenityDTO() {

    }

    public AmenityDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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
}
