package presentation.view;

import application.pesquisa.AndamentoPesquisa;
import domain.exceptions.PersistenciaException;
import domain.exceptions.RegraDeNegocioException;
import domain.model.pesquisa.Abrangencia;
import domain.model.pesquisa.Pesquisa;
import domain.model.pesquisa.SituacaoPesquisa;
import domain.model.pesquisa.TipoPesquisa;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import presentation.controller.PesquisaFxController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TelaConsultaPesquisa {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final PesquisaFxController controller;
    private final ObservableList<Pesquisa> dadosTabela = FXCollections.observableArrayList();
    private final TableView<Pesquisa> tabela = new TableView<>(dadosTabela);
    private final DatePicker dpInicio = new DatePicker();
    private final DatePicker dpFim = new DatePicker();
    private final TextField txtCargo = new TextField();
    private final ComboBox<TipoPesquisa> cbTipo = new ComboBox<>();
    private final ComboBox<SituacaoPesquisa> cbSituacao = new ComboBox<>();
    private final ComboBox<Abrangencia> cbAbrangencia = new ComboBox<>();
    private final TextField txtQtdMinima = new TextField();
    private final Label lblResumo = new Label("0 pesquisa(s) exibida(s)");
    private Stage stagePrincipal;

    public TelaConsultaPesquisa(PesquisaFxController controller) {
        this.controller = controller;
    }

    public void exibir(Stage stage) {
        this.stagePrincipal = stage;
        stage.setTitle("Módulo de Pesquisas Eleitorais");

        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(16));

        VBox topo = new VBox(12);
        Label titulo = new Label("Gerenciamento e Consulta de Pesquisas");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        topo.getChildren().addAll(titulo, montarFiltros(), montarBarraFiltros(), new Separator(), montarBarraCrud());

        configurarTabela();

        HBox rodape = new HBox(12, lblResumo);
        rodape.setAlignment(Pos.CENTER_LEFT);
        rodape.setPadding(new Insets(10, 0, 0, 0));

        raiz.setTop(topo);
        raiz.setCenter(tabela);
        raiz.setBottom(rodape);
        BorderPane.setMargin(tabela, new Insets(12, 0, 0, 0));

        carregarTodas();

        stage.setScene(new Scene(raiz, 1120, 680));
        stage.show();
    }

    private GridPane montarFiltros() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.setStyle("-fx-background-color: #f4f6f8; -fx-background-radius: 8;");

        for (int i = 0; i < 4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            grid.getColumnConstraints().add(col);
        }

        dpInicio.setPromptText("Data início");
        dpInicio.setMaxWidth(Double.MAX_VALUE);

        dpFim.setPromptText("Data fim");
        dpFim.setMaxWidth(Double.MAX_VALUE);

        txtCargo.setPromptText("Ex.: Presidente, Governador...");
        txtQtdMinima.setPromptText("Ex.: 500");

        configurarComboComOpcaoTodos(cbTipo, TipoPesquisa.values(), "Todos os tipos");
        configurarComboComOpcaoTodos(cbSituacao, SituacaoPesquisa.values(), "Todas as situações");
        configurarComboComOpcaoTodos(cbAbrangencia, Abrangencia.values(), "Todas as abrangências");

        grid.add(criarCampo("Período (De)", dpInicio), 0, 0);
        grid.add(criarCampo("Período (Até)", dpFim), 1, 0);
        grid.add(criarCampo("Cargo", txtCargo), 2, 0);
        grid.add(criarCampo("Qtd. Mínima Prevista", txtQtdMinima), 3, 0);

        grid.add(criarCampo("Tipo de Pesquisa", cbTipo), 0, 1);
        grid.add(criarCampo("Situação", cbSituacao), 1, 1);
        grid.add(criarCampo("Abrangência", cbAbrangencia), 2, 1);

        return grid;
    }

    private <T extends Enum<T>> void configurarComboComOpcaoTodos(ComboBox<T> combo, T[] valores, String rotuloNulo) {
        combo.getItems().clear();
        combo.getItems().add(null);
        combo.getItems().addAll(valores);
        combo.setValue(null);
        combo.setMaxWidth(Double.MAX_VALUE);

        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? rotuloNulo : item.name());
            }
        });

        combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? rotuloNulo : item.name());
            }
        });
    }

    private VBox criarCampo(String rotulo, Node controle) {
        Label lbl = new Label(rotulo);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        return new VBox(4, lbl, controle);
    }

    private HBox montarBarraFiltros() {
        Button btnLimpar = new Button("Limpar Filtros");
        btnLimpar.setOnAction(e -> limparFiltros());

        Button btnPesquisar = new Button("Pesquisar");
        btnPesquisar.setDefaultButton(true);
        btnPesquisar.setOnAction(e -> executarPesquisa());

        HBox box = new HBox(10, btnLimpar, btnPesquisar);
        box.setAlignment(Pos.CENTER_RIGHT);
        return box;
    }

    private HBox montarBarraCrud() {
        Button btnNova = new Button("+ Nova Pesquisa");
        btnNova.setOnAction(e -> new TelaFormPesquisa(controller, null, this::carregarTodas).exibir(stagePrincipal));

        Button btnEditar = new Button("Editar");
        btnEditar.setOnAction(e -> {
            Pesquisa sel = exigirSelecionada();
            if (sel != null) new TelaFormPesquisa(controller, sel, this::carregarTodas).exibir(stagePrincipal);
        });

        Button btnConfigurar = new Button("Configurar (Perguntas / Candidatos / Amostra)");
        btnConfigurar.setOnAction(e -> {
            Pesquisa sel = exigirSelecionada();
            if (sel != null) new TelaConfiguracaoPesquisa(controller, sel.getCodigo(), this::carregarTodas).exibir(stagePrincipal);
        });

        Button btnIniciarColeta = new Button("Iniciar Coleta");
        btnIniciarColeta.setOnAction(e -> executarAcaoCiclo("iniciar coleta", controller::iniciarColeta));

        Button btnEncerrarColeta = new Button("Encerrar Coleta");
        btnEncerrarColeta.setOnAction(e -> executarAcaoCiclo("encerrar coleta", controller::encerrarColeta));

        Button btnProcessar = new Button("Processar");
        btnProcessar.setOnAction(e -> executarAcaoCiclo("processar", controller::processar));

        Button btnAndamento = new Button("Andamento");
        btnAndamento.setOnAction(e -> {
            Pesquisa sel = exigirSelecionada();
            if (sel == null) return;
            try {
                AndamentoPesquisa a = controller.acompanhar(sel.getCodigo());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Andamento — " + sel.getCodigo(),
                        String.format("Pesquisa: %s%nPrevistas: %d%nRealizadas: %d%nConclusão: %.1f%%",
                                sel.getNome(), a.quantidadePrevista(), a.quantidadeRealizada(), a.percentualConclusao()));
            } catch (RegraDeNegocioException | PersistenciaException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", ex.getMessage());
            }
        });

        Button btnExcluir = new Button("Excluir");
        btnExcluir.setStyle("-fx-text-fill: #b91c1c;");
        btnExcluir.setOnAction(e -> {
            Pesquisa sel = exigirSelecionada();
            if (sel == null) return;

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Excluir pesquisa " + sel.getCodigo() + "?");
            confirmacao.setContentText("Esta ação não poderá ser desfeita.");
            Optional<ButtonType> resposta = confirmacao.showAndWait();

            if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
                try {
                    controller.excluir(sel.getCodigo());
                    carregarTodas();
                } catch (RegraDeNegocioException | PersistenciaException ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Não foi possível excluir", ex.getMessage());
                }
            }
        });

        HBox box = new HBox(8, btnNova, btnEditar, btnConfigurar,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                btnIniciarColeta, btnEncerrarColeta, btnProcessar, btnAndamento,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                btnExcluir);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void executarAcaoCiclo(String acao, java.util.function.Consumer<String> operacao) {
        Pesquisa sel = exigirSelecionada();
        if (sel == null) return;
        try {
            operacao.accept(sel.getCodigo());
            carregarTodas();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso",
                    "Operação (" + acao + ") realizada para " + sel.getCodigo() + ".");
        } catch (RegraDeNegocioException | PersistenciaException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao " + acao, ex.getMessage());
        }
    }

    private Pesquisa exigirSelecionada() {
        Pesquisa sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhuma pesquisa selecionada",
                    "Selecione uma pesquisa na tabela primeiro.");
        }
        return sel;
    }

    @SuppressWarnings("unchecked")
    private void configurarTabela() {
        tabela.setPlaceholder(new Label("Nenhuma pesquisa encontrada."));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        TableColumn<Pesquisa, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));

        TableColumn<Pesquisa, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));

        TableColumn<Pesquisa, String> colCargo = new TableColumn<>("Cargo");
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCargo()));

        TableColumn<Pesquisa, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTipo() != null ? c.getValue().getTipo().name() : ""));

        TableColumn<Pesquisa, String> colSituacao = new TableColumn<>("Situação");
        colSituacao.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSituacao() != null ? c.getValue().getSituacao().name() : ""));

        TableColumn<Pesquisa, String> colAbrangencia = new TableColumn<>("Abrangência");
        colAbrangencia.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getAbrangencia() != null ? c.getValue().getAbrangencia().name() : ""));

        TableColumn<Pesquisa, String> colInicio = new TableColumn<>("Início");
        colInicio.setCellValueFactory(c -> new SimpleStringProperty(formatarData(c.getValue().getInicioColeta())));

        TableColumn<Pesquisa, String> colFim = new TableColumn<>("Fim");
        colFim.setCellValueFactory(c -> new SimpleStringProperty(formatarData(c.getValue().getFimColeta())));

        TableColumn<Pesquisa, String> colEntrevistas = new TableColumn<>("Realizadas / Previstas");
        colEntrevistas.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getQuantidadeRealizada() + " / " + c.getValue().getQuantidadePrevista()));

        TableColumn<Pesquisa, String> colConclusao = new TableColumn<>("Conclusão");
        colConclusao.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.1f%%", c.getValue().percentualConclusao())));

        tabela.getColumns().addAll(
                colCodigo, colNome, colCargo, colTipo, colSituacao,
                colAbrangencia, colInicio, colFim, colEntrevistas, colConclusao
        );
    }

    private void carregarTodas() {
        try {
            atualizarTabela(controller.listarTodas());
        } catch (RegraDeNegocioException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao carregar pesquisas", e.getMessage());
        }
    }

    private void executarPesquisa() {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fim = dpFim.getValue();

        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Período inválido",
                    "A data final não pode ser anterior à data inicial.");
            return;
        }

        Integer qtdMinima = null;
        String textoQtd = txtQtdMinima.getText().trim();
        if (!textoQtd.isEmpty()) {
            try {
                qtdMinima = Integer.parseInt(textoQtd);
                if (qtdMinima < 0) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                            "A quantidade mínima prevista deve ser maior ou igual a zero.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido",
                        "Digite um número inteiro válido para a quantidade mínima prevista.");
                return;
            }
        }

        try {
            List<Pesquisa> resultados = controller.filtrar(
                    inicio, fim, txtCargo.getText(),
                    cbTipo.getValue(), cbSituacao.getValue(), cbAbrangencia.getValue(),
                    qtdMinima
            );
            atualizarTabela(resultados);
        } catch (RegraDeNegocioException | PersistenciaException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao consultar pesquisas", e.getMessage());
        }
    }

    private void limparFiltros() {
        dpInicio.setValue(null);
        dpFim.setValue(null);
        txtCargo.clear();
        cbTipo.setValue(null);
        cbSituacao.setValue(null);
        cbAbrangencia.setValue(null);
        txtQtdMinima.clear();
        carregarTodas();
    }

    private void atualizarTabela(List<Pesquisa> pesquisas) {
        dadosTabela.setAll(pesquisas);
        lblResumo.setText(pesquisas.size() + " pesquisa(s) exibida(s)");
    }

    private String formatarData(LocalDate data) {
        return data != null ? FORMATO_DATA.format(data) : "";
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}