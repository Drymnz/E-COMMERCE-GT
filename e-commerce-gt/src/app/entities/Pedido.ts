export class Pedido {
  constructor(
    private _id_pedido: number,
    private _fecha_hora_entrega: string,
    private _id_comprador: number,
    private _id_estado_pedido: number
  ) {}

  /** Getters */
  get id_pedido(): number {
    return this._id_pedido;
  }

  get fecha_hora_entrega(): string {
    return this._fecha_hora_entrega;
  }

  get id_comprador(): number {
    return this._id_comprador;
  }

  get id_estado_pedido(): number {
    return this._id_estado_pedido;
  }

  /** Setters */
  set fecha_hora_entrega(valor: string) {
    this._fecha_hora_entrega = valor;
  }

  set id_comprador(valor: number) {
    this._id_comprador = valor;
  }

  set id_estado_pedido(valor: number) {
    this._id_estado_pedido = valor;
  }

  /** Retorna información resumida del pedido */
  get infoResumida(): string {
    return `Pedido #${this._id_pedido} - Estado: ${this._id_estado_pedido}`;
  }

  // Crea una instancia de Pedido
  static crearDesdeDatos(
    id_pedido: number,
    fecha_hora_entrega: string,
    id_comprador: number,
    id_estado_pedido: number
  ): Pedido {
    return new Pedido(id_pedido, fecha_hora_entrega, id_comprador, id_estado_pedido);
  }

  // Crea una instancia de Pedido desde un objeto JSON
  static fromJSON(json: any): Pedido {
    return new Pedido(
      json._id_pedido,
      json._fecha_hora_entrega,
      json._id_comprador,
      json._id_estado_pedido
    );
  }

  /**
   * Actualiza los datos básicos del pedido
   */
  actualizarDatos(
    fecha_hora_entrega: string,
    id_comprador: number,
    id_estado_pedido: number
  ): void {
    this._fecha_hora_entrega = fecha_hora_entrega;
    this._id_comprador = id_comprador;
    this._id_estado_pedido = id_estado_pedido;
  }
}