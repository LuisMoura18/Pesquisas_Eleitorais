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
import application.admin.SessaoUsuario;
import domain.exceptions.AcessoNegadoException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.Usuario;
import domain.model.admin.StatusUsuario;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PesquisaService {
    //verifica se o usuario está permitido pelo admin
    private void verificarPermissaoPesquisa() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null
                || logado.getStatus() != StatusUsuario.ATIVO
                || !(logado.getPerfis().contains(PerfilAcesso.ADMINISTRADOR)
                || logado.getPerfis().contains(PerfilAcesso.PESQUISADOR))) {
            throw new AcessoNegadoException("Funcionalidade restrita a Administradores e Pesquisadores.");
        }
    }
    //verifica se o usuario é o analista
    private void verificarPermissaoProcessamento() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();
        if (logado == null
                || logado.getStatus() != StatusUsuario.ATIVO
                || !(logado.getPerfis().contains(PerfilAcesso.ADMINISTRADOR)
                || logado.getPerfis().contains(PerfilAcesso.ANALISTA))) {
            throw new AcessoNegadoException("Funcionalidade restrita a Administradores e Analistas.");
        }
    }
    private final PesquisaRepository repositorio;

    public PesquisaService(PesquisaRepository repositorio) {
        this.repositorio = repositorio;
    }

    //CRUD
    public Pesquisa cadastrar(String codigo, String nome, String cargo, LocalDate inicio,
                              LocalDate fim, Abrangencia abrangencia, TipoPesquisa tipo,
                              int quantidadePrevista) {
        verificarPermissaoPesquisa();
        if (repositorio.buscarPorCodigo(codigo).isPresent())
            throw new PesquisaInvalidaException("Já existe pesquisa com o código " + codigo);

        Pesquisa pesquisa = new Pesquisa(codigo, nome, cargo, inicio, fim,
                abrangencia, tipo, quantidadePrevista);
        repositorio.salvar(pesquisa);
        return pesquisa;
    }

    public Pesquisa editar(String codigo, String nome, String cargo, LocalDate inicio,
                           LocalDate fim, Abrangencia abrangencia, TipoPesquisa tipo,
                           int quantidadePrevista) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.editar(nome, cargo, inicio, fim, abrangencia, tipo, quantidadePrevista);
        repositorio.salvar(p);
        return p;
    }

    public void excluir(String codigo) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        if (!p.podeSerExcluida())
            throw new PesquisaInvalidaException(
                    "Não é possível excluir uma pesquisa processada ou que já possui entrevistas.");
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

    public void adicionarPergunta(String codigo, Pergunta pergunta) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.adicionarPergunta(pergunta);
        repositorio.salvar(p);
    }

    public void editarPergunta(String codigo, int ordem, String enunciado,
                               TipoPergunta tipo, boolean obrigatoria) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.editarPergunta(ordem, enunciado, tipo, obrigatoria);
        repositorio.salvar(p);
    }

    public void removerPergunta(String codigo, int ordem) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.removerPergunta(ordem);
        repositorio.salvar(p);
    }

    public void adicionarOpcao(String codigo, int ordemPergunta, OpcaoResposta opcao) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.adicionarOpcao(ordemPergunta, opcao);
        repositorio.salvar(p);
    }

    public void removerOpcao(String codigo, int ordemPergunta, int ordemOpcao) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.removerOpcao(ordemPergunta, ordemOpcao);
        repositorio.salvar(p);
    }


    public void associarCandidato(String codigo, String numero, String nome) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.associarCandidato(numero, nome);
        repositorio.salvar(p);
    }

    public void associarRegiao(String codigo, String regiao) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.associarRegiao(regiao);
        repositorio.salvar(p);
    }

    public void definirPlanoAmostral(String codigo, PlanoAmostral plano) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.definirPlanoAmostral(plano);
        repositorio.salvar(p);
    }

    public List<GrupoAmostral> gruposQueNaoAtingiramMeta(String codigo,
                                                         Map<Long, Integer> entrevistasValidasPorGrupo) {
        return buscar(codigo).gruposQueNaoAtingiramMeta(entrevistasValidasPorGrupo);
    }

    public void iniciarColeta(String codigo) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.iniciarColeta();
        repositorio.salvar(p);
    }

    public void encerrarColeta(String codigo) {
        verificarPermissaoPesquisa();
        Pesquisa p = buscar(codigo);
        p.encerrarColeta();
        repositorio.salvar(p);
    }

    public void processar(String codigo) {
        verificarPermissaoProcessamento();
        Pesquisa p = buscar(codigo);
        p.processar();
        repositorio.salvar(p);
    }

    //método chamada pela coleta
    public void registrarEntrevista(String codigo) {
        verificarPermissaoPesquisa();
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