package dev.danieljones.taskapi.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import dev.danieljones.taskapi.model.Task;

public interface TaskRepository extends CrudRepository<Task, Long>{
    List<Task> findByLastName(String lastName);

	Task findById(long id);
}
