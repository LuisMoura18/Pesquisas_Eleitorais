package domain.exceptions;

public class AcessoNegadoException extends RegraDeNegocioException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}