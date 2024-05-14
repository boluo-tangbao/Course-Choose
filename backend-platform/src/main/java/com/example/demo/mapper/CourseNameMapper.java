package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.CourseName;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CourseNameMapper extends BaseMapper<CourseName> {
    @Select("select Course_number,Course_name from course_name")
    List<CourseName> selectAll();

    @Select("select Course_number,Course_name from course_name where Course_number = #{classId} ")
    CourseName findByCourseNumber(String classId);
}
