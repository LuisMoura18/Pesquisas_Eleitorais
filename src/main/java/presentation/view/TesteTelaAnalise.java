package presentation.view;

import application.analise.RelatorioController;
import javafx.application.Application;
import javafx.stage.Stage;

public class TesteTelaAnalise extends Application {

    @Override
    public void start(Stage stage) {
        RelatorioController controller = new RelatorioController();
        TelaAnaliseRelatorios tela = new TelaAnaliseRelatorios(controller);
        tela.exibir(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}