package edu.jUnitEMosquito.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "due_date")
    private OffsetDateTime dataLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Usuario creator;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tags> tags;

    public Task(String title, OffsetDateTime dataLimite, Usuario usuarioAuth, Group group, TaskStatus taskStatus) {
        this.title = title;
        this.dataLimite = dataLimite;
        this.creator = usuarioAuth;
        this.group = group;
        this.taskStatus = taskStatus;
    }

    public enum TaskStatus{
        PAUSED("paused"),
        FINISHED("finished"),
        WORKING("working");

        String status;
        TaskStatus(String status) {
            this.status = status;
        }
    }

    public Task() {
    }

    public Task(String title, OffsetDateTime dataLimite, Usuario creator, Group group) {
        this.title = title;
        this.dataLimite = dataLimite;
        this.creator = creator;
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(dataLimite, task.dataLimite) && Objects.equals(creator, task.creator) && Objects.equals(group, task.group) && Objects.equals(tags, task.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OffsetDateTime getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(OffsetDateTime dataLimite) {
        this.dataLimite = dataLimite;
    }

    public Usuario getCreator() {
        return creator;
    }

    public void setCreator(Usuario creator) {
        this.creator = creator;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
