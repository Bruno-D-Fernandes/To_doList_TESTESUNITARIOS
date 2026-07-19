package edu.jUnitEMosquito.repository;

import edu.jUnitEMosquito.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagsRepository extends JpaRepository<Tags, Long> {
    List<Tags> findByGroup_Id(Long id);
}
