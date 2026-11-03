package com.myntra.clone.catalog.dto;

import java.util.List;

public record AutosuggestResponse(
        int total,
        List<SuggestionDto> suggestions
) {}
