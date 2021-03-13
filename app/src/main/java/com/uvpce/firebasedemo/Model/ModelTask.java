package com.uvpce.firebasedemo.Model;

public class ModelTask {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String user_id;
    private String task_name;
    private String task_desc;
    private String image_id;
    private String date;

    public ModelTask() {
    }

    public ModelTask(String id, String user_id, String task_name, String task_desc, String image_id, String date) {
        this.id = id;
        this.user_id = user_id;
        this.task_name = task_name;
        this.task_desc = task_desc;
        this.image_id = image_id;
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_desc() {
        return task_desc;
    }

    public void setTask_desc(String task_desc) {
        this.task_desc = task_desc;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
