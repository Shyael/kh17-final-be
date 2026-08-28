package com.kh.khedu.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.kh.khedu.vo.student.StudentListResponseVO;

@Mapper
public interface StudentDao {
    List<StudentListResponseVO> selectList();
    int sequence();
}