package com.buaa.song.specificationExecutor;

import javax.persistence.*;

/**
 * @author Zhao Bo
 * @create 2021/7/15
 * @class Task
 * @description
 */
@Entity
@Table(name = "tb_task")
public class Task1 {

    private Long id ;
    private String taskName;
    private Project project;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "task_name")
    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", project=" + project +
                '}';
    }
}