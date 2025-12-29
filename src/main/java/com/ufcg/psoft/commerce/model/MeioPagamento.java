package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MeioPagamento {

    @JsonProperty("cartão_credito")
    CARTAO_CREDITO {
        @Override
        public double calcularTotal(double valor){
            return valor;
        }
    },
    @JsonProperty("cartao_debito")
    CARTAO_DEBITO {
        @Override
        public double calcularTotal(double valor){
            return valor * 0.975;
        }
    },
    @JsonProperty("pix")
    PIX {
        @Override
        public double calcularTotal(double valor){
            return valor * 0.95;
        }
    };
    public abstract double calcularTotal(double valor);
}
