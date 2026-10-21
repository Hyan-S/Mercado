package senai.hyan.mercado;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import senai.hyan.mercado.entity.Produto;
import senai.hyan.mercado.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração: abrem a tela no Chrome, executam a ação pela interface
 * e conferem o resultado direto no banco (SQL Server) pelo repositório.
 *
 * O Chrome abre visível por padrão; rode com -Dheadless=true para esconder a janela.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProdutoSeleniumTest {

    @LocalServerPort
    private int porta;

    @Autowired
    private ProdutoRepository repository;

    private WebDriver driver;
    private WebDriverWait espera;
    private String nomeTeste;

    @BeforeEach
    void abrirNavegador() {
        ChromeOptions opcoes = new ChromeOptions();
        if (Boolean.getBoolean("headless")) {
            opcoes.addArguments("--headless=new");
        }
        opcoes.addArguments("--window-size=1280,900");
        driver = new ChromeDriver(opcoes);
        espera = new WebDriverWait(driver, Duration.ofSeconds(10));
        nomeTeste = "Selenium " + UUID.randomUUID().toString().substring(0, 8);
    }

    @AfterEach
    void fecharNavegador() {
        if (driver != null) {
            driver.quit();
        }
        repository.deleteAll(repository.findByNome(nomeTeste));
        repository.deleteAll(repository.findByNome(nomeTeste + " editado"));
    }

    @Test
    @DisplayName("Teste 1 (Insert): cadastra pela tela e confirma no banco")
    void deveInserirProduto() {
        abrirTela();

        preencher("nome", nomeTeste);
        preencher("descricao", "Arroz tipo 1 - 5kg");
        preencher("quantidade", "10");
        preencher("preco", "25.90");
        driver.findElement(By.id("btn-salvar")).click();

        esperarLinha(nomeTeste);
        assertEquals("Produto salvo com sucesso", driver.findElement(By.id("mensagem")).getText());

        List<Produto> noBanco = repository.findByNome(nomeTeste);
        assertEquals(1, noBanco.size());
        Produto produto = noBanco.get(0);
        assertEquals("Arroz tipo 1 - 5kg", produto.getDescricao());
        assertEquals(10, produto.getQuantidade());
        assertEquals(0, new BigDecimal("25.90").compareTo(produto.getPreco()));
    }

    @Test
    @DisplayName("Teste 2 (Update): edita pela tela e confirma no banco")
    void deveAtualizarProduto() {
        Produto existente = repository.save(novoProduto(nomeTeste));
        abrirTela();

        esperarLinha(nomeTeste).findElement(By.className("btn-editar")).click();
        espera.until(ExpectedConditions.attributeToBe(By.id("nome"), "value", nomeTeste));

        preencher("nome", nomeTeste + " editado");
        preencher("quantidade", "3");
        preencher("preco", "7.50");
        driver.findElement(By.id("btn-salvar")).click();

        esperarLinha(nomeTeste + " editado");
        assertEquals("Produto atualizado com sucesso", driver.findElement(By.id("mensagem")).getText());

        Produto atualizado = repository.findById(existente.getId()).orElseThrow();
        assertEquals(nomeTeste + " editado", atualizado.getNome());
        assertEquals(3, atualizado.getQuantidade());
        assertEquals(0, new BigDecimal("7.50").compareTo(atualizado.getPreco()));
    }

    @Test
    @DisplayName("Teste 3 (Delete): exclui pela tela e confirma no banco")
    void deveExcluirProduto() {
        Produto existente = repository.save(novoProduto(nomeTeste));
        abrirTela();

        WebElement linha = esperarLinha(nomeTeste);
        linha.findElement(By.className("btn-excluir")).click();

        espera.until(ExpectedConditions.stalenessOf(linha));
        assertTrue(driver.findElements(linhaDoProduto(nomeTeste)).isEmpty());
        assertEquals("Produto excluído com sucesso", driver.findElement(By.id("mensagem")).getText());

        assertFalse(repository.existsById(existente.getId()));
    }

    private void abrirTela() {
        driver.get("http://localhost:" + porta + "/");
        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id("nome")));
    }

    private void preencher(String id, String valor) {
        WebElement campo = driver.findElement(By.id(id));
        campo.clear();
        campo.sendKeys(valor);
    }

    private WebElement esperarLinha(String nome) {
        return espera.until(ExpectedConditions.visibilityOfElementLocated(linhaDoProduto(nome)));
    }

    private By linhaDoProduto(String nome) {
        return By.cssSelector("tr[data-nome='" + nome + "']");
    }

    private Produto novoProduto(String nome) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao("Feijão carioca 1kg");
        produto.setQuantidade(20);
        produto.setPreco(new BigDecimal("8.99"));
        return produto;
    }
}
