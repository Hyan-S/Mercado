package senai.hyan.mercado.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import senai.hyan.mercado.entity.Produto;
import senai.hyan.mercado.repository.ProdutoRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Produto buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    public Produto salvar(Produto produto) {
        validar(produto);
        produto.setId(null);
        return repository.save(produto);
    }

    public Produto atualizar(UUID id, Produto dados) {
        validar(dados);
        Produto produto = buscar(id);
        produto.setNome(dados.getNome());
        produto.setDescricao(dados.getDescricao());
        produto.setQuantidade(dados.getQuantidade());
        produto.setPreco(dados.getPreco());
        return repository.save(produto);
    }

    public void excluir(UUID id) {
        repository.delete(buscar(id));
    }

    private void validar(Produto produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome é obrigatório");
        }
        if (produto.getDescricao() == null || produto.getDescricao().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Descrição é obrigatória");
        }
        if (produto.getQuantidade() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade não pode ser negativa");
        }
        if (produto.getPreco() == null || produto.getPreco().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço inválido");
        }
    }
}
