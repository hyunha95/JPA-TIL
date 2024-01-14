package com.example.infinitescroll;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InfiniteScrollService {

    private final InfiniteScrollRepository infiniteScrollRepository;

    public Slice<InfiniteScrollDto> findInfiniteScrollBy(Pageable pageable) {
        return infiniteScrollRepository.findInfiniteScrollBy(pageable).map(InfiniteScrollDto::new);
    }
}
