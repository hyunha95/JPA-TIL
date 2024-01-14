package com.example.infinitescroll;

public record InfiniteScrollDto(
        Long id,
        String title,
        String content,
        String createdBy
) {
    public InfiniteScrollDto(InfiniteScroll infiniteScroll) {
        this(
                infiniteScroll.getId(),
                infiniteScroll.getTitle(),
                infiniteScroll.getContent(),
                infiniteScroll.getCreatedBy()
        );
    }
}
