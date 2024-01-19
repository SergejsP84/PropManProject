package lv.emendatus.Destiny_PropMan.service.interfaces;

import lv.emendatus.Destiny_PropMan.domain.dto.search.PropertySearchResultDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.search.SearchCriteria;

import java.util.List;

public interface PropertySearchService {
    List<PropertySearchResultDTO> searchProperties(SearchCriteria criteria);
}