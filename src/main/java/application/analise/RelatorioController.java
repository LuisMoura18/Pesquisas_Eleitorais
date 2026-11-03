
package application.analise;

import domain.model.analise.AnaliseEstatistica;
import domain.model.analise.FiltroRelatorio;
import domain.model.analise.Relatorio;
import domain.model.analise.ResultadoEstatistico;

import java.io.IOException;
import java.util.List;

public class RelatorioController {

    private final AnaliseEstatistica analise;
    private FiltroRelatorio filtro;
    private Relatorio relatorio;

    public RelatorioController() {
        this.analise = new AnaliseEstatistica();
        this.filtro = new FiltroRelatorio();
    }

    public ResultadoEstatistico calcularResultado(
            int quantidade, int totalValidos, String categoria) {
        return analise.calcularResultado(
                quantidade, totalValidos, categoria);
    }

    public void aplicarFiltros(FiltroRelatorio filtro) {
        if (filtro == null || !filtro.validarFiltros()) {
            throw new IllegalArgumentException("Filtros invalidos.");
        }
        this.filtro = filtro;
    }

    public Relatorio gerarRelatorio(int id, String titulo) {
        relatorio = new Relatorio(id, titulo);
        List<ResultadoEstatistico> resultados =
                analise.obterResultados();
        relatorio.gerarRelatorio(resultados);
        return relatorio;
    }

    public String visualizarTabela() {
        if (relatorio == null) {
            throw new IllegalStateException(
                    "Nenhum relatorio foi gerado.");
        }
        return relatorio.gerarTabela();
    }

    public void exportarCSV(String caminho) throws IOException {
        if (relatorio == null) {
            throw new IllegalStateException(
                    "Nenhum relatorio foi gerado.");
        }
        relatorio.exportarCSV(caminho);
    }
}
