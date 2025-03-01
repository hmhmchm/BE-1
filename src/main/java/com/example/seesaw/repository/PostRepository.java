package com.example.seesaw.repository;


import com.example.seesaw.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByTitleContainingOrContentsContaining(@Param("title") String title, @Param("contents") String contents);

    boolean existsByTitle(String title);
    // Top 16개만 스크랩 순으로 가져오기
    List<Post> findTop16ByOrderByScrapCountDesc();

}
