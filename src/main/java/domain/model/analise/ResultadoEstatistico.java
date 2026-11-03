
package domain.model.analise;

public class ResultadoEstatistico {

    private int quantidadeAbsoluta;
    private double percentual;
    private String categoria;

    public ResultadoEstatistico(int quantidadeAbsoluta,
                                double percentual,
                                String categoria) {
        this.quantidadeAbsoluta = quantidadeAbsoluta;
        this.percentual = percentual;
        this.categoria = categoria;
    }

    public int obterQuantidadeAbsoluta() {
        return quantidadeAbsoluta;
    }

    public double obterPercentual() {
        return percentual;
    }

    public String getCategoria() {
        return categoria;
    }
}
