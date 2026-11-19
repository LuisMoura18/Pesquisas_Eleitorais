
package application.analise;

import application.admin.SessaoUsuario;
import domain.exceptions.AcessoNegadoException;
import domain.model.admin.PerfilAcesso;
import domain.model.admin.StatusUsuario;
import domain.model.admin.Usuario;
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

    private void validarPermissao() {
        Usuario logado = SessaoUsuario.getInstancia().getUsuarioLogado();

        if (logado == null) {
            throw new AcessoNegadoException("Acesso negado: nenhum usuário autenticado na sessão.");
        }

        if (logado.getStatus() != StatusUsuario.ATIVO) {
            throw new AcessoNegadoException("Acesso negado: usuário inativo no sistema.");
        }

        boolean temPermissao = logado.getPerfis().contains(PerfilAcesso.ANALISTA)
                || logado.getPerfis().contains(PerfilAcesso.PESQUISADOR)
                || logado.getPerfis().contains(PerfilAcesso.ADMINISTRADOR);

        if (!temPermissao) {
            throw new AcessoNegadoException("Acesso negado: perfil sem permissão para análise e relatórios.");
        }
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
