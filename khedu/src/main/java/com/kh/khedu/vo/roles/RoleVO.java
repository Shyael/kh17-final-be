package com.kh.khedu.vo.roles;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "권한 조회용 VO")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RoleVO {
    private int roleNo;
    private String roleName;
    private String roleDescription;
}
