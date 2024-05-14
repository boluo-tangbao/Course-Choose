package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="student_course")
@Data
public class Grade {
    @Id
    //private Integer id;
    @MppMultiId // 复合主键
    @TableField("Student_number")
    private String studentId;
    @MppMultiId // 复合主键
    @TableField("Term")
    private String term;
    @MppMultiId // 复合主键
    @TableField("Course_number")
    private String courseId;
    @TableField("Job_number")
    private String teacherId;
    @TableField("Time")
    private String time;   //需要在别的地方进行记录
    private Integer usualGrade;
    private Integer finalGrade;
    private Integer Score;
    private Double Grade_point;
}
