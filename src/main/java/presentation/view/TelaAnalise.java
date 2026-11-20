package presentation.view;

import application.admin.AdminService;
import application.analise.RelatorioController;
import application.pesquisa.PesquisaService;
import domain.exceptions.AcessoNegadoException;
import domain.model.analise.ResultadoEstatistico;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import presentation.controller.AnaliseFxController;

import java.io.IOException;

public class TelaAnalise {

    private final Stage stage;
    private final AdminService adminService;
    private final PesquisaService pesquisaService;
    private final RelatorioController relatorioController;
    private final AnaliseFxController controller;

    public TelaAnalise(Stage stage,
                       AdminService adminService,
                       PesquisaService pesquisaService,
                       RelatorioController relatorioController) {
        this.stage = stage;
        this.adminService = adminService;
        this.pesquisaService = pesquisaService;
        this.relatorioController = relatorioController;
        this.controller = new AnaliseFxController(relatorioController);
    }

    public void mostrar() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F4F6;");

        HBox topo = new HBox();
        topo.setAlignment(Pos.CENTER_LEFT);
        Label lblTitulo = new Label("Análise Estatística e Relatórios");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E1E1E;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnVoltar = new Button("Voltar ao Menu");
        btnVoltar.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #CED4DA; -fx-border-radius: 4; -fx-padding: 6 14;");
        btnVoltar.setOnAction(e ->
                new TelaMenuPrincipal(stage, adminService, pesquisaService, relatorioController).mostrar()
        );
        topo.getChildren().addAll(lblTitulo, spacer, btnVoltar);

        // Card de Cálculo Estatístico
        VBox cardCalc = new VBox(10);
        cardCalc.setPadding(new Insets(14));
        cardCalc.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #D8DCE3; -fx-border-radius: 6; -fx-background-radius: 6;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);

        TextField txtCategoria = new TextField();
        txtCategoria.setPromptText("Ex.: Candidato A / Região Norte");
        TextField txtQtd = new TextField();
        txtQtd.setPromptText("Ex.: 450");
        TextField txtTotal = new TextField();
        txtTotal.setPromptText("Ex.: 1000");

        grid.add(new Label("Categoria / Opção"), 0, 0);
        grid.add(txtCategoria, 0, 1);
        grid.add(new Label("Qtd. Respostas Válidas"), 1, 0);
        grid.add(txtQtd, 1, 1);
        grid.add(new Label("Total Geral Válido"), 2, 0);
        grid.add(txtTotal, 2, 1);

        TextArea txtSaida = new TextArea();
        txtSaida.setEditable(false);
        txtSaida.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 13px;");
        VBox.setVgrow(txtSaida, Priority.ALWAYS);

        Button btnCalcular = new Button("Calcular Resultado");
        btnCalcular.setStyle("-fx-background-color: #B3D9FF; -fx-border-color: #8EC5FC; -fx-border-radius: 4; -fx-padding: 6 16;");
        btnCalcular.setOnAction(e -> {
            try {
                ResultadoEstatistico res = controller.calcularResultado(
                        Integer.parseInt(txtQtd.getText().trim()),
                        Integer.parseInt(txtTotal.getText().trim()),
                        txtCategoria.getText().trim()
                );
                txtSaida.appendText("Resultado adicionado: " + res + "\n");
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Informe números inteiros válidos.");
            } catch (AcessoNegadoException | IllegalArgumentException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        TextField txtIdRel = new TextField();
        txtIdRel.setPromptText("ID (ex: 1)");
        TextField txtTituloRel = new TextField();
        txtTituloRel.setPromptText("Título do Relatório");
        TextField txtCaminhoCsv = new TextField("dados/relatorio.csv");

        Button btnGerar = new Button("Gerar e Visualizar Tabela");
        btnGerar.setStyle("-fx-background-color: #B3D9FF; -fx-border-color: #8EC5FC; -fx-border-radius: 4; -fx-padding: 6 16;");
        btnGerar.setOnAction(e -> {
            try {
                controller.gerarRelatorio(Integer.parseInt(txtIdRel.getText().trim()), txtTituloRel.getText().trim());
                txtSaida.setText(controller.visualizarTabela());
            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        Button btnExportar = new Button("Exportar CSV");
        btnExportar.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #CED4DA; -fx-border-radius: 4; -fx-padding: 6 14;");
        btnExportar.setOnAction(e -> {
            try {
                controller.exportarCSV(txtCaminhoCsv.getText().trim());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Exportado", "Arquivo salvo em: " + txtCaminhoCsv.getText());
            } catch (IOException | RuntimeException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Exportar", ex.getMessage());
            }
        });

        HBox linhaRel = new HBox(10, txtIdRel, txtTituloRel, btnGerar, txtCaminhoCsv, btnExportar);
        linhaRel.setAlignment(Pos.CENTER_LEFT);

        cardCalc.getChildren().addAll(grid, btnCalcular, linhaRel);
        root.getChildren().addAll(topo, cardCalc, txtSaida);

        stage.setTitle("Análise Estatística - Pesquisas Eleitorais");
        stage.setScene(new Scene(root, 960, 620));
        stage.centerOnScreen();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}