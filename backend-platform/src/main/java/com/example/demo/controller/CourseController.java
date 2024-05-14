package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.pojo.TeacherSingleCourse;
import com.example.demo.mapper.TeacherSingleCourseMapper;
import com.example.demo.entity.Classes;
import com.example.demo.entity.Course;
import com.example.demo.mapper.CourseMapper;
import com.example.demo.service.WebSocketServer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Resource
    CourseMapper courseMapper;

    @Resource
    WebSocketServer webSocketServer;

    @Resource
    TeacherSingleCourseMapper teacherSingleCourseMapper;

    @PostMapping
    public Result<?> save(@RequestBody Course course) {
        if (courseMapper.isKeyRepeat(course.getId()).intValue() != 0) {
            return Result.error("-1");
        }
        courseMapper.insert(course);
        return Result.success();
    }

/*    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "0") Integer selectDep) {
        LambdaQueryWrapper<Course> wrapper = Wrappers.<Course>lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(courseNameMapper::getName, search);
        }
        if (selectDep.intValue() != 0) {
            wrapper.eq(Course::getDepartment, selectDep);
        }

        Page<Course> coursePage = courseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(coursePage);
    }*/

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "0") Integer selectDep) {
        LambdaQueryWrapper<Course> wrapper = Wrappers.<Course>lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            // 添加连接条件
            wrapper.inSql(Course::getId, "SELECT Course_number FROM course_name WHERE name LIKE '%" + search + "%'");
        }
        //if (selectDep.intValue() != 0) {
        //    wrapper.eq(Course::getDepartment, selectDep);
        //}
        Page<Course> coursePage = courseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(coursePage);
    }



    @PutMapping
    public Result<?> update(@RequestBody Course course) {
        courseMapper.updateById(course);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id) {
        courseMapper.deleteById(id);
        return Result.success();
    }


    // 该函数能够将course实体中department字段中数字转换为汉字，方便在前端展示。
    // 但使用后分页问题无法解决，因此没有使用该函数，转换在前端想办法完成了
    /*public List<CourseVO> queryCourseDe() {
        List<Course> courses = courseMapper.selectList(null);
        List<CourseVO> courseVOS = new ArrayList<>();

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            Department department = departmentMapper.selectById(course.getDepartment());
            CourseVO courseVO = new CourseVO(course, department);

            courseVOS.add(courseVO);
        }
        return courseVOS;
    }*/

    @GetMapping("/withName")
    public Result<?> findAllWithName() {
        return Result.success(courseMapper.findAllCourseWithName());
    }

    @GetMapping("/getCredit")
    public Result<?> findAllCredit() {
        return Result.success(courseMapper.findCredit());
    }

    @GetMapping("/forTeacherWithSameCourse")
    private Result<?> findForTeacherWithSameCourse(@RequestParam(defaultValue = "") String search,
                                     @RequestParam(defaultValue = "") String teacherId) {
        List<TeacherSingleCourse> data = teacherSingleCourseMapper.findForTeacherWithSameCourse(teacherId, search);
        Integer total = data.size();
        Map<String, Object> res = new HashMap<>();
        res.put("list", data);
        res.put("total", total);
        return Result.success(res);
    }

    @GetMapping("/getAllTeacherAmount")
    public Result<?> findAllTeacherAmount(@RequestParam(defaultValue = "") String courseNumber,
                                          @RequestParam(defaultValue = "") String term) {
        List<Classes> data = courseMapper.findAllTeacherAmount(courseNumber,term);
        Integer total = data.size();
        Map<String, Object> res = new HashMap<>();
        res.put("list", data);
        res.put("total", total);
        return Result.success(res);
    }

    @GetMapping("/onlineUsers")
    public Result<?> getOnlineUsers() {
        Set<String> data = webSocketServer.getOnlineUsers();
        System.out.println("收到的在线用户为："+data);
        Map<String, Object> res = new HashMap<>();
        res.put("list", data);
        return Result.success(res);
    }
}
