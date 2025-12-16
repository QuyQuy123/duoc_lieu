package com.web.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    /*
    @ManyToMany(mappedBy = "tags")
    private List<Plant> plants;

    @ManyToMany(mappedBy = "tags")
    private List<Article> articles;

    @ManyToMany(mappedBy = "tags")
    private List<Research> research;

     */
}
