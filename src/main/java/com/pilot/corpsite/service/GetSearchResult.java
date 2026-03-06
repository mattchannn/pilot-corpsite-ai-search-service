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

@Service
public class GetSearchResult {
    @Value("${azure.ai-search.instance}")
    private String endpoint;

    private static final String INDEX_NAME = "corpsite-search-indexes";

    private static final String API_KEY = "";

    private static final String[] select = {"id", "parent_id", "chunk", "title", "language"};

    public List<String> search(String query) {
        SearchIndexClient searchIndexClient = new SearchIndexClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(API_KEY))
                .buildClient();

        VectorizableTextQuery vectorizableTextQuery = new VectorizableTextQuery(query)
                .setFields("embeddings")
                .setKNearestNeighborsCount(5);
        VectorSearchOptions vectorSearchOptions = new VectorSearchOptions()
                .setQueries(vectorizableTextQuery);
        SearchOptions searchOptions = new SearchOptions()
                .setIncludeTotalCount(true)
                .setSelect(select)
                .setVectorSearchOptions(vectorSearchOptions)
                .setTop(200);

        SearchPagedIterable result = searchIndexClient.getSearchClient(INDEX_NAME)
                .search(query, searchOptions, Context.NONE);

        String luckyParentId = result.stream()
                .findFirst()
                .get()
                .getDocument(SearchDocument.class)
                .getParentId();

        return result.stream()
                .filter(x -> x.getDocument(SearchDocument.class)
                        .getParentId()
                        .equals(luckyParentId))
                .map(r -> r.getDocument(SearchDocument.class).getChunk())
                .toList();
    }
}
