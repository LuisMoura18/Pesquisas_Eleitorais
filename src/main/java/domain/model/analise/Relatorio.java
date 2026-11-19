
package domain.model.analise;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class Relatorio {

    private int id;
    private String titulo;
    private LocalDate dataGeracao;
    private List<ResultadoEstatistico> resultados;

    public Relatorio(int id, String titulo) {
        this.id = id;
        this.titulo = titulo;
        this.dataGeracao = LocalDate.now();
        this.resultados = new ArrayList<>();
    }

    public void gerarRelatorio(List<ResultadoEstatistico> dados) {
        this.resultados = new ArrayList<>(dados);
        this.dataGeracao = LocalDate.now();
    }

    public List<ResultadoEstatistico> obterResultados() {
        return new ArrayList<>(resultados);
    }

    public void exportarCSV(String caminho) throws IOException {
        try (FileWriter writer = new FileWriter(caminho)) {
            writer.write("Categoria,Quantidade,Percentual\n");

            for (ResultadoEstatistico resultado : resultados) {
                writer.write(
                        resultado.getCategoria() + "," +
                                resultado.obterQuantidadeAbsoluta() + "," +
                                resultado.obterPercentual() + "\n"
                );
            }
        }
    }

    public String gerarTabela() {
        StringBuilder tabela = new StringBuilder();

        tabela.append("Categoria | Quantidade | Percentual\n");

        for (ResultadoEstatistico resultado : resultados) {
            tabela.append(resultado.getCategoria())
                    .append(" | ")
                    .append(resultado.obterQuantidadeAbsoluta())
                    .append(" | ")
                    .append(resultado.obterPercentual())
                    .append("%\n");
        }

        return tabela.toString();
    }
}
