package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChamadoPostPutRequestDTO {

    @JsonProperty("servico_id")
    @NotNull(message = "Serviço obrigatório")
    private Long servicoId;

    @JsonProperty("empresa_cnpj")
    @NotBlank(message = "CNPJ da empresa obrigatório")
    private String empresaCnpj;

    @JsonProperty("endereco")
    @NotBlank(message = "Endereço obrigatório")
    private String endereco;

    @JsonProperty("cliente_id")
    @NotNull(message = "Cliente obrigatório")
    private Long clienteId;

    @JsonProperty("tecnico_id")
    private Long tecnicoId; // Pode ser nulo no início

    @JsonProperty("status_acao")
    private String statusAcao;

    @JsonProperty("codigo")
    @Pattern(regexp = "^\\d{6}$", message = "Código de acesso deve ter exatamente 6 dígitos numéricos")
    private String codigo;

    public String getEmpresaCnpj() {
        return empresaCnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public long getClienteId() {
        return clienteId;
    }

    public long getServicoId() {
        return servicoId;
    }

    public String getCodigoAcesso() {
        return codigo;
    }
}
