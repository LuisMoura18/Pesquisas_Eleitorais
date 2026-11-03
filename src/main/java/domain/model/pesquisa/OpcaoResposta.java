package domain.model.pesquisa;

import domain.exceptions.PesquisaInvalidaException;
import java.io.Serializable;

public class OpcaoResposta implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String texto;
    private final int ordem;

    public OpcaoResposta(String texto, int ordem) {
        if (texto == null || texto.isBlank())
            throw new PesquisaInvalidaException("O texto da opção de resposta é obrigatório.");
        if (ordem <= 0)
            throw new PesquisaInvalidaException("A ordem da opção deve ser maior que zero.");
        this.texto = texto;
        this.ordem = ordem;
    }

    public String getTexto() { return texto; }
    public int getOrdem() { return ordem; }

    @Override
    public String toString() { return ordem + ") " + texto; }
}