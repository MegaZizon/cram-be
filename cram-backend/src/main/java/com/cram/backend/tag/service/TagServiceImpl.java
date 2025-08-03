package com.cram.backend.tag.service;

import com.cram.backend.tag.entity.Tag;
import com.cram.backend.tag.repository.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> findOrCreateTags(List<String> names) {
        return names.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(
                                Tag.builder().name(name).build())
                        )
                )
                .collect(Collectors.toList());
    }
}
