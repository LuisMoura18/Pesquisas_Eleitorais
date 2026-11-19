package domain.exceptions;

public class UsuarioInvalidoException extends RegraDeNegocioException {
    public UsuarioInvalidoException(String mensagem) {
        super(mensagem);
    }
}
