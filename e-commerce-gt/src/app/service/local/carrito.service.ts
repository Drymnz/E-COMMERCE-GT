import { Injectable, signal, computed, effect } from '@angular/core';
import { ItemCarrito } from '../../entities/ItemCarrito';
import { Articulo } from '../../entities/Article';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {
  private readonly STORAGE_KEY = 'carrito_compras';
  
  // Signal con datos cargados desde localStorage
  private carritoItems = signal<ItemCarrito[]>(this.cargarCarritoLocal());

  items = this.carritoItems.asReadonly();
  totalItems = computed(() => this.carritoItems().reduce((sum, item) => sum + item.cantidad, 0));
  totalPrecio = computed(() => this.carritoItems().reduce((sum, item) => sum + (item.articulo.precio * item.cantidad), 0));

  constructor() {
    // Guarda automáticamente cuando cambia el carrito
    effect(() => this.guardarCarritoLocal(this.carritoItems()));
  }

  agregarArticulo(articulo: Articulo, cantidad: number = 1): void {
    const itemsActuales = this.carritoItems();
    const itemExistente = itemsActuales.find(i => i.articulo.id_articulo === articulo.id_articulo);

    if (itemExistente) {
      const nuevaCantidad = Math.min(itemExistente.cantidad + cantidad, articulo.stock);
      this.carritoItems.update(items => items.map(i =>
        i.articulo.id_articulo === articulo.id_articulo
          ? ItemCarrito.crearDesdeDatos(i.articulo, nuevaCantidad)
          : i
      ));
    } else {
      this.carritoItems.update(items => [
        ...items,
        ItemCarrito.crearDesdeDatos(articulo, Math.min(cantidad, articulo.stock))
      ]);
    }
  }

  actualizarCantidad(idArticulo: number, cantidad: number): void {
    if (cantidad <= 0) {
      this.eliminarArticulo(idArticulo);
      return;
    }

    this.carritoItems.update(items => items.map(i => 
      i.articulo.id_articulo === idArticulo
        ? ItemCarrito.crearDesdeDatos(i.articulo, Math.min(cantidad, i.articulo.stock))
        : i
    ));
  }

  eliminarArticulo(idArticulo: number): void {
    this.carritoItems.update(items => items.filter(i => i.articulo.id_articulo !== idArticulo));
  }

  vaciarCarrito(): void {
    this.carritoItems.set([]);
  }

  obtenerItem(idArticulo: number): ItemCarrito | undefined {
    return this.carritoItems().find(i => i.articulo.id_articulo === idArticulo);
  }

  // Guarda el carrito en localStorage
  private guardarCarritoLocal(items: ItemCarrito[]): void {
    try {
      const datosGuardar = items.map(i => ({
        articulo: {
          id_articulo: i.articulo.id_articulo,
          nombre: i.articulo.nombre,
          precio: i.articulo.precio,
          stock: i.articulo.stock,
          descripcion: i.articulo.descripcion,
          imagen: i.articulo.imagen,
          id_categoria: i.articulo.id_categoria,
          id_estado: i.articulo.id_estado
        },
        cantidad: i.cantidad
      }));
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(datosGuardar));
    } catch (error) {
      console.error('Error al guardar carrito:', error);
    }
  }

  // Carga el carrito desde localStorage
  private cargarCarritoLocal(): ItemCarrito[] {
    try {
      const datos = localStorage.getItem(this.STORAGE_KEY);
      if (!datos) return [];

      return JSON.parse(datos).map((i: any) => 
        ItemCarrito.crearDesdeDatos(Articulo.fromJSON(i.articulo), i.cantidad)
      );
    } catch (error) {
      console.error('Error al cargar carrito:', error);
      return [];
    }
  }
}