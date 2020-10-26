package com.malelprojects.RedditClone.model;

import java.time.Instant;
import java.util.List;

public class SubReddit {
    private Long id;
    private String name;
    private String description;
    private List<Post> posts;
    private Instant createdDate;
    private User user;

}
