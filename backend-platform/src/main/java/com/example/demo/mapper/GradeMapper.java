package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Grade;
import com.example.demo.pojo.GradePlus;
import com.example.demo.pojo.GradeWithStudentName;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface GradeMapper extends BaseMapper<Grade> {
    // 该函数返回若非0，说明不满足选课条件
    // 此时未加入时间限制
    @Select("select count(*) from student_course where job_number = #{studentId} and term = #{term} and " +
            "time = #{time} and course_number = #{courseId} and teacher_number = #{teacherId}")
    Integer findIsChosen(String studentId, String term, String courseId, String teacherId, String time);

    // 加入时间限制
    @Select("select count(distinct c.term,c.time) from classes as c join grade as g " +
            "on g.student_number = #{studentId} and c.term = g.term and c.course_number = g.course_number " +
            "and c.job_number = g.job_number and c.time = g.time " +
            "and c.term = #{term} and c.time = #{time}")
    Integer findIsConflicting(String studentId, String term, String time);

    // 发起delete请求时，后端无法接受对象参数，原因不明，该方法废弃
    // 出问题可能是由于前端封装时键值没有打双引号，后端SQL语句中字段名与数据库中不匹配，或是此处注解使用错误造成
    // 之后写的方法能够出效果，懒得改了
    @Select("delete from student_course where score = 0 and term = #{term} " +
            "and course_number = #{courseId} and job_number = #{teacherId} and student_number = #{studentId}")
    Integer deleteForQuit(String term, String courseId, String teacherId, String studentId);

    @Select("select id from student_course where score = 0 and term = #{term} " +
            "and course_number = #{courseId} and job_number = #{teacherId} and student_number = #{studentId} " +
            "and time = #{time}")
    Integer findCertainId(String term, String courseId, String teacherId, String studentId, String time);

    @Select("select s.student_number,s.name,g.usualgrade,g.finalgrade,g.score " +
            "from student_course as g join student_name as s on g.term = #{term} and g.Course_number = #{courseId} " +
            "and g.Job_number = #{teacherId} and g.time = #{time} and g.student_number = s.student_number")
    List<GradeWithStudentName> findListForGrade(String term, String courseId, String teacherId, String time);

    @Update("update student_course set usualgrade = #{usualGrade}, finalgrade = #{finalGrade} " +
            "where term = #{term} and student_number = #{studentId} and Job_number = #{teacherId} and " +
            "course_number = #{courseId} and time = #{time}")
    Integer updateByLogging(String term, String courseId, String teacherId, String studentId, String time, Integer usualGrade, Integer finalGrade);

    @Update("update student_course set score = usualgrade * #{usualGradeProportion} + " +
            "finalgrade * #{finalGradeProportion} where term = #{term} and " +
            "Job_number = #{teacherId} and course_number = #{courseId} and time = #{time}")
    Integer updateTotal(String term, String courseId, String teacherId, String time, Double usualGradeProportion, Double finalGradeProportion);

    @Update("UPDATE student_course" +
            "        SET Grade_point = (    CASE\n" +
            "        WHEN score >= 90 THEN 4.0\n" +
            "        WHEN score >= 85 THEN 3.7\n" +
            "        WHEN score >= 82 THEN 3.3\n" +
            "        WHEN score >= 78 THEN 3.0\n" +
            "        WHEN score >= 75 THEN 2.7\n" +
            "        WHEN score >= 72 THEN 2.3\n" +
            "        WHEN score >= 68 THEN 2.0\n" +
            "        WHEN score >= 66 THEN 1.7\n" +
            "        WHEN score >= 64 THEN 1.5\n" +
            "        WHEN score >= 60 THEN 1.0\n" +
            "        ELSE 0.0\n" +
            "    END\n)" +
            "        where term = #{term} and " +
            "Job_number = #{teacherId} and course_number = #{courseId} and time = #{time}")
    Integer updateGPA(String term, String courseId, String teacherId, String time);

    @Select("select g.term,g.course_number,c.name as course_name,g.job_number,t.name as teacher_name,g.usualgrade,g.finalgrade," +
            "g.score from student_course as g join course as c join teacher as t " +
            "on student_number = #{studentId} and g.course_number = c.course_number and g.job_number = t.job_number " +
            "and g.term like concat('%', #{term}, '%')")
    List<GradePlus> findMyGrade(String studentId, String term);


    @Select("select count(*) from student_course where student_number = #{studentId} and term = #{term} and course_number = #{courseId}")
    Integer findIsRepeat(String studentId, String term, String courseId);
}
