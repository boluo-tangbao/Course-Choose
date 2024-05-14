package com.example.demo.pojo;

import lombok.Data;

@Data
public class TeacherSingleCourse {
    private String courseNumber;
    private String credit;
    private String jobNumber;
    private String term;
    private String time;
    private String classroom;
    private Integer curCapacity;
    private Integer capacity;
    private String courseName;
    private Integer teacherAmount;
}
