package domain.model.pesquisa;

import domain.exceptions.PesquisaEncerradaException;
import domain.exceptions.PesquisaInvalidaException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Pesquisa implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String codigo;
    private String nome;
    private String cargo;
    private LocalDate inicioColeta;
    private LocalDate fimColeta;
    private Abrangencia abrangencia;
    private TipoPesquisa tipo;
    private SituacaoPesquisa situacao = SituacaoPesquisa.PLANEJADA;
    private int quantidadePrevista;
    private int quantidadeRealizada;
    private final List<Pergunta> perguntas = new ArrayList<>();
    private PlanoAmostral planoAmostral;

    public Pesquisa(String codigo, String nome, String cargo, LocalDate inicio,
                    LocalDate fim, Abrangencia abrangencia, TipoPesquisa tipo,
                    int quantidadePrevista) {
        if (codigo == null || codigo.isBlank())
            throw new PesquisaInvalidaException("Código da pesquisa é obrigatório.");
        validarDados(abrangencia, tipo, inicio, fim, quantidadePrevista);

        this.codigo = codigo;
        this.nome = nome;
        this.cargo = cargo;
        this.inicioColeta = inicio;
        this.fimColeta = fim;
        this.abrangencia = abrangencia;
        this.tipo = tipo;
        this.quantidadePrevista = quantidadePrevista;
    }

    //Dados
    public void editar(String nome, String cargo, LocalDate inicio, LocalDate fim,
                       Abrangencia abrangencia, TipoPesquisa tipo, int quantidadePrevista) {
        exigirAlteravel();
        validarDados(abrangencia, tipo, inicio, fim, quantidadePrevista);
        if (quantidadePrevista < quantidadeRealizada)
            throw new PesquisaInvalidaException(
                    "A quantidade prevista não pode ser menor que as entrevistas já realizadas (" + quantidadeRealizada + ").");
        if (planoAmostral != null && planoAmostral.totalEntrevistas() > quantidadePrevista)
            throw new PesquisaInvalidaException(
                    "A quantidade prevista não comporta as metas do plano amostral (" + planoAmostral.totalEntrevistas() + ").");

        this.nome = nome;
        this.cargo = cargo;
        this.inicioColeta = inicio;
        this.fimColeta = fim;
        this.abrangencia = abrangencia;
        this.tipo = tipo;
        this.quantidadePrevista = quantidadePrevista;
    }

    public boolean podeSerAlterada() {
        return situacao != SituacaoPesquisa.PROCESSADA;
    }

    public boolean podeSerExcluida() {
        return situacao != SituacaoPesquisa.PROCESSADA && quantidadeRealizada == 0;
    }

    //Perguntas
    public void adicionarPergunta(Pergunta pergunta) {
        exigirPlanejada("adicionar perguntas");
        for (Pergunta p : perguntas) {
            if (p.getOrdem() == pergunta.getOrdem())
                throw new PesquisaInvalidaException("Já existe uma pergunta com a ordem " + pergunta.getOrdem() + ".");
        }
        perguntas.add(pergunta);
    }

    public void editarPergunta(int ordem, String enunciado, TipoPergunta tipo, boolean obrigatoria) {
        exigirPlanejada("editar perguntas");
        buscarPergunta(ordem).editar(enunciado, tipo, obrigatoria);
    }

    public void removerPergunta(int ordem) {
        exigirPlanejada("remover perguntas");
        perguntas.remove(buscarPergunta(ordem));
    }

    public void adicionarOpcao(int ordemPergunta, OpcaoResposta opcao) {
        exigirPlanejada("alterar opções de resposta");
        buscarPergunta(ordemPergunta).adicionarOpcao(opcao);
    }

    public void removerOpcao(int ordemPergunta, int ordemOpcao) {
        exigirPlanejada("alterar opções de resposta");
        buscarPergunta(ordemPergunta).removerOpcao(ordemOpcao);
    }

    public Pergunta buscarPergunta(int ordem) {
        return perguntas.stream()
                .filter(p -> p.getOrdem() == ordem)
                .findFirst()
                .orElseThrow(() -> new PesquisaInvalidaException("Pergunta " + ordem + " não encontrada."));
    }

    //Plano amostral
    public void definirPlanoAmostral(PlanoAmostral plano) {
        exigirPlanejada("definir o plano amostral");
        if (plano.getGrupos().isEmpty())
            throw new PesquisaInvalidaException("O plano amostral precisa ter ao menos um grupo.");
        if (plano.totalEntrevistas() > quantidadePrevista)
            throw new PesquisaInvalidaException("As metas dos grupos (" + plano.totalEntrevistas()
                    + ") ultrapassam a quantidade prevista (" + quantidadePrevista + ").");
        this.planoAmostral = plano;
    }

    public List<GrupoAmostral> gruposQueNaoAtingiramMeta(Map<Long, Integer> entrevistasValidasPorGrupo) {
        List<GrupoAmostral> resultado = new ArrayList<>();
        if (planoAmostral == null) return resultado;
        for (GrupoAmostral g : planoAmostral.getGrupos()) {
            int validas = entrevistasValidasPorGrupo.getOrDefault(g.getId(), 0);
            if (!g.atingiuMeta(validas)) resultado.add(g);
        }
        return resultado;
    }

    // ---------- Ciclo de vida ----------

    public void iniciarColeta() {
        exigirSituacao(SituacaoPesquisa.PLANEJADA,
                "Só é possível iniciar a coleta de uma pesquisa planejada.");
        if (perguntas.isEmpty())
            throw new PesquisaInvalidaException("Cadastre ao menos uma pergunta antes de iniciar a coleta.");
        for (Pergunta p : perguntas) {
            if (p.getOpcoes().size() < 2)
                throw new PesquisaInvalidaException("A pergunta " + p.getOrdem() + " precisa ter ao menos 2 opções de resposta.");
        }
        situacao = SituacaoPesquisa.EM_COLETA;
    }

    public void encerrarColeta() {
        exigirSituacao(SituacaoPesquisa.EM_COLETA,
                "Só é possível encerrar uma pesquisa que está em coleta.");
        situacao = SituacaoPesquisa.ENCERRADA;
    }

    public void processar() {
        exigirSituacao(SituacaoPesquisa.ENCERRADA,
                "A pesquisa só pode ser processada após o encerramento da coleta.");
        situacao = SituacaoPesquisa.PROCESSADA;
    }

    public void registrarEntrevista() {
        if (situacao == SituacaoPesquisa.ENCERRADA || situacao == SituacaoPesquisa.PROCESSADA)
            throw new PesquisaEncerradaException("Pesquisa encerrada não pode receber novas entrevistas.");
        if (situacao == SituacaoPesquisa.PLANEJADA)
            throw new PesquisaInvalidaException("Inicie a coleta antes de registrar entrevistas.");
        if (quantidadeRealizada >= quantidadePrevista)
            throw new PesquisaInvalidaException("Quantidade prevista de entrevistas já atingida.");
        quantidadeRealizada++;
    }

    public double percentualConclusao() {
        return 100.0 * quantidadeRealizada / quantidadePrevista;
    }

    //validacoes
    private void validarDados(Abrangencia abrangencia, TipoPesquisa tipo,
                              LocalDate inicio, LocalDate fim, int quantidadePrevista) {
        if (inicio == null || fim == null || fim.isBefore(inicio))
            throw new PesquisaInvalidaException("Período de coleta inválido.");
        if (abrangencia == null)
            throw new PesquisaInvalidaException("A abrangência é obrigatória.");
        if (tipo == null)
            throw new PesquisaInvalidaException("O tipo da pesquisa é obrigatório.");
        if (quantidadePrevista <= 0)
            throw new PesquisaInvalidaException("Quantidade prevista deve ser maior que zero.");
    }

    private void exigirAlteravel() {
        if (!podeSerAlterada())
            throw new PesquisaInvalidaException("Pesquisa processada não pode ser alterada.");
    }

    private void exigirPlanejada(String acao) {
        if (situacao != SituacaoPesquisa.PLANEJADA)
            throw new PesquisaInvalidaException("Só é possível " + acao + " em pesquisa planejada.");
    }

    private void exigirSituacao(SituacaoPesquisa esperada, String mensagem) {
        if (situacao != esperada)
            throw new PesquisaInvalidaException(mensagem);
    }

    //getters
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getCargo() { return cargo; }
    public LocalDate getInicioColeta() { return inicioColeta; }
    public LocalDate getFimColeta() { return fimColeta; }
    public Abrangencia getAbrangencia() { return abrangencia; }
    public TipoPesquisa getTipo() { return tipo; }
    public SituacaoPesquisa getSituacao() { return situacao; }
    public int getQuantidadePrevista() { return quantidadePrevista; }
    public int getQuantidadeRealizada() { return quantidadeRealizada; }
    public List<Pergunta> getPerguntas() { return Collections.unmodifiableList(perguntas); }
    public PlanoAmostral getPlanoAmostral() { return planoAmostral; }
}