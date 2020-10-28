package com.malelprojects.RedditClone.repository;

import com.malelprojects.RedditClone.model.Post;
import com.malelprojects.RedditClone.model.User;
import com.malelprojects.RedditClone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
