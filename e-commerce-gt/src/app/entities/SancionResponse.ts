import { Sancion } from "./Sancion";

export class SancionResponse {
  constructor(
    private _sanciones: Sancion[],
    private _totalSanciones: number,
    private _totalPaginas: number,
    private _paginaActual: number
  ) {}

  get sanciones(): Sancion[] { return this._sanciones; }
  get totalSanciones(): number { return this._totalSanciones; }
  get totalPaginas(): number { return this._totalPaginas; }
  get paginaActual(): number { return this._paginaActual; }
  
  set sanciones(valor: Sancion[]) { this._sanciones = valor; }
  set totalSanciones(valor: number) { this._totalSanciones = valor; }
  set totalPaginas(valor: number) { this._totalPaginas = valor; }
  set paginaActual(valor: number) { this._paginaActual = valor; }

  static crearDesdeDatos(sanciones: Sancion[], totalSanciones: number, totalPaginas: number, paginaActual: number): SancionResponse {
    return new SancionResponse(sanciones, totalSanciones, totalPaginas, paginaActual);
  }

  static fromJSON(json: any): SancionResponse {
    const sanciones = json.sanciones.map((s: any) => Sancion.fromJSON(s));
    return new SancionResponse(sanciones, json.totalSanciones, json.totalPaginas, json.paginaActual);
  }
}