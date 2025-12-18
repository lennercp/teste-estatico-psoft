package com.ufcg.psoft.commerce.service.admin;

import com.ufcg.psoft.commerce.dto.AdminPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.AdminResponseDTO;

public interface AdminService {
    AdminResponseDTO criarAdmin(AdminPostPutRequestDTO dto);
}
