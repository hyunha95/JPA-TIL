package com.example.infinitescroll;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class InfiniteScroll {

    @Id @GeneratedValue
    @Column(name = "infinite_scroll_id")
    private Long id;

    private String title;
    private String content;

    private String createdBy;
}
