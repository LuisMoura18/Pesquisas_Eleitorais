package domain.model.pesquisa;

import domain.exceptions.PesquisaInvalidaException;
import java.io.Serializable;

public class GrupoAmostral implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final String descricao;
    private final int quantidadeMinima;

    public GrupoAmostral(long id, String descricao, int quantidadeMinima) {
        if (descricao == null || descricao.isBlank())
            throw new PesquisaInvalidaException("A descrição do grupo amostral é obrigatória.");
        if (quantidadeMinima <= 0)
            throw new PesquisaInvalidaException("A quantidade mínima do grupo deve ser maior que zero.");
        this.id = id;
        this.descricao = descricao;
        this.quantidadeMinima = quantidadeMinima;
    }

    public boolean atingiuMeta(int entrevistasValidas) {
        return entrevistasValidas >= quantidadeMinima;
    }

    public long getId() { return id; }
    public String getDescricao() { return descricao; }
    public int getQuantidadeMinima() { return quantidadeMinima; }

    @Override
    public String toString() { return descricao + " (mínimo " + quantidadeMinima + ")"; }
}