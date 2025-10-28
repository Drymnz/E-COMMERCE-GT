export class Sancion {
  constructor(
    private _id_sancion: number,
    private _motivo: string,
    private _fecha_hora: Date,
    private _id_usuario: number,
    private _nombre_usuario?: string,
    private _email_usuario?: string
  ) {}

  get id_sancion(): number { return this._id_sancion; }
  get motivo(): string { return this._motivo; }
  get fecha_hora(): Date { return this._fecha_hora; }
  get id_usuario(): number { return this._id_usuario; }
  get nombre_usuario(): string | undefined { return this._nombre_usuario; }
  get email_usuario(): string | undefined { return this._email_usuario; }
  
  set motivo(valor: string) { this._motivo = valor; }
  set fecha_hora(valor: Date) { this._fecha_hora = valor; }
  set id_usuario(valor: number) { this._id_usuario = valor; }
  set nombre_usuario(valor: string | undefined) { this._nombre_usuario = valor; }
  set email_usuario(valor: string | undefined) { this._email_usuario = valor; }

  static crearDesdeDatos(id: number, motivo: string, fecha_hora: Date, id_usuario: number, nombre_usuario?: string, email_usuario?: string): Sancion {
    return new Sancion(id, motivo, fecha_hora, id_usuario, nombre_usuario, email_usuario);
  }

  static fromJSON(json: any): Sancion {
    const fechaHora = typeof json.fecha_hora === 'string' ? new Date(json.fecha_hora) : json.fecha_hora;
    return new Sancion(json.id_sancion, json.motivo, fechaHora, json.id_usuario, json.nombre_usuario, json.email_usuario);
  }

  get fechaFormateada(): string {
    return this._fecha_hora.toLocaleString('es-GT', {
      year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'
    });
  }
}
