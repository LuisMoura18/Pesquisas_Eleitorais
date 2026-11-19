package domain.exceptions;

public class PesquisaInvalidaException extends RegraDeNegocioException{
    public PesquisaInvalidaException(String m){
        super(m);
    }
}