package com.example.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.mapper.TeacherNameMapper;
import com.example.demo.entity.Teacher;
import com.example.demo.mapper.TeacherMapper;
import com.example.demo.entity.TeacherName;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/teacher")
public class TeacherController {
    @Resource
    TeacherMapper teacherMapper;
    @Resource
    TeacherNameMapper teacherNameMapper;

    @PostMapping
    public Result<?> save(@RequestBody Teacher teacher) {
        if (teacherMapper.isKeyRepeat(teacher.getId()).intValue() != 0) {
            return Result.error("-1");
        }
        if (teacher.getPassword() == null) {
            teacher.setPassword(teacher.getId());
        }
        teacherMapper.insert(teacher);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id) {
        teacherMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Teacher teacher) {
        // 前端传过来的数据password字段为null。
        // 下列操作可以保证插入数据库的数据中password字段值不为null，这一步可以不做
        // 后来发现包含null字段的实体插入数据库时，null不会覆盖原有数据，或许是框架自身功能，因此这一步可以不做
        // Teacher fromDb = teacherMapper.selectOne(Wrappers.<Teacher>lambdaQuery().eq(Teacher::getId, teacher.getId()));
        // teacher.setPassword(fromDb.getPassword());

        teacherMapper.updateById(teacher);
        return Result.success();
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "0") Integer selectDep) {
        LambdaQueryWrapper<Teacher> wrapper = Wrappers.<Teacher>lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Teacher::getId, search);
        }
/*        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Teacher::getName, search);
        }*/
        if (selectDep.intValue() != 0) {
            wrapper.eq(Teacher::getDepartment, selectDep);
        }
        // SystemAdministrator字段不需要在前端展示，删去
        wrapper.ne(Teacher::getId, 100);
        Page<Teacher> teacherPage = teacherMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(teacherPage);
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody Teacher request) {
        Teacher res = teacherMapper.selectOne(
                Wrappers.<Teacher>lambdaQuery()
                        .eq(Teacher::getId, request.getId())
                        .eq(Teacher::getPassword, request.getPassword())
        );

        if (res == null) {
            return Result.error("-1");
        }

        // 查询 Teacher_Name 表中的姓名数据
        TeacherName name = teacherNameMapper.selectOne(
                Wrappers.<TeacherName>lambdaQuery()
                        .eq(TeacherName::getId, request.getId())
        );

        if (name == null) {
            return Result.error("-1");
        }

        return Result.success(name);
    }

    @GetMapping("/withName")
    public Result<?> findAllWithName() {
        return Result.success(teacherMapper.findAllTeacherWithName());
    }
}
