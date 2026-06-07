package com.tkk.repo;

import com.tkk.dto.BibleIndex;
import com.tkk.dto.VerseIndex;
import com.tkk.entity.Bible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BibleRepository extends JpaRepository<Bible, Long> {

   @Query("select distinct new com.tkk.dto.BibleIndex(b.bookId, b.bookName, b.totalChapters) from Bible b order by b.bookId")
    public List<BibleIndex> getIndex();

   @Query("select b.verseNumber, b.verse from Bible b where b.bookId = :bookId and b.chapterId = :chapterId")
   public List<VerseIndex> getVerses(@Param("bookId") int bookId, @Param("chapterId") int chapterId);

    @Query("select b.verseNumber, b.verse from Bible b where b.bookId = :bookId and b.chapterId = :chapterId and b.verseNumber between :from and :to order by b.verseNumber")
    public List<VerseIndex> getCertainVerses(@Param("bookId") int bookId, @Param("chapterId") int chapterId, @Param("from") int from, @Param("to") int to);

    @Query("select b.verseNumber, b.verse from Bible b where b.bookId = :bookId and b.chapterId = :chapterId and b.verseNumber = :verseNumber")
    public List<VerseIndex> getCertainVerse(@Param("bookId") int bookId, @Param("chapterId") int chapterId, @Param("verseNumber") int verseNumber);

}
