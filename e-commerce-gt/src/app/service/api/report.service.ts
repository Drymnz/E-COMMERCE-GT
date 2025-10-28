import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Usuario } from '../../entities/Usuario';
import { Articulo } from '../../entities/Article';
import { environment } from './article.service';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) { }

  private crearParamsFecha(fechaInicio: string, fechaFin: string): HttpParams {
    return new HttpParams().set('fechaInicio', fechaInicio).set('fechaFin', fechaFin);
  }

  /**
   * Top 10 productos más vendidos en un intervalo de tiempo
   * @param fechaInicio formato: YYYY-MM-DDTHH:mm:ss
   * @param fechaFin formato: YYYY-MM-DDTHH:mm:ss
   */
  obtenerTopProductosVendidos(fechaInicio: string, fechaFin: string): Observable<Articulo[]> {
    return this.http.get<any[]>(`${this.apiUrl}/productos-mas-vendidos`, { 
      params: this.crearParamsFecha(fechaInicio, fechaFin) 
    }).pipe(map(r => r.map((a: any) => Articulo.fromJSON(a))));
  }

  /**
   * Top 5 clientes que más ganancias por compras han generado
   * @param fechaInicio formato: YYYY-MM-DDTHH:mm:ss
   * @param fechaFin formato: YYYY-MM-DDTHH:mm:ss
   */
  obtenerTopClientesCompradores(fechaInicio: string, fechaFin: string): Observable<Usuario[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clientes-mejores-compradores`, { 
      params: this.crearParamsFecha(fechaInicio, fechaFin) 
    }).pipe(map(r => r.map((u: any) => this.mapearUsuario(u))));
  }

  /**
   * Top 5 clientes que más productos han vendido
   * @param fechaInicio formato: YYYY-MM-DDTHH:mm:ss
   * @param fechaFin formato: YYYY-MM-DDTHH:mm:ss
   */
  obtenerTopClientesVendedores(fechaInicio: string, fechaFin: string): Observable<Usuario[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clientes-mejores-vendedores`, { 
      params: this.crearParamsFecha(fechaInicio, fechaFin) 
    }).pipe(map(r => r.map((u: any) => this.mapearUsuario(u))));
  }

  /**
   * Top 10 clientes que más pedidos han realizado
   * @param fechaInicio formato: YYYY-MM-DDTHH:mm:ss
   * @param fechaFin formato: YYYY-MM-DDTHH:mm:ss
   */
  obtenerTopClientesPedidos(fechaInicio: string, fechaFin: string): Observable<Usuario[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clientes-mas-pedidos`, { 
      params: this.crearParamsFecha(fechaInicio, fechaFin) 
    }).pipe(map(r => r.map((u: any) => this.mapearUsuario(u))));
  }

  obtenerTopClientesProductosVenta(): Observable<Usuario[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clientes-mas-productos-venta`).pipe(
      map(r => r.map((u: any) => this.mapearUsuario(u)))
    );
  }

  // convierte fechas Date a formato YYYY-MM-DDTHH:mm:ss
  formatearFecha(fecha: Date): string {
    return fecha.toISOString().slice(0, 19);
  }

  // obtiene rango de fechas común (último mes)
  obtenerRangoUltimoMes(): { fechaInicio: string, fechaFin: string } {
    const fechaFin = new Date();
    const fechaInicio = new Date();
    fechaInicio.setMonth(fechaInicio.getMonth() - 1);
    return {
      fechaInicio: this.formatearFecha(fechaInicio),
      fechaFin: this.formatearFecha(fechaFin)
    };
  }

  // obtiene rango de fechas común (último año)
  obtenerRangoUltimoAnio(): { fechaInicio: string, fechaFin: string } {
    const fechaFin = new Date();
    const fechaInicio = new Date();
    fechaInicio.setFullYear(fechaInicio.getFullYear() - 1);
    return {
      fechaInicio: this.formatearFecha(fechaInicio),
      fechaFin: this.formatearFecha(fechaFin)
    };
  }

  private mapearUsuario(data: any): Usuario {
    return Usuario.crearDesdeDatos(
      data.id_usuario,
      data.nombre,
      data.apellido,
      data.email,
      data.id_estado.toString(),
      data.id_rol.toString()
    );
  }
}