package lv.emendatus.Destiny_PropMan.controllers;

import lv.emendatus.Destiny_PropMan.annotation.search_controller.SearchProperties;
import lv.emendatus.Destiny_PropMan.domain.dto.search.PropertySearchResultDTO;
import lv.emendatus.Destiny_PropMan.domain.dto.search.SearchCriteria;
import lv.emendatus.Destiny_PropMan.service.implementation.PropertySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final PropertySearchService propertySearchService;

    @Autowired
    public SearchController(PropertySearchService propertySearchService) {
        this.propertySearchService = propertySearchService;
    }
    @GetMapping("/properties")
    @SearchProperties
    public ResponseEntity<List<PropertySearchResultDTO>> searchProperties(@RequestBody SearchCriteria criteria) {
        List<PropertySearchResultDTO> searchResults = propertySearchService.searchProperties(criteria);
        return ResponseEntity.ok(searchResults);
    }
}
