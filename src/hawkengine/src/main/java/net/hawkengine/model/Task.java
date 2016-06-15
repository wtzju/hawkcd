package net.hawkengine.model;

import net.hawkengine.model.enums.RunIf;
import net.hawkengine.model.enums.Status;
import net.hawkengine.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class Task {
    private TaskDefinition taskDefinition;
    private String jobId;
    private RunIf runIfCondition;
    private TaskType type;
    private Status status;
    private String output;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    protected Task() {
        this.startTime = LocalDateTime.now();
    }

    public TaskDefinition getTaskDefinition() {
        return this.taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public RunIf getRunIfCondition() {
        return this.runIfCondition;
    }

    public void setRunIfCondition(RunIf runIfCondition) {
        this.runIfCondition = runIfCondition;
    }

    public TaskType getType() {
        return this.type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getOutput() {
        return this.output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}