package application.pesquisa;

import domain.model.pesquisa.Pesquisa;
import domain.repository.PesquisaRepository;

public class PesquisaService {
    private final PesquisaRepository repo;
    public PesquisaService(PesquisaRepository repo) { this.repo = repo; }

    public Pesquisa cadastrar(String codigo, String nome, String cargo, LocalDate ini,
                              LocalDate fim, Abrangencia abr, TipoPesquisa tipo, int qtd) {
        if (repo.buscarPorCodigo(codigo).isPresent())
            throw new PesquisaInvalidaException("Já existe pesquisa com o código " + codigo);
        Pesquisa p = new Pesquisa(codigo, nome, cargo, ini, fim, abr, tipo, qtd);
        repo.salvar(p);
        return p;
    }
}
