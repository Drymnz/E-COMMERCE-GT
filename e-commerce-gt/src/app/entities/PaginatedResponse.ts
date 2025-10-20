import { Usuario } from './Usuario';

export class PaginatedResponse {
  constructor(
    private _usuarios: Usuario[],
    private _currentPage: number,
    private _pageSize: number,
    private _totalUsuarios: number,
    private _totalPages: number
  ) {}

  /** Getters */
  get usuarios(): Usuario[] {
    return this._usuarios;
  }

  get currentPage(): number {
    return this._currentPage;
  }

  get pageSize(): number {
    return this._pageSize;
  }

  get totalUsuarios(): number {
    return this._totalUsuarios;
  }

  get totalPages(): number {
    return this._totalPages;
  }

  /** Setters */
  set usuarios(valor: Usuario[]) {
    this._usuarios = valor;
  }

  set currentPage(valor: number) {
    this._currentPage = valor;
  }

  set pageSize(valor: number) {
    this._pageSize = valor;
  }

  set totalUsuarios(valor: number) {
    this._totalUsuarios = valor;
  }

  set totalPages(valor: number) {
    this._totalPages = valor;
  }

  // Crear desde datos
  static crearDesdeDatos(
    usuarios: Usuario[],
    currentPage: number,
    pageSize: number,
    totalUsuarios: number,
    totalPages: number
  ): PaginatedResponse {
    return new PaginatedResponse(usuarios, currentPage, pageSize, totalUsuarios, totalPages);
  }

  // Crear desde JSON
  static fromJSON(json: any, usuarios: Usuario[]): PaginatedResponse {
    return new PaginatedResponse(
      usuarios,
      json.currentPage,
      json.pageSize,
      json.totalUsuarios,
      json.totalPages
    );
  }

  // Verificar si hay más páginas
  get tienePaginaAnterior(): boolean {
    return this._currentPage > 1;
  }

  get tienePaginaSiguiente(): boolean {
    return this._currentPage < this._totalPages;
  }

  // Obtener rango de registros actuales
  get rangoInicio(): number {
    return (this._currentPage - 1) * this._pageSize + 1;
  }

  get rangoFin(): number {
    return Math.min(this._currentPage * this._pageSize, this._totalUsuarios);
  }
}