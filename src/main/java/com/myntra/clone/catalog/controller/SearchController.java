package com.myntra.clone.catalog.controller;
import com.myntra.clone.catalog.dto.AutosuggestResponse;
import com.myntra.clone.catalog.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    private SearchService productService;


    public SearchController(SearchService theProductService) {
        this.productService = theProductService;
    }

    @GetMapping("/gateway/v1/search/autosuggest")
    public ResponseEntity<AutosuggestResponse> getAutoSugggestProducts(@RequestParam(name = "q") String q) {

        AutosuggestResponse response = productService.getAutosuggest(q);

        return ResponseEntity.ok(response);
    }


}