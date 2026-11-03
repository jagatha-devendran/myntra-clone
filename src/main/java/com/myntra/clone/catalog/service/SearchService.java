package com.myntra.clone.catalog.service;

import com.myntra.clone.catalog.dto.AutosuggestResponse;

public interface SearchService {
    public AutosuggestResponse getAutosuggest(String rawQuery);

}
