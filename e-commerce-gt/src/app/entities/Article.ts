export class Articulo {
  constructor(
    private _id_articulo: number,
    private _nombre: string,
    private _descripcion: string,
    private _precio: number,
    private _imagen: string,
    private _stock: number,
    private _id_estado_articulo: number,
    private _categorias: string[] = [],
    private _id_accion: number
  ) {}

  static fromJSON(json: any): Articulo {
    return new Articulo(
      json.id_articulo || json._id_articulo,
      json.nombre || json._nombre,
      json.descripcion || json._descripcion,
      json.precio || json._precio,
      json.imagen || json._imagen,
      json.stock || json._stock,
      json.id_estado_articulo || json._id_estado_articulo,
      json.categorias || json._categorias || [],
      json.id_accion || json._id_accion
    );
  }

  get id_articulo(): number { return this._id_articulo; }
  get nombre(): string { return this._nombre; }
  get descripcion(): string { return this._descripcion; }
  get precio(): number { return this._precio; }
  get id_accion(): number { return this._id_accion; }
  get imagen(): string { return this._imagen; }
  get stock(): number { return this._stock; }
  get id_estado_articulo(): number { return this._id_estado_articulo; }
  get categorias(): string[] { return [...this._categorias]; }

  set nombre(nombre: string) { this._nombre = nombre; }
  set descripcion(descripcion: string) { this._descripcion = descripcion; }
  set precio(precio: number) { this._precio = precio; }
  set imagen(imagen: string) { this._imagen = imagen; }
  set stock(stock: number) { this._stock = stock; }
  set id_estado_articulo(id_estado_articulo: number) { this._id_estado_articulo = id_estado_articulo; }

  agregarCategoria(categoria: string): void {
    if (!this._categorias.includes(categoria)) this._categorias.push(categoria);
  }

  removerCategoria(categoria: string): void {
    this._categorias = this._categorias.filter(cat => cat !== categoria);
  }

  get precioFormateado(): string { return `Q ${this._precio.toFixed(2)}`; }
  get disponible(): boolean { return this._stock > 0; }
  get estadoStock(): string {
    if (this._stock === 0) return 'Agotado';
    if (this._stock < 5) return 'Pocas unidades';
    return 'Disponible';
  }

  toJSON(): any {
    return {
      id_articulo: this._id_articulo,
      nombre: this._nombre,
      descripcion: this._descripcion,
      precio: this._precio,
      imagen: this._imagen,
      stock: this._stock,
      id_estado_articulo: this._id_estado_articulo,
      categorias: [...this._categorias],
      id_accion: this.id_accion
    };
  }
}