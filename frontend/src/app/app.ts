import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Produto, ProdutoService } from './produto.service';

@Component({
  selector: 'app-root',
  imports: [FormsModule, CurrencyPipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private readonly service = inject(ProdutoService);

  protected readonly produtos = signal<Produto[]>([]);
  protected readonly editandoId = signal<string | null>(null);
  protected readonly mensagem = signal('');

  protected form: Produto = this.novoProduto();

  ngOnInit(): void {
    this.carregar();
  }

  protected salvar(): void {
    const id = this.editandoId();
    const requisicao = id
      ? this.service.atualizar(id, this.form)
      : this.service.salvar(this.form);

    requisicao.subscribe({
      next: () => {
        this.mensagem.set(id ? 'Produto atualizado com sucesso' : 'Produto salvo com sucesso');
        this.cancelar();
        this.carregar();
      },
      error: (erro) => this.mensagem.set(erro.error?.message ?? 'Erro ao salvar o produto')
    });
  }

  protected editar(produto: Produto): void {
    this.form = { ...produto };
    this.editandoId.set(produto.id!);
    this.mensagem.set('');
  }

  protected excluir(produto: Produto): void {
    this.service.excluir(produto.id!).subscribe({
      next: () => {
        this.mensagem.set('Produto excluído com sucesso');
        if (this.editandoId() === produto.id) {
          this.cancelar();
        }
        this.carregar();
      },
      error: () => this.mensagem.set('Erro ao excluir o produto')
    });
  }

  protected cancelar(): void {
    this.form = this.novoProduto();
    this.editandoId.set(null);
  }

  private carregar(): void {
    this.service.listar().subscribe((lista) => this.produtos.set(lista));
  }

  private novoProduto(): Produto {
    return { nome: '', descricao: '', quantidade: 0, preco: 0 };
  }
}
