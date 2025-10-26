import { Articulo } from "./Article";

export class Publicacion {
  constructor(
    private _id_usuario: number,
    private _articulo: Articulo,
    private _id_publicacion?: number,
    private _fecha_hora_entrega?: Date | string
  ) { }

  static fromJSON(json: any): Publicacion {
    return new Publicacion(
      json.id_usuario || json._id_usuario,
      Articulo.fromJSON(json.articulo || json._articulo),
      json.id_publicacion || json._id_publicacion,
      json.fecha_hora_entrega || json._fecha_hora_entrega
    );
  }

  // Getters
  get id_publicacion(): number | undefined {
    return this._id_publicacion;
  }

  get id_usuario(): number {
    return this._id_usuario;
  }

  get articulo(): Articulo {
    return this._articulo;
  }

  get fecha_hora_entrega(): Date | string | undefined {
    return this._fecha_hora_entrega;
  }

  // Setters
  set id_publicacion(id: number | undefined) {
    this._id_publicacion = id;
  }

  set fecha_hora_entrega(fecha: Date | string | undefined) {
    this._fecha_hora_entrega = fecha;
  }

  // Método para convertir a JSON
  toJSON(): any {
    return {
      id_publicacion: this._id_publicacion, 
      id_usuario: this._id_usuario,               
      articulo: this._articulo.toJSON(),
      fecha_hora_entrega: this._fecha_hora_entrega
    };
  }
}