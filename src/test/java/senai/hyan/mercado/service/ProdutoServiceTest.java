package senai.hyan.mercado.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import senai.hyan.mercado.entity.Produto;
import senai.hyan.mercado.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade do serviço: o repositório é simulado com Mockito,
 * então nada aqui toca no banco.
 */
@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    @Test
    void deveSalvarProdutoValido() {
        when(repository.save(any(Produto.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Produto salvo = service.salvar(produto("Arroz", 10, "25.90"));

        assertEquals("Arroz", salvo.getNome());
        verify(repository).save(salvo);
    }

    @Test
    void naoDeveSalvarProdutoSemNome() {
        assertThrows(ResponseStatusException.class, () -> service.salvar(produto(" ", 10, "25.90")));
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveSalvarProdutoComPrecoNegativo() {
        assertThrows(ResponseStatusException.class, () -> service.salvar(produto("Arroz", 10, "-1")));
        verify(repository, never()).save(any());
    }

    @Test
    void deveAtualizarOsCamposDoProdutoExistente() {
        UUID id = UUID.randomUUID();
        Produto existente = produto("Arroz", 10, "25.90");
        existente.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Produto.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Produto atualizado = service.atualizar(id, produto("Arroz integral", 4, "30.00"));

        assertEquals(id, atualizado.getId());
        assertEquals("Arroz integral", atualizado.getNome());
        assertEquals(4, atualizado.getQuantidade());
        assertEquals(new BigDecimal("30.00"), atualizado.getPreco());
    }

    @Test
    void deveFalharAoExcluirProdutoInexistente() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.excluir(id));
        verify(repository, never()).delete(any());
    }

    private Produto produto(String nome, int quantidade, String preco) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao("Descrição de teste");
        produto.setQuantidade(quantidade);
        produto.setPreco(new BigDecimal(preco));
        return produto;
    }
}
