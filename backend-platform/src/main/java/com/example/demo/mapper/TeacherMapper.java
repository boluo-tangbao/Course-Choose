package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.TeacherName;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeacherMapper extends BaseMapper<Teacher> {
    @Select("select t.id,tn.name from teacher t,teacher_name tn where t.id = tn.id")
    List<TeacherName> findAllTeacherWithName();

    @Select("select count(*) from teacher where id = #{id}")
    Integer isKeyRepeat(String id);
}
