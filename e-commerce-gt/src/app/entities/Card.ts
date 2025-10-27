export class Card {
  constructor(
    private _numero: string,
    private _cvv: string,
    private _fecha_vencimiento: Date,
    private _saldo: number,
    private _id_usuario: number
  ) {}

  //Getters
  get numero(): string {
    return this._numero;
  }

  get cvv(): string {
    return this._cvv;
  }

  get fecha_vencimiento(): Date {
    return this._fecha_vencimiento;
  }

  get saldo(): number {
    return this._saldo;
  }

  get id_usuario(): number {
    return this._id_usuario;
  }

  //Setters
  set cvv(valor: string) {
    this._cvv = valor;
  }

  set fecha_vencimiento(valor: Date) {
    this._fecha_vencimiento = valor;
  }

  set saldo(valor: number) {
    this._saldo = valor;
  }

  //Retorna los últimos 4 dígitos de la tarjeta
  get ultimosDigitos(): string {
    return this._numero.slice(-4);
  }

  //Retorna el número enmascarado
  get numeroEnmascarado(): string {
    const grupos = this._numero.match(/.{1,4}/g) || [];
    return grupos.map((grupo, index) => 
      index === grupos.length - 1 ? grupo : '****'
    ).join(' ');
  }

  //Verificar vencimiento
  get estaVencida(): boolean {
    const hoy = new Date();
    return this._fecha_vencimiento < hoy;
  }

  //Verificar saldo
  tieneSaldoSuficiente(monto: number): boolean {
    return this._saldo >= monto;
  }

  //Card
  static crearDesdeDatos(
    numero: string,
    cvv: string,
    fechaVencimiento: Date,
    saldo: number,
    idUsuario: number
  ): Card {
    return new Card(numero, cvv, fechaVencimiento, saldo, idUsuario);
  }

  //Card  JSON
  static fromJSON(json: any): Card {
    return new Card(
      json._numero || json.numero,
      json._cvv || json.cvv,
      new Date(json._fecha_vencimiento || json.fecha_vencimiento),
      json._saldo || json.saldo,
      json._id_usuario || json.id_usuario
    );
  }

  // Convierte  a formato JSON 
  toJSON(): any {
    return {
      numero: this._numero,
      cvv: this._cvv,
      fecha_vencimiento: this._fecha_vencimiento.toISOString().split('T')[0],
      saldo: this._saldo,
      id_usuario: this._id_usuario
    };
  }
}