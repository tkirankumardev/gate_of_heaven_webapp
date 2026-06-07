    package com.tkk.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;


    @Getter

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder

    @Entity
    @Table(name = "bible")
    public class Bible {

        @Id
        private Long id;

        @Column(name = "book_id")
        private int bookId;

        @Column(length = 40, name = "book_name")
        private String bookName;

        @Column(name = "total_chapters")
        private String totalChapters;

        @Column(length = 3)
        private char testament;

        @Column(name = "chapter_id")
        private int chapterId;

        @Column(nullable = false, name = "verse_number")
        private int verseNumber;

        @Column(length = 1024)
        private String verse;
    }
