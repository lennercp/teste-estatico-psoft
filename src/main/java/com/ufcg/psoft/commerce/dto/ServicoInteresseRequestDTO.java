package com.ufcg.psoft.commerce.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoInteresseRequestDTO {

    @JsonProperty("id_cliente")
    private Long id;

    @JsonProperty("codigo_acesso")
    private String codigoAcesso;

}
