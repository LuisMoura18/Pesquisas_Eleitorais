package presentation.controller;

import application.pesquisa.FiltroPesquisa;
import application.pesquisa.PesquisaService;
import domain.model.pesquisa.Abrangencia;
import domain.model.pesquisa.Pesquisa;
import domain.model.pesquisa.SituacaoPesquisa;
import domain.model.pesquisa.TipoPesquisa;
import java.time.LocalDate;
import java.util.List;

public class ConsultaPesquisaController {

    private final PesquisaService service;

    public ConsultaPesquisaController(PesquisaService service) {
        this.service = service;
    }

    public List<Pesquisa> listarTodas() {
        return service.listar();
    }

    public List<Pesquisa> filtrar(LocalDate inicio, LocalDate fim, String cargo,
                                  TipoPesquisa tipo, SituacaoPesquisa situacao,
                                  Abrangencia abrangencia, Integer qtdMinimaPrevista) {
        FiltroPesquisa filtro = new FiltroPesquisa();

        if (inicio != null || fim != null) {
            filtro.comPeriodo(inicio, fim);
        }
        if (cargo != null && !cargo.isBlank()) {
            filtro.comCargo(cargo.trim());
        }
        if (tipo != null) {
            filtro.comTipo(tipo);
        }
        if (situacao != null) {
            filtro.comSituacao(situacao);
        }
        if (abrangencia != null) {
            filtro.comAbrangencia(abrangencia);
        }
        if (qtdMinimaPrevista != null) {
            filtro.comQuantidadeMinimaPrevista(qtdMinimaPrevista);
        }

        return service.consultar(filtro);
    }
}