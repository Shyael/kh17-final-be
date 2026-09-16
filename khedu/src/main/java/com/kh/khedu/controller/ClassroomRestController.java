package com.kh.khedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.khedu.service.ClassroomService;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "강의실 API")
@RestController
@RequestMapping("/api/employee/classroom")
public class ClassroomRestController {

    @Autowired
    private ClassroomService classroomService;

    @GetMapping
    public List<ClassroomWhenRegisterVO> classroomList() {
        return classroomService.getClassroomList();
    }
}
