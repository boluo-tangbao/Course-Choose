package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "teacher_name")
@Data
public class TeacherName {
    @Id
    @TableId("Job_number")
    private String id;//工号
    @Column(name = "Name")
    private String name;//姓名
}
