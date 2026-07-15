package edu.jUnitEMosquito.repository;

import edu.jUnitEMosquito.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
