package com.kh.khedu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.khedu.dao.ClassroomDao;
import com.kh.khedu.vo.classroom.ClassroomWhenRegisterVO;

@Service
public class ClassroomServiceImpl implements ClassroomService {

	@Autowired
    private ClassroomDao classroomDao;

    @Override
    public List<ClassroomWhenRegisterVO> getClassroomList() {
        return classroomDao.classroomListWhenRegister();
    }

}
