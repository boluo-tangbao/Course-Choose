package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Entity
@TableName("course_name")
@Data
@SecondaryTable(name = "course", pkJoinColumns = @PrimaryKeyJoinColumn(name = "Course_number"))
public class CourseName {
    @Id

    @TableId("Course_number")
    private String CourseNumber;
    @TableField("Course_name")
    private String Name;

}
