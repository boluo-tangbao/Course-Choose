package com.example.demo.pojo;

import lombok.Data;

@Data
public class GradeWithStudentName {
    private String studentNumber;
    private String name;
    private Integer usualGrade;
    private Integer finalGrade;
    private Integer score;
}
