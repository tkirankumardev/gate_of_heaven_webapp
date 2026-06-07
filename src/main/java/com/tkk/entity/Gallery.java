package com.tkk.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
public class Gallery {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String category;

    @NotBlank(message = "Metadata can't be blank, null or empty, must be defined")
    private String metaData;

    private String objectId;

    private String objectExtension;

    private LocalDate date;

    @Transient
    private String preSignedUrl;

    public void setPresignedUrl(String string) {
        preSignedUrl = string;
    }
}
