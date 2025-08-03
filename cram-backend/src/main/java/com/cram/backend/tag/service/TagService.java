package com.cram.backend.tag.service;

import com.cram.backend.tag.entity.Tag;

import java.util.List;

public interface TagService {
    List<Tag> findOrCreateTags(List<String> names);
}
