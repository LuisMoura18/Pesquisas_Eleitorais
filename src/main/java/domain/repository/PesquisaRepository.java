package domain.repository;

import domain.model.pesquisa.Pesquisa;

import java.util.List;
import java.util.Optional;

public interface PesquisaRepository {
    void salvar(Pesquisa pesquisa);
    Optional<Pesquisa> buscarPorCodigo(String codigo);
    List<Pesquisa> listar();
    void excluir(String codigo);
}