
package domain.model.analise;

import java.util.ArrayList;
import java.util.List;

public class AnaliseEstatistica {

    private List<ResultadoEstatistico> resultados;

    public AnaliseEstatistica() {
        this.resultados = new ArrayList<>();
    }

    public ResultadoEstatistico calcularResultado(
            int quantidade, int totalValidos, String categoria) {

        if (totalValidos <= 0 || quantidade < 0
                || quantidade > totalValidos) {
            throw new IllegalArgumentException(
                    "Quantidades invalidas para o calculo.");
        }

        double percentual = (quantidade * 100.0) / totalValidos;

        ResultadoEstatistico resultado =
                new ResultadoEstatistico(quantidade, percentual, categoria);

        resultados.add(resultado);
        return resultado;
    }

    public List<ResultadoEstatistico> obterResultados() {
        return new ArrayList<>(resultados);
    }
}
