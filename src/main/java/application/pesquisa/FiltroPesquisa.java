package application.pesquisa;

import domain.model.pesquisa.Abrangencia;
import domain.model.pesquisa.Pesquisa;
import domain.model.pesquisa.SituacaoPesquisa;
import domain.model.pesquisa.TipoPesquisa;
import java.time.LocalDate;

public class FiltroPesquisa {
    private LocalDate periodoInicio;
    private LocalDate periodoFim;
    private String cargo;
    private TipoPesquisa tipo;
    private SituacaoPesquisa situacao;
    private Abrangencia abrangencia;
    private Integer quantidadeMinimaPrevista;

    public FiltroPesquisa comPeriodo(LocalDate inicio, LocalDate fim) {
        this.periodoInicio = inicio;
        this.periodoFim = fim;
        return this;
    }
    public FiltroPesquisa comCargo(String cargo) { this.cargo = cargo; return this; }
    public FiltroPesquisa comTipo(TipoPesquisa tipo) { this.tipo = tipo; return this; }
    public FiltroPesquisa comSituacao(SituacaoPesquisa situacao) { this.situacao = situacao; return this; }
    public FiltroPesquisa comAbrangencia(Abrangencia abrangencia) { this.abrangencia = abrangencia; return this; }
    public FiltroPesquisa comQuantidadeMinimaPrevista(int qtd) { this.quantidadeMinimaPrevista = qtd; return this; }

    public boolean aceita(Pesquisa p) {
        if (periodoInicio != null && p.getInicioColeta().isBefore(periodoInicio)) return false;
        if (periodoFim != null && p.getFimColeta().isAfter(periodoFim)) return false;
        if (cargo != null && !cargo.equalsIgnoreCase(p.getCargo())) return false;
        if (tipo != null && p.getTipo() != tipo) return false;
        if (situacao != null && p.getSituacao() != situacao) return false;
        if (abrangencia != null && p.getAbrangencia() != abrangencia) return false;
        if (quantidadeMinimaPrevista != null && p.getQuantidadePrevista() < quantidadeMinimaPrevista) return false;
        return true;
    }
}