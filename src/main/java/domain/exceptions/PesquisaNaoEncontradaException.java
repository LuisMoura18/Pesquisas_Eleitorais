package domain.exceptions;

public class PesquisaNaoEncontradaException extends RegraDeNegocioException {
    public PesquisaNaoEncontradaException(String codigo) {
        super("Pesquisa não encontrada: " + codigo);
    }
}