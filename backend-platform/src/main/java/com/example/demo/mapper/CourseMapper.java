package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Course;
import com.example.demo.pojo.CourseWithCredit;
import com.example.demo.pojo.CourseWithName;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CourseMapper extends BaseMapper<Course> {
    @Select("select c.Course_number as id,name from course c,course_name n where c.Course_number = n.Course_number")
    List<CourseWithName> findAllCourseWithName();

    @Select("select Course_number as id,credit from course")
    List<CourseWithCredit> findCredit();

    @Select("select count(*) from course where id = #{id}")
    Integer isKeyRepeat(String id);

    @Select("select Job_number from course where Course_number = #{Course_Number} AND Term = #{Term}")
    List<Classes> findAllTeacherAmount(@Param("Course_Number") String courseNumber, @Param("Term") String term);
}
