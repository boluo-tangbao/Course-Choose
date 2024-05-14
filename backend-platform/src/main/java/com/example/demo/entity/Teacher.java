package com.example.demo.entity;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "teacher")
@Data
@SecondaryTables({
        @SecondaryTable(name = "teacher_name", pkJoinColumns = @PrimaryKeyJoinColumn(name = "Job_number")),
        @SecondaryTable(name = "yuan_xi", pkJoinColumns = @PrimaryKeyJoinColumn(name = "Department"))
})
public class Teacher {
    @Id
    //@TableId(type = IdType.AUTO)
    @TableId("Job_number")
    private String id;//工号
    private String Gender;//性别
    private String Password;//密码
    private String Professional_title;//职称
    private String In_school;//在校与否
    //@Column(table = "teacher_name")
    //private String Name;//名字
    private Integer Department;//所属系
}

