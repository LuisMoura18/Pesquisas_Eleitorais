package presentation.view;

import domain.exceptions.PersistenciaException;
import domain.exceptions.RegraDeNegocioException;
import domain.model.pesquisa.Abrangencia;
import domain.model.pesquisa.Pesquisa;
import domain.model.pesquisa.TipoPesquisa;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import presentation.controller.PesquisaFxController;

import java.time.LocalDate;

public class TelaFormPesquisa {
    private final PesquisaFxController controller;
    private final Pesquisa pesquisaEdicao;
    private final Runnable aoSalvar;

    public TelaFormPesquisa(PesquisaFxController controller, Pesquisa pesquisaEdicao, Runnable aoSalvar) {
        this.controller = controller;
        this.pesquisaEdicao = pesquisaEdicao;
        this.aoSalvar = aoSalvar;
    }
    //chamada que abre a tela pro usuario
    public void exibir(Window owner) {
        boolean modoEdicao = (pesquisaEdicao != null);

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle(modoEdicao ? "Editar Pesquisa - " + pesquisaEdicao.getCodigo() : "Nova Pesquisa Eleitoral");

        VBox raiz = new VBox(14);
        raiz.setPadding(new Insets(18));

        Label titulo = new Label(modoEdicao ? "Editar Pesquisa" : "Cadastrar Nova Pesquisa");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Ex.: PESQ-2026-01");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Ex.: Intenção de Voto - 1º Turno");

        TextField txtCargo = new TextField();
        txtCargo.setPromptText("Ex.: Presidente, Governador, Senador...");

        DatePicker dpInicio = new DatePicker();
        dpInicio.setMaxWidth(Double.MAX_VALUE);

        DatePicker dpFim = new DatePicker();
        dpFim.setMaxWidth(Double.MAX_VALUE);

        ComboBox<Abrangencia> cbAbrangencia = new ComboBox<>();
        cbAbrangencia.getItems().setAll(Abrangencia.values());
        cbAbrangencia.setMaxWidth(Double.MAX_VALUE);

        ComboBox<TipoPesquisa> cbTipo = new ComboBox<>();
        cbTipo.getItems().setAll(TipoPesquisa.values());
        cbTipo.setMaxWidth(Double.MAX_VALUE);

        TextField txtQtdPrevista = new TextField();
        txtQtdPrevista.setPromptText("Ex.: 1000");

        if (modoEdicao) {
            txtCodigo.setText(pesquisaEdicao.getCodigo());
            txtCodigo.setDisable(true);
            txtNome.setText(pesquisaEdicao.getNome());
            txtCargo.setText(pesquisaEdicao.getCargo());
            dpInicio.setValue(pesquisaEdicao.getInicioColeta());
            dpFim.setValue(pesquisaEdicao.getFimColeta());
            cbAbrangencia.setValue(pesquisaEdicao.getAbrangencia());
            cbTipo.setValue(pesquisaEdicao.getTipo());
            txtQtdPrevista.setText(String.valueOf(pesquisaEdicao.getQuantidadePrevista()));
        } else {
            cbAbrangencia.setValue(Abrangencia.NACIONAL);
            cbTipo.setValue(TipoPesquisa.INTENCAO_VOTO);
        }

        GridPane.setHgrow(txtNome, Priority.ALWAYS);

        grid.add(new Label("Código:"), 0, 0);
        grid.add(txtCodigo, 1, 0);
        grid.add(new Label("Nome:"), 0, 1);
        grid.add(txtNome, 1, 1);
        grid.add(new Label("Cargo:"), 0, 2);
        grid.add(txtCargo, 1, 2);
        grid.add(new Label("Início da Coleta:"), 0, 3);
        grid.add(dpInicio, 1, 3);
        grid.add(new Label("Fim da Coleta:"), 0, 4);
        grid.add(dpFim, 1, 4);
        grid.add(new Label("Abrangência:"), 0, 5);
        grid.add(cbAbrangencia, 1, 5);
        grid.add(new Label("Tipo de Pesquisa:"), 0, 6);
        grid.add(cbTipo, 1, 6);
        grid.add(new Label("Qtd. Prevista:"), 0, 7);
        grid.add(txtQtdPrevista, 1, 7);

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> dialog.close());

        Button btnSalvar = new Button("Salvar");
        btnSalvar.setDefaultButton(true);
        btnSalvar.setOnAction(e -> {
            try {
                String codigo = txtCodigo.getText().trim();
                String nome = txtNome.getText().trim();
                String cargo = txtCargo.getText().trim();
                LocalDate inicio = dpInicio.getValue();
                LocalDate fim = dpFim.getValue();
                Abrangencia abrangencia = cbAbrangencia.getValue();
                TipoPesquisa tipo = cbTipo.getValue();

                if (nome.isEmpty() || cargo.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos obrigatórios", "Preencha nome e cargo.");
                    return;
                }

                int qtdPrevista;
                try {
                    qtdPrevista = Integer.parseInt(txtQtdPrevista.getText().trim());
                } catch (NumberFormatException ex) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                            "A quantidade prevista deve ser um número inteiro.");
                    return;
                }

                if (modoEdicao) {
                    controller.editar(codigo, nome, cargo, inicio, fim, abrangencia, tipo, qtdPrevista);
                } else {
                    controller.cadastrar(codigo, nome, cargo, inicio, fim, abrangencia, tipo, qtdPrevista);
                }

                if (aoSalvar != null) aoSalvar.run();
                dialog.close();
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro ao salvar pesquisa", ex.getMessage());
            }
        });

        HBox botoes = new HBox(10, btnCancelar, btnSalvar);
        botoes.setAlignment(Pos.CENTER_RIGHT);

        raiz.getChildren().addAll(titulo, grid, botoes);

        dialog.setScene(new Scene(raiz, 480, 410));
        dialog.showAndWait();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}