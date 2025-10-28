export class Usuario {
  constructor(
    private _id_usuario: number,
    private _nombre: string,
    private _apellido: string,
    private _email: string,
    private _id_estado: string,
    private _id_rol: string
  ) {}

  get id_usuario(): number { return this._id_usuario; }
  get nombre(): string { return this._nombre; }
  get apellido(): string { return this._apellido; }
  get email(): string { return this._email; }
  get id_estado(): string { return this._id_estado; }
  get id_rol(): string { return this._id_rol; }
  get nombreCompleto(): string { return `${this._nombre} ${this._apellido}`; }
  
  set nombre(valor: string) { this._nombre = valor; }
  set apellido(valor: string) { this._apellido = valor; }
  set email(valor: string) { this._email = valor; }
  set id_estado(valor: string) { this._id_estado = valor; }
  set id_rol(valor: string) { this._id_rol = valor; }

  static crearDesdeDatos(id: number, nombre: string, apellido: string, email: string, id_estado: string, id_rol: string): Usuario {
    return new Usuario(id, nombre, apellido, email, id_estado, id_rol);
  }

  static fromJSON(json: any): Usuario {
    return new Usuario(json._id_usuario, json._nombre, json._apellido, json._email, json._id_estado, json._id_rol);
  }

  actualizarDatos(nombre: string, apellido: string, email: string): void {
    this._nombre = nombre;
    this._apellido = apellido;
    this._email = email;
  }
}