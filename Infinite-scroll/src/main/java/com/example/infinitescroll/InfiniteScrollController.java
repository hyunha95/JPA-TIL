package com.example.infinitescroll;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RequiredArgsConstructor
@RestController
public class InfiniteScrollController {

    private final InfiniteScrollService infiniteScrollService;

    @GetMapping("/infinite-scroll")
    public Slice<InfiniteScrollDto> findInfiniteScrollBy(Pageable pageable) {
        return infiniteScrollService.findInfiniteScrollBy(pageable);
    }
}
