export class ItemCarrito {
  constructor(
    private _articulo: any,
    private _cantidad: number
  ) {}

  // Getters
  get articulo(): any { return this._articulo; }
  get cantidad(): number { return this._cantidad; }

  // Setters
  set articulo(valor: any) { this._articulo = valor; }
  set cantidad(valor: number) { this._cantidad = valor; }

  // Métodos estáticos
  static crearDesdeDatos(articulo: any, cantidad: number): ItemCarrito {
    return new ItemCarrito(articulo, cantidad);
  }

  static fromJSON(json: any): ItemCarrito {
    return new ItemCarrito(
      json.articulo || json._articulo,
      json.cantidad || json._cantidad
    );
  }

  // Métodos de instancia
  actualizarCantidad(cantidad: number): void {
    this.cantidad = cantidad;
  }

  incrementarCantidad(): void {
    this.cantidad++;
  }

  decrementarCantidad(): void {
    if (this.cantidad > 1) this.cantidad--;
  }
}