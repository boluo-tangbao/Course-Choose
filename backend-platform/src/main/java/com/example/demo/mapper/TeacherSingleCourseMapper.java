package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.TeacherSingleCourse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TeacherSingleCourseMapper extends BaseMapper<TeacherSingleCourse> {
    @Select("SELECT c.Course_number AS courseNumber, " +
            "c.Credit, " +
            "c.Job_number AS jobNumber, " +
            "c.Term, " +
            "c.Time, " +
            "c.Classroom, " +
            "c.CurCapacity AS curCapacity, " +
            "c.Capacity, " +
            "cn.Course_name AS courseName " +
            "FROM course c " +
            "INNER JOIN course_name cn ON c.Course_number = cn.Course_number " +
            "WHERE c.Job_number = #{jobNumber} AND cn.Course_name LIKE CONCAT('%', #{courseName}, '%')")
    List<TeacherSingleCourse> findForTeacher(@Param("jobNumber") String jobNumber, @Param("courseName") String courseName);

    @Select("SELECT tmp.*,count(DISTINCT Job_number) as teacherAmount from course c1," +
            "(SELECT c.Course_number AS courseNumber, " +
            "c.Credit, " +
            "c.Job_number AS jobNumber, " +
            "c.Term, " +
            "c.Time, " +
            "cn.Course_name AS courseName " +
            "FROM course c INNER JOIN course_name cn ON c.Course_number = cn.Course_number " +
            "WHERE c.Job_number = #{jobNumber} AND cn.Course_name LIKE CONCAT('%', #{courseName}, '%')) tmp " +
            "where c1.Course_number = tmp.courseNumber AND c1.Term = tmp.Term")
    List<TeacherSingleCourse> findForTeacherWithSameCourse(@Param("jobNumber") String jobNumber, @Param("courseName") String courseName);
}
