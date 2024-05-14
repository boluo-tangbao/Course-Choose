package com.example.demo.entity;/*package com.example.demo.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("classes")
@Data
public class Classes {
    @TableId(type = IdType.AUTO)
    private String id;
    private String term;
    private String courseId;
    private String courseName;
    private String teacherId;
    private String teacherName;
    private String time;
    private Integer limitNum;
    private Integer currentNum;
}*/

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;

@Entity
@TableName("course")
@Data
@SecondaryTable(name = "teacher_name", pkJoinColumns = @PrimaryKeyJoinColumn(name = "Job_number"))
public class Classes {
    @Id
    @TableId("Course_number")
    private String id;
    private String Credit;
    private String JobNumber;
    private String Term;
    private String Time;
    private String Classroom;
    private Integer CurCapacity;
    private Integer Capacity;
}
