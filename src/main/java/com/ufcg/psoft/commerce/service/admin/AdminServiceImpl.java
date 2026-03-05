package com.ufcg.psoft.commerce.service.admin;

import com.ufcg.psoft.commerce.dto.AdminPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.AdminResponseDTO;
import com.ufcg.psoft.commerce.exception.AdminJaExisteException;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.repository.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public AdminResponseDTO criarAdmin(AdminPostPutRequestDTO dto) {

        if (adminRepository.count() > 0) {
            throw new AdminJaExisteException();
        }

        Admin admin = Admin.builder()
                .nome(dto.getNome())
                .senha(dto.getSenha())
                .build();

        return new AdminResponseDTO(adminRepository.save(admin));
    }

}
