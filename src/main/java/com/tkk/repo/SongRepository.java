package com.tkk.repo;

import com.tkk.dto.SongIndex;
import com.tkk.dto.SongIndexScore;
import com.tkk.entity.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("select new com.tkk.dto.SongIndex(s.id, s.title, s.transliteration) from Song s")
    public List<SongIndex> findAllByList();

    public Optional<Song> findById(Long id);

    @Query("select new com.tkk.dto.SongIndex(s.id, s.title, s.transliteration) from Song s order by s.createdDate DESC")
    public List<SongIndex> findLast(Pageable pageable);

    @Query(value = """
SELECT id, title, transliteration, 
       GREATEST(
           similarity(title, :search),
           similarity(transliteration, lower(:search))
       ) AS score
FROM songs
WHERE
      title % :search
   OR transliteration % lower(:search)
   OR title ILIKE '%' || :search || '%'
   OR transliteration ILIKE '%' || lower(:search) || '%'
ORDER BY score DESC
LIMIT 20;
    """, nativeQuery = true)
    List<SongIndexScore> search(@Param("search") String search);

    boolean existsByTitle(String title);
}
