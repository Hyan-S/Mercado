import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface Produto {
  id?: string;
  nome: string;
  descricao: string;
  quantidade: number;
  preco: number;
}

@Injectable({ providedIn: 'root' })
export class ProdutoService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/produtos';

  listar(): Observable<Produto[]> {
    return this.http.get<Produto[]>(this.url);
  }

  salvar(produto: Produto): Observable<Produto> {
    return this.http.post<Produto>(this.url, produto);
  }

  atualizar(id: string, produto: Produto): Observable<Produto> {
    return this.http.put<Produto>(`${this.url}/${id}`, produto);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
