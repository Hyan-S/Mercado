package senai.hyan.mercado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import senai.hyan.mercado.entity.Produto;

import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    List<Produto> findByNome(String nome);
}
