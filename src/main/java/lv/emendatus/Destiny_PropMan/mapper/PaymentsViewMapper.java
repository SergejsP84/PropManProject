package lv.emendatus.Destiny_PropMan.mapper;

import lv.emendatus.Destiny_PropMan.domain.dto.view.PaymentsViewDTO;
import lv.emendatus.Destiny_PropMan.domain.entity.Property;
import lv.emendatus.Destiny_PropMan.domain.entity.TenantPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentsViewMapper {

    PaymentsViewMapper INSTANCE = Mappers.getMapper(PaymentsViewMapper.class);

    @Mapping(source = "tenantPayment.amount", target = "amount")
    @Mapping(source = "property.description", target = "description")
    @Mapping(source = "property.address", target = "address")
    @Mapping(source = "property.settlement", target = "settlement")
    @Mapping(source = "property.country", target = "country")
    @Mapping(source = "tenantPayment.receiptDue", target = "receiptDue")
    PaymentsViewDTO toDTO(TenantPayment tenantPayment, Property property);

    default List<PaymentsViewDTO> toDTOList(List<TenantPayment> tenantPayments, List<Property> properties) {
        List<PaymentsViewDTO> dtos = new ArrayList<>();
        for (int i = 0; i < tenantPayments.size(); i++) {
            dtos.add(toDTO(tenantPayments.get(i), properties.get(i)));
        }
        return dtos;
    }
}