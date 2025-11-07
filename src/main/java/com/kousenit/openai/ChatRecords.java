package com.kousenit.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ChatRecords {

    public record ChatInput(String model, String input) {}

    public record ChatResponse(
            String id,
            String object,
            @JsonProperty("created_at") long createdAt,
            String status,
            Object error,
            @JsonProperty("incomplete_details") Object incompleteDetails,
            Object instructions,
            @JsonProperty("max_output_tokens") Object maxOutputTokens,
            String model,
            List<OutputItem> output,
            @JsonProperty("parallel_tool_calls") boolean parallelToolCalls,
            @JsonProperty("previous_response_id") Object previousResponseId,
            Reasoning reasoning,
            boolean store,
            double temperature,
            TextFormat text,
            @JsonProperty("tool_choice") String toolChoice,
            List<Object> tools,
            @JsonProperty("top_p") double topP,
            String truncation,
            Usage usage,
            Object user,
            Object metadata
    ) {}

    public record OutputItem(
            String type,
            String id,
            String status,
            String role,
            List<ContentItem> content
    ) {}

    public record ContentItem(
            String type,
            String text,
            List<Object> annotations
    ) {}

    public record Reasoning(
            Object effort,
            Object summary
    ) {}

    public record TextFormat(
            FormatType format
    ) {}

    public record FormatType(
            String type
    ) {}

    public record Usage(
            @JsonProperty("input_tokens") int inputTokens,
            @JsonProperty("input_tokens_details") InputTokensDetails inputTokensDetails,
            @JsonProperty("output_tokens") int outputTokens,
            @JsonProperty("output_tokens_details") OutputTokensDetails outputTokensDetails,
            @JsonProperty("total_tokens") int totalTokens
    ) {}

    public record InputTokensDetails(
            @JsonProperty("cached_tokens") int cachedTokens
    ) {}

    public record OutputTokensDetails(
            @JsonProperty("reasoning_tokens") int reasoningTokens
    ) {}
}
