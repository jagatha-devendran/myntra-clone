package com.myntra.clone.catalog.service;

import com.myntra.clone.catalog.dto.AutosuggestResponse;
import com.myntra.clone.catalog.dto.SuggestionDto;
import com.myntra.clone.catalog.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImp implements SearchService {

    private ProductRepository productRepository;

    public SearchServiceImp(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public AutosuggestResponse getAutosuggest(String rawQuery) {
        if (rawQuery == null || rawQuery.trim().isEmpty()){
            return new AutosuggestResponse(0, List.of());
        }

        String query = rawQuery.trim();
        Pageable limit = PageRequest.of(0, 5);

        List<SuggestionDto> categorySuggestions = productRepository.findCategorySuggestions(query, limit);
        List<SuggestionDto> brandSuggestions = productRepository.findBrandSuggestions(query, limit);

        List<SuggestionDto> combined = new ArrayList<>();
        combined.addAll(categorySuggestions);
        combined.addAll(brandSuggestions);

        return new AutosuggestResponse(combined.size(), combined);
    }
}
