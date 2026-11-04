
package domain.model.analise;

import java.time.LocalDate;

public class FiltroRelatorio {

    private String regiao;
    private String faixaEtaria;
    private String escolaridade;
    private LocalDate periodoInicial;
    private LocalDate periodoFinal;
    private String pesquisa;
    private String candidato;

    public void definirRegiao(String regiao) {
        this.regiao = regiao;
    }

    public void definirFaixaEtaria(String faixaEtaria) {
        this.faixaEtaria = faixaEtaria;
    }

    public void definirEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public void definirPeriodo(LocalDate inicio, LocalDate fim) {
        this.periodoInicial = inicio;
        this.periodoFinal = fim;
    }

    public void definirPesquisa(String pesquisa) {
        this.pesquisa = pesquisa;
    }

    public void definirCandidato(String candidato) {
        this.candidato = candidato;
    }

    public boolean validarFiltros() {
        return periodoInicial == null || periodoFinal == null
                || !periodoInicial.isAfter(periodoFinal);
    }

    public void limparFiltros() {
        regiao = null;
        faixaEtaria = null;
        escolaridade = null;
        periodoInicial = null;
        periodoFinal = null;
        pesquisa = null;
        candidato = null;
    }
}
