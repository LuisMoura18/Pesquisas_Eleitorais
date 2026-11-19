package domain.model.pesquisa;

import domain.exceptions.PesquisaInvalidaException;

public class RegraIntencaoVoto implements RegraTipoPesquisa {
    @Override
    public void validarInicioColeta(Pesquisa pesquisa) {
        if (pesquisa.getCandidatos().isEmpty())
            throw new PesquisaInvalidaException(
                    "Pesquisa de intenção de voto precisa ter ao menos um candidato associado.");
    }
}