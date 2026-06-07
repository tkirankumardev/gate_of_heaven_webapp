package com.tkk.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor

@Builder


@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    @NotBlank(message = "title can't be blank")
    @NotEmpty(message = "title can't be empty")
    @NotNull(message = "title can't be null")
    @Size(min = 10, message = "title can't be less than 10 amd more than 30")
    private String title;

    @Column(name = "transliteration")
    private String transliteration;

    @NotBlank(message = "lyrics can't be blank")
    @NotEmpty(message = "lyrics can't be empty")
    @NotNull(message = "lyrics can't be null")
    @Size(min = 100, message = "check the size of lyrics must be greater than 100 characters")
    @Column(columnDefinition = "TEXT")
    private String lyrics;

    @Column(name = "author")
    private String author;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDate createdDate;
}