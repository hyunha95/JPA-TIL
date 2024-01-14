package com.example.infinitescroll;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfiniteScrollRepository extends JpaRepository<InfiniteScroll, Long> {
    Slice<InfiniteScroll> findInfiniteScrollBy(Pageable pageable);
}
