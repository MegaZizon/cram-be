package com.cram.backend.studygrouptag.entity;

import com.cram.backend.studygroup.entity.StudyGroup;
import com.cram.backend.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StudyGroupTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sutdy_group_id")
    private StudyGroup studyGroup;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public StudyGroupTag(Long id, StudyGroup studyGroup, Tag tag) {
        this.id = id;
        this.studyGroup = studyGroup;
        this.tag = tag;
    }
}