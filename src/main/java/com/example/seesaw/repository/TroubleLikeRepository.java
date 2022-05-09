package com.example.seesaw.repository;


import com.example.seesaw.model.TroubleComment;
import com.example.seesaw.model.TroubleLike;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface TroubleLikeRepository extends JpaRepository<TroubleLike, Long> {
    TroubleLike findByTroubleCommentAndUserId(TroubleComment troubleComment, Long userId);
    @Transactional
    void deleteById(Long id);

    List<TroubleLike> findAllByTroubleCommentId(Long commentId);
}
