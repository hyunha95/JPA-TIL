package com.example.infinitescroll;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@SpringBootTest
class InfiniteScrollApplicationTests {

    @Test
    void contextLoads() {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/main/resources/data.sql"));
            for (int i = 0; i < 1000; i++) {
                bw.write("INSERT INTO infinite_scroll (infinite_scroll_id, title, content, created_by) VALUES ("+ (i + 1) +", 'title" + i + "', 'content" + i + "', 'createdBy" + i + "');");
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
