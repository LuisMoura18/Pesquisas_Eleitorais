package domain.model.pesquisa;

import domain.exceptions.PesquisaInvalidaException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pergunta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String enunciado;
    private final int ordem;
    private TipoPergunta tipo;
    private boolean obrigatoria;
    private final List<OpcaoResposta> opcoes = new ArrayList<>();

    public Pergunta(String enunciado, int ordem, TipoPergunta tipo, boolean obrigatoria) {
        if (ordem <= 0)
            throw new PesquisaInvalidaException("A ordem da pergunta deve ser maior que zero.");
        validarEnunciado(enunciado);
        if (tipo == null)
            throw new PesquisaInvalidaException("O tipo da pergunta é obrigatório.");
        this.enunciado = enunciado;
        this.ordem = ordem;
        this.tipo = tipo;
        this.obrigatoria = obrigatoria;
    }

    public void editar(String enunciado, TipoPergunta tipo, boolean obrigatoria) {
        validarEnunciado(enunciado);
        if (tipo == null)
            throw new PesquisaInvalidaException("O tipo da pergunta é obrigatório.");
        this.enunciado = enunciado;
        this.tipo = tipo;
        this.obrigatoria = obrigatoria;
    }

    public void adicionarOpcao(OpcaoResposta nova) {
        for (OpcaoResposta o : opcoes) {
            if (o.getOrdem() == nova.getOrdem())
                throw new PesquisaInvalidaException("Já existe uma opção com a ordem " + nova.getOrdem() + ".");
            if (o.getTexto().equalsIgnoreCase(nova.getTexto()))
                throw new PesquisaInvalidaException("Já existe a opção \"" + nova.getTexto() + "\" nesta pergunta.");
        }
        opcoes.add(nova);
    }

    public void removerOpcao(int ordemOpcao) {
        boolean removida = opcoes.removeIf(o -> o.getOrdem() == ordemOpcao);
        if (!removida)
            throw new PesquisaInvalidaException("Opção " + ordemOpcao + " não encontrada.");
    }

    private void validarEnunciado(String enunciado) {
        if (enunciado == null || enunciado.isBlank())
            throw new PesquisaInvalidaException("O texto da pergunta é obrigatório.");
    }

    public String getEnunciado() { return enunciado; }
    public int getOrdem() { return ordem; }
    public TipoPergunta getTipo() { return tipo; }
    public boolean isObrigatoria() { return obrigatoria; }
    public List<OpcaoResposta> getOpcoes() { return Collections.unmodifiableList(opcoes); }

    @Override
    public String toString() { return ordem + ". " + enunciado + (obrigatoria ? " (obrigatória)" : ""); }
}