package domain.model.pesquisa;

import domain.exceptions.PesquisaEncerradaException;
import domain.exceptions.PesquisaInvalidaException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pesquisa implements Serializable {

    private final String codigo;
    private String nome;
    private String cargo;
    private LocalDate inicioColeta;
    private LocalDate fimColeta;
    private Abrangencia abrangencia;
    private TipoPesquisa tipo;
    private SituacaoPesquisa situacao = SituacaoPesquisa.PLANEJADA;
    private int qntdPrevista;
    private int qntdRealizada;

    public Pesquisa(String codigo, String nome, String cargo, LocalDate inicio, LocalDate fim,
                    Abrangencia abrangencia, TipoPesquisa tipo, int qntdPrevista) {

        if (codigo == null || codigo.isBlank()){
            throw new PesquisaInvalidaException("Código da pesquisa é obrigatória.");
        }
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new PesquisaInvalidaException("Período de coleta inválido.");
        }
        if (qntdPrevista <= 0) {
            throw new PesquisaInvalidaException("A quantidade prevista deve ser maior que zero");
        }

        this.codigo = codigo;
        this.nome = nome;
        this.cargo = cargo;
        this.inicioColeta = inicio;
        this.fimColeta = fim;
        this.abrangencia = abrangencia;
        this.tipo = tipo;
        this.qntdPrevista = qntdPrevista;
    }

}