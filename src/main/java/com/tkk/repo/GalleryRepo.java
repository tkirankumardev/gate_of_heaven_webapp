package com.tkk.repo;

import com.tkk.entity.Gallery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GalleryRepo extends JpaRepository<Gallery, Long> {


    @Query("select g from Gallery g where g.category = :category")
    List<Gallery> getRandom(@Param("category") String category, Pageable pageable);

    @Query("select distinct g.metaData from Gallery g where g.category = :category order by g.metaData asc")
    List<String> getMetaData(@Param("category") String category);

    List<Gallery> findGalleryByCategoryAndMetaData(String category, String metaData);

}
