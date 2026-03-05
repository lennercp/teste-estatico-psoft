package com.ufcg.psoft.commerce.model.state;

import com.ufcg.psoft.commerce.exception.ChamadoConcluidoNaoCanceladoException;
import com.ufcg.psoft.commerce.exception.ChamadoJaCanceladoException;
import com.ufcg.psoft.commerce.exception.ClienteNaoAutorizadoCancelarChamadoException;

public enum StatusChamado implements StatusComportamento {
    RECEBIDO {
        @Override
        public StatusChamado avancar() { return EM_ANALISE; }

        @Override
        public StatusChamado cancelar(Long sId, Long dId) {
            if (!sId.equals(dId)) {
                throw new ClienteNaoAutorizadoCancelarChamadoException();

            }

            return CANCELADO;

        }

        @Override
        public String getDescricao() { return "RECEBIDO"; }
    },
    EM_ANALISE {
        @Override
        public StatusChamado avancar() { return AGUARDANDO_TECNICO; }

        @Override
        public StatusChamado cancelar(Long sId, Long dId) {
            if (!sId.equals(dId)) {
                throw new ClienteNaoAutorizadoCancelarChamadoException();

            }

            return CANCELADO;
        }

        @Override
        public String getDescricao() { return "EM_ANALISE"; }
    },
    AGUARDANDO_TECNICO {
        @Override
        public StatusChamado avancar() { return ATENDIMENTO; }

        @Override
        public StatusChamado cancelar(Long sId, Long dId) {
            if (!sId.equals(dId)) {
                throw new ClienteNaoAutorizadoCancelarChamadoException();

            }

            return CANCELADO;
        }

        @Override
        public String getDescricao() { return "AGUARDANDO_TECNICO"; }
    },
    ATENDIMENTO {
        @Override
        public StatusChamado avancar() { return CONCLUIDO; }

        @Override
        public StatusChamado cancelar(Long sId, Long dId) {
        throw new ClienteNaoAutorizadoCancelarChamadoException(); }

        @Override
        public String getDescricao() { return "ATENDIMENTO"; }
    },
    CONCLUIDO {
        @Override public StatusChamado avancar() { return this; }
        @Override public StatusChamado cancelar(Long sId, Long dId) {
            throw new ChamadoConcluidoNaoCanceladoException();
        }
        @Override public String getDescricao() { return "CONCLUIDO"; }
    },
    CANCELADO {
        @Override public StatusChamado avancar() { return this; }
        @Override public StatusChamado cancelar(Long sId, Long dId) {
            throw new ChamadoJaCanceladoException();
        }
        @Override public String getDescricao() { return "CANCELADO"; }
    }
}
