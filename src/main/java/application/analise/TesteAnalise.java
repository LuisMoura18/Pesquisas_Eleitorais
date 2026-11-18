
package application.analise;

import domain.model.analise.AnaliseEstatistica;
import domain.model.analise.ResultadoEstatistico;
import domain.model.analise.Relatorio;
import application.analise.RelatorioController;
import domain.model.analise.FiltroRelatorio;

public class TesteAnalise {

    public static void main(String[] args) {

        AnaliseEstatistica analise = new AnaliseEstatistica();

        ResultadoEstatistico a =
                analise.calcularResultado(60, 100, "Candidato A");

        ResultadoEstatistico b =
                analise.calcularResultado(30, 100, "Candidato B");

        ResultadoEstatistico c =
                analise.calcularResultado(10, 100, "Candidato C");

        System.out.println("RESULTADOS DA PESQUISA");

        for (ResultadoEstatistico resultado :
                analise.obterResultados()) {

            System.out.println(
                    resultado.getCategoria() + ": " +
                            resultado.obterQuantidadeAbsoluta() +
                            " votos (" +
                            resultado.obterPercentual() + "%)"
            );
        }
        RelatorioController controller = new RelatorioController();

        controller.calcularResultado(60, 100, "Candidato A");
        controller.calcularResultado(30, 100, "Candidato B");
        controller.calcularResultado(10, 100, "Candidato C");

        FiltroRelatorio filtro = new FiltroRelatorio();
        filtro.definirCandidato("Candidato A");

        controller.aplicarFiltros(filtro);
        controller.gerarRelatorio(1, "Teste com filtro");

        System.out.println(controller.visualizarTabela());

        // Teste de geracao de relatorio e exportacao CSV
        Relatorio relatorio = new Relatorio(1, "Pesquisa Eleitoral");
        relatorio.gerarRelatorio(analise.obterResultados());

        System.out.println("\nRELATORIO GERADO");
        System.out.println(relatorio.gerarTabela());

        try {
            relatorio.exportarCSV("resultado_teste.csv");
            System.out.println("CSV exportado com sucesso!");
        } catch (java.io.IOException e) {
            System.out.println("Erro ao exportar CSV: " + e.getMessage());
        }

        // Teste de quantidade invalida
        try {
            analise.calcularResultado(120, 100, "Invalido");
            System.out.println("ERRO: validacao falhou!");
        } catch (IllegalArgumentException e) {
            System.out.println("Teste de validacao: OK");
        }
    }
}
