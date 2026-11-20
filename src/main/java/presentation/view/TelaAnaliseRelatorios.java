
package presentation.view;

import application.analise.RelatorioController;
import domain.model.analise.ResultadoEstatistico;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class TelaAnaliseRelatorios {

    private final RelatorioController controller;

    public TelaAnaliseRelatorios(RelatorioController controller) {
        this.controller = controller;
    }

    public void exibir(Stage stage) {
        stage.setTitle("Análise Estatística e Relatórios");

        Label titulo = new Label("Análise Estatística e Relatórios");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField txtCategoria = new TextField();
        txtCategoria.setPromptText("Candidato / Categoria");

        TextField txtQuantidade = new TextField();
        txtQuantidade.setPromptText("Quantidade de votos");

        TextField txtTotal = new TextField();
        txtTotal.setPromptText("Total de votos válidos");

        TextArea areaResultados = new TextArea();
        areaResultados.setEditable(false);
        areaResultados.setPrefHeight(300);

        Button btnCalcular = new Button("Calcular Resultado");
        Button btnGerar = new Button("Gerar Relatório");
        Button btnExportar = new Button("Exportar CSV");

        btnCalcular.setOnAction(e -> {
            try {
                String categoria = txtCategoria.getText().trim();
                if (categoria.isEmpty()) {
                    throw new IllegalArgumentException("Informe a categoria.");
                }

                int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
                int total = Integer.parseInt(txtTotal.getText().trim());

                ResultadoEstatistico resultado =
                        controller.calcularResultado(quantidade, total, categoria);

                areaResultados.appendText(
                        resultado.getCategoria() + ": " +
                                resultado.obterQuantidadeAbsoluta() + " votos (" +
                                resultado.obterPercentual() + "%)\n"
                );

            } catch (IllegalArgumentException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        btnGerar.setOnAction(e -> {
            try {
                controller.gerarRelatorio(1, "Relatório Estatístico");
                areaResultados.setText(controller.visualizarTabela());
            } catch (RuntimeException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        btnExportar.setOnAction(e -> {
            FileChooser seletor = new FileChooser();
            seletor.setTitle("Salvar relatório CSV");
            seletor.setInitialFileName("relatorio.csv");
            seletor.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv")
            );

            File arquivo = seletor.showSaveDialog(stage);
            if (arquivo == null) return;

            try {
                controller.exportarCSV(arquivo.getAbsolutePath());
                new Alert(Alert.AlertType.INFORMATION,
                        "Relatório exportado com sucesso!").showAndWait();
            } catch (IOException | RuntimeException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        HBox campos = new HBox(10, txtCategoria, txtQuantidade, txtTotal);
        HBox botoes = new HBox(10, btnCalcular, btnGerar, btnExportar);

        VBox raiz = new VBox(15, titulo, campos, botoes, areaResultados);
        raiz.setPadding(new Insets(20));

        stage.setScene(new Scene(raiz, 850, 500));
        stage.show();
    }

    private void mostrarErro(String mensagem) {
        new Alert(Alert.AlertType.ERROR, mensagem).showAndWait();
    }
}
