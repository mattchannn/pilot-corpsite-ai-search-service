package com.pilot.corpsite.service;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.Context;
import com.azure.search.documents.indexes.SearchIndexClient;
import com.azure.search.documents.indexes.SearchIndexClientBuilder;
import com.azure.search.documents.models.*;
import com.azure.search.documents.util.SearchPagedIterable;
import com.pilot.corpsite.model.api.SearchDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetChunks {
    @Value("${azure.ai-search.instance}")
    private String endpoint;

    @Value("${azure.ai-search.apiKey}")
    private String apiKey;

    @Value("${azure.ai-search.isVectorSearchEnabled}")
    private Boolean useVectorSearch;

    private static final String INDEX_NAME = "corpsite-search-indexes";

    private static final String[] select = {"id", "parent_id", "chunk", "title"};

    public List<SearchDocument> search(String query) {
        SearchIndexClient searchIndexClient = new SearchIndexClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .buildClient();

        VectorizableTextQuery vectorizableTextQuery = new VectorizableTextQuery(query)
                .setFields("embeddings")
                .setKNearestNeighborsCount(5);
        VectorSearchOptions vectorSearchOptions = new VectorSearchOptions()
                .setQueries(vectorizableTextQuery);

        SearchOptions searchOptions = new SearchOptions()
                .setIncludeTotalCount(true)
                .setSelect(select)
                .setOrderBy("score_override desc, search.score() desc")
                .setTop(200);

        if (useVectorSearch) {
            searchOptions.setVectorSearchOptions(vectorSearchOptions);
        }

        SearchPagedIterable result = searchIndexClient.getSearchClient(INDEX_NAME)
                .search(query, searchOptions, Context.NONE);

        return result.stream()
                .filter(r -> r.getScore() >= 0.75)
                .map(r -> r.getDocument(SearchDocument.class))
                .collect(Collectors.groupingBy(SearchDocument::getParentId))
                .entrySet().stream()
                .map(entry -> {
                    List<SearchDocument> documents = entry.getValue();
                    SearchDocument aggregated = new SearchDocument();
                    aggregated.setId(null);
                    aggregated.setParentId(entry.getKey());
                    aggregated.setChunk(documents.stream()
                            .map(SearchDocument::getChunk)
                            .collect(Collectors.joining(" ")));
                    aggregated.setExternalLink(documents.get(0).getExternalLink());
                    return aggregated;
                })
                .collect(Collectors.toList());
    }
}
