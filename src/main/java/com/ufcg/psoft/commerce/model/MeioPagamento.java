package com.ufcg.psoft.commerce.model;

public enum MeioPagamento {

    CARTAO_CREDITO {
        @Override
        public double calcularTotal(double valor){
            return valor;
        }
    },
    CARTAO_DEBITO {
        @Override
        public double calcularTotal(double valor){
            return valor * 0.975;
        }
    },
    PIX {
        @Override
        public double calcularTotal(double valor){
            return valor * 0.95;
        }
    };
    public abstract double calcularTotal(double valor);
}
