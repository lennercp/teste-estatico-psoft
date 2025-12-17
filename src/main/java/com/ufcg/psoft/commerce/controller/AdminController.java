package com.ufcg.psoft.commerce.controller;
import com.ufcg.psoft.commerce.dto.AdminPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.AdminResponseDTO;
import com.ufcg.psoft.commerce.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminResponseDTO> criarAdmin(
            @RequestBody @Valid AdminPostPutRequestDTO dto) {

        AdminResponseDTO response = adminService.criarAdmin(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}

