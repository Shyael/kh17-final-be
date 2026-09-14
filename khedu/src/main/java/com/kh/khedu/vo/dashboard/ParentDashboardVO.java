package com.kh.khedu.vo.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ParentDashboardVO implements AcademyDashboardVO {
	// 연결된 자녀별 대시보드 정보
    private List<DashboardChildVO> children;
}
