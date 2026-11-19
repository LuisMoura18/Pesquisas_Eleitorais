package presentation.controller;

import application.pesquisa.AndamentoPesquisa;
import application.pesquisa.FiltroPesquisa;
import application.pesquisa.PesquisaService;
import domain.model.pesquisa.Abrangencia;
import domain.model.pesquisa.GrupoAmostral;
import domain.model.pesquisa.OpcaoResposta;
import domain.model.pesquisa.Pergunta;
import domain.model.pesquisa.Pesquisa;
import domain.model.pesquisa.PlanoAmostral;
import domain.model.pesquisa.SituacaoPesquisa;
import domain.model.pesquisa.TipoPergunta;
import domain.model.pesquisa.TipoPesquisa;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PesquisaFxController {

    private final PesquisaService service;

    public PesquisaFxController(PesquisaService service) {
        this.service = service;
    }

    public List<Pesquisa> listarTodas() {
        return service.listar();
    }

    public Pesquisa buscarPorCodigo(String codigo) {
        return service.buscar(codigo);
    }

    public List<Pesquisa> filtrar(LocalDate inicio, LocalDate fim, String cargo,
                                  TipoPesquisa tipo, SituacaoPesquisa situacao,
                                  Abrangencia abrangencia, Integer qtdMinimaPrevista) {
        FiltroPesquisa filtro = new FiltroPesquisa();
        if (inicio != null || fim != null) filtro.comPeriodo(inicio, fim);
        if (cargo != null && !cargo.isBlank()) filtro.comCargo(cargo.trim());
        if (tipo != null) filtro.comTipo(tipo);
        if (situacao != null) filtro.comSituacao(situacao);
        if (abrangencia != null) filtro.comAbrangencia(abrangencia);
        if (qtdMinimaPrevista != null) filtro.comQuantidadeMinimaPrevista(qtdMinimaPrevista);
        return service.consultar(filtro);
    }

    public void cadastrar(String codigo, String nome, String cargo, LocalDate inicio,
                          LocalDate fim, Abrangencia abrangencia, TipoPesquisa tipo, int qtdPrevista) {
        service.cadastrar(codigo, nome, cargo, inicio, fim, abrangencia, tipo, qtdPrevista);
    }

    public void editar(String codigo, String nome, String cargo, LocalDate inicio,
                       LocalDate fim, Abrangencia abrangencia, TipoPesquisa tipo, int qtdPrevista) {
        service.editar(codigo, nome, cargo, inicio, fim, abrangencia, tipo, qtdPrevista);
    }

    public void excluir(String codigo) {
        service.excluir(codigo);
    }

    public void iniciarColeta(String codigo) {
        service.iniciarColeta(codigo);
    }

    public void encerrarColeta(String codigo) {
        service.encerrarColeta(codigo);
    }

    public void processar(String codigo) {
        service.processar(codigo);
    }

    public AndamentoPesquisa acompanhar(String codigo) {
        return service.acompanhar(codigo);
    }

    public void adicionarPergunta(String codigo, Pergunta pergunta) {
        service.adicionarPergunta(codigo, pergunta);
    }

    public void editarPergunta(String codigo, int ordem, String enunciado, TipoPergunta tipo, boolean obrigatoria) {
        service.editarPergunta(codigo, ordem, enunciado, tipo, obrigatoria);
    }

    public void removerPergunta(String codigo, int ordem) {
        service.removerPergunta(codigo, ordem);
    }

    public void adicionarOpcao(String codigo, int ordemPergunta, OpcaoResposta opcao) {
        service.adicionarOpcao(codigo, ordemPergunta, opcao);
    }

    public void removerOpcao(String codigo, int ordemPergunta, int ordemOpcao) {
        service.removerOpcao(codigo, ordemPergunta, ordemOpcao);
    }

    public void associarCandidato(String codigo, String numero, String nome) {
        service.associarCandidato(codigo, numero, nome);
    }

    public void associarRegiao(String codigo, String regiao) {
        service.associarRegiao(codigo, regiao);
    }

    public void definirPlanoAmostral(String codigo, PlanoAmostral plano) {
        service.definirPlanoAmostral(codigo, plano);
    }

    public List<GrupoAmostral> gruposQueNaoAtingiramMeta(String codigo, Map<Long, Integer> validasPorGrupo) {
        return service.gruposQueNaoAtingiramMeta(codigo, validasPorGrupo);
    }
}