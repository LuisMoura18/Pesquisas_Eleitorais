package domain.model.pesquisa;

import domain.exceptions.PesquisaInvalidaException;

public class RegraSegundoTurno implements RegraTipoPesquisa {
    @Override
    public void validarInicioColeta(Pesquisa pesquisa) {
        if (pesquisa.getCandidatos().size() != 2)
            throw new PesquisaInvalidaException(
                    "Pesquisa de segundo turno precisa ter exatamente dois candidatos associados.");
    }
}