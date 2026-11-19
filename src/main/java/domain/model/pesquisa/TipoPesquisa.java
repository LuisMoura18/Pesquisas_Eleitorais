package domain.model.pesquisa;

public enum TipoPesquisa {
    INTENCAO_VOTO(new RegraIntencaoVoto()),
    REJEICAO(new RegraPadrao()),
    CONHECIMENTO(new RegraPadrao()),
    SEGUNDO_TURNO(new RegraSegundoTurno()),
    OUTRO(new RegraPadrao());

    private final RegraTipoPesquisa regra;

    TipoPesquisa(RegraTipoPesquisa regra) {
        this.regra = regra;
    }

    public RegraTipoPesquisa regra() {
        return regra;
    }
}