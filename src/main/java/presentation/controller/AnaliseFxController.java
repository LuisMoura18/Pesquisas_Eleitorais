package presentation.controller;

import application.analise.RelatorioController;
import domain.model.analise.Relatorio;
import domain.model.analise.ResultadoEstatistico;

import java.io.IOException;

public class AnaliseFxController {

    private final RelatorioController relatorioController;

    public AnaliseFxController(RelatorioController relatorioController) {
        this.relatorioController = relatorioController;
    }

    public ResultadoEstatistico calcularResultado(int quantidade, int totalValidos, String categoria) {
        return relatorioController.calcularResultado(quantidade, totalValidos, categoria);
    }

    public Relatorio gerarRelatorio(int id, String titulo) {
        return relatorioController.gerarRelatorio(id, titulo);
    }

    public String visualizarTabela() {
        return relatorioController.visualizarTabela();
    }

    public void exportarCSV(String caminho) throws IOException {
        relatorioController.exportarCSV(caminho);
    }
}