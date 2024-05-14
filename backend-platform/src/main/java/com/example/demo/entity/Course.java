package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Entity
@TableName("course")
@Data
@SecondaryTable(name = "teacher_name", pkJoinColumns = @PrimaryKeyJoinColumn(name = "Job_number"))
public class Course {
    @Id
    @TableId("Course_number")
    private String id;
    private String Credit;
    private String Job_number;
    private String Time;
    private String Classroom;
    private Integer CurCapacity;
    private Integer Capacity;
}
