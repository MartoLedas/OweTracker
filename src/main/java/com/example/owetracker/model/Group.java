package com.example.owetracker.model;

public class Group {

    private Integer id;
    private String title;
    private Integer created_by;

    public Group(Integer id, String title, Integer created_by) {
        this.id = id;
        this.title = title;
        this.created_by = created_by;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }
}
