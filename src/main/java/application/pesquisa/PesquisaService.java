package application.pesquisa;

import domain.exceptions.PesquisaInvalidaException;
import domain.exceptions.PesquisaNaoEncontradaException;
import domain.model.pesquisa.Abrangencia;
import domain.model.pesquisa.GrupoAmostral;
import domain.model.pesquisa.OpcaoResposta;
import domain.model.pesquisa.Pergunta;
import domain.model.pesquisa.Pesquisa;
import domain.model.pesquisa.PlanoAmostral;
import domain.model.pesquisa.TipoPergunta;
import domain.model.pesquisa.TipoPesquisa;
import domain.repository.PesquisaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PesquisaService {
    private final PesquisaRepository repositorio;

    public PesquisaService(PesquisaRepository repositorio) {
        this.repositorio = repositorio;
    }
    //CRUD PESQUISA
    public Pesquisa cadastrar(String codigo, String nome, String cargo, LocalDate inicio,
                              LocalDate fim, Abrangencia abrangencia, TipoPesquisa tipo,
                              int quantidadePrevista) {
        if (repositorio.buscarPorCodigo(codigo).isPresent())
            throw new PesquisaInvalidaException("Já existe pesquisa com o código " + codigo);

        Pesquisa pesquisa = new Pesquisa(codigo, nome, cargo, inicio, fim, abrangencia, tipo, quantidadePrevista);
        repositorio.salvar(pesquisa);
        return pesquisa;
    }

    public Pesquisa editar(String codigo, String nome, String cargo, LocalDate inicio,
                           LocalDate fim, Abrangencia abrangencia, TipoPesquisa tipo,
                           int quantidadePrevista) {
        Pesquisa p = buscar(codigo);
        p.editar(nome, cargo, inicio, fim, abrangencia, tipo, quantidadePrevista);
        repositorio.salvar(p);
        return p;
    }

    public void excluir(String codigo) {
        Pesquisa p = buscar(codigo);
        if (!p.podeSerExcluida())
            throw new PesquisaInvalidaException("Não é possível excluir uma pesquisa processada ou que já possui entrevistas.");
        repositorio.excluir(codigo);
    }

    public Pesquisa buscar(String codigo) {
        return repositorio.buscarPorCodigo(codigo)
                .orElseThrow(() -> new PesquisaNaoEncontradaException(codigo));
    }

    public List<Pesquisa> listar() {
        return repositorio.listar();
    }

    public List<Pesquisa> consultar(FiltroPesquisa filtro) {
        return repositorio.listar().stream().filter(filtro::aceita).toList();
    }

    //Perguntas e opcoes
    public void adicionarPergunta(String codigo, Pergunta pergunta) {
        Pesquisa p = buscar(codigo);
        p.adicionarPergunta(pergunta);
        repositorio.salvar(p);
    }

    public void editarPergunta(String codigo, int ordem, String enunciado,
                               TipoPergunta tipo, boolean obrigatoria) {
        Pesquisa p = buscar(codigo);
        p.editarPergunta(ordem, enunciado, tipo, obrigatoria);
        repositorio.salvar(p);
    }

    public void removerPergunta(String codigo, int ordem) {
        Pesquisa p = buscar(codigo);
        p.removerPergunta(ordem);
        repositorio.salvar(p);
    }

    public void adicionarOpcao(String codigo, int ordemPergunta, OpcaoResposta opcao) {
        Pesquisa p = buscar(codigo);
        p.adicionarOpcao(ordemPergunta, opcao);
        repositorio.salvar(p);
    }

    public void removerOpcao(String codigo, int ordemPergunta, int ordemOpcao) {
        Pesquisa p = buscar(codigo);
        p.removerOpcao(ordemPergunta, ordemOpcao);
        repositorio.salvar(p);
    }

    //Plano amostral
    public void definirPlanoAmostral(String codigo, PlanoAmostral plano) {
        Pesquisa p = buscar(codigo);
        p.definirPlanoAmostral(plano);
        repositorio.salvar(p);
    }

    public List<GrupoAmostral> gruposQueNaoAtingiramMeta(String codigo, Map<Long, Integer> entrevistasValidasPorGrupo) {
        return buscar(codigo).gruposQueNaoAtingiramMeta(entrevistasValidasPorGrupo);
    }

    //Situacao e andamento
    public void iniciarColeta(String codigo) {
        Pesquisa p = buscar(codigo);
        p.iniciarColeta();
        repositorio.salvar(p);
    }

    public void encerrarColeta(String codigo) {
        Pesquisa p = buscar(codigo);
        p.encerrarColeta();
        repositorio.salvar(p);
    }

    public void processar(String codigo) {
        Pesquisa p = buscar(codigo);
        p.processar();
        repositorio.salvar(p);
    }

    public void registrarEntrevista(String codigo) {
        Pesquisa p = buscar(codigo);
        p.registrarEntrevista();
        repositorio.salvar(p);
    }

    public AndamentoPesquisa acompanhar(String codigo) {
        Pesquisa p = buscar(codigo);
        return new AndamentoPesquisa(p.getCodigo(), p.getQuantidadePrevista(),
                p.getQuantidadeRealizada(), p.percentualConclusao());
    }
}