export class Notificacion {
  constructor(
    private _id_notificacion: number,
    private _mensaje: string,
    private _fecha_hora: Date,
    private _id_usuario: number
  ) {}

  static fromJSON(json: any): Notificacion {
    return new Notificacion(
      json.id_notificacion || json._id_notificacion,
      json.mensaje || json._mensaje,
      new Date(json.fecha_hora || json._fecha_hora),
      json.id_usuario || json._id_usuario
    );
  }

  // Getters
  get id_notificacion(): number {
    return this._id_notificacion;
  }

  get mensaje(): string {
    return this._mensaje;
  }

  get fecha_hora(): Date {
    return this._fecha_hora;
  }

  get id_usuario(): number {
    return this._id_usuario;
  }

  // Setters
  set mensaje(mensaje: string) {
    this._mensaje = mensaje;
  }

  set fecha_hora(fecha: Date) {
    this._fecha_hora = fecha;
  }

  // útiles
  get fechaFormateada(): string {
    return this._fecha_hora.toLocaleDateString('es-GT', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  get fechaRelativa(): string {
    const ahora = new Date();
    const diff = ahora.getTime() - this._fecha_hora.getTime();
    const segundos = Math.floor(diff / 1000);
    const minutos = Math.floor(segundos / 60);
    const horas = Math.floor(minutos / 60);
    const dias = Math.floor(horas / 24);

    if (dias > 7) {
      return this.fechaFormateada;
    } else if (dias > 0) {
      return `Hace ${dias} día${dias !== 1 ? 's' : ''}`;
    } else if (horas > 0) {
      return `Hace ${horas} hora${horas !== 1 ? 's' : ''}`;
    } else if (minutos > 0) {
      return `Hace ${minutos} minuto${minutos !== 1 ? 's' : ''}`;
    } else {
      return 'Hace un momento';
    }
  }

  get esReciente(): boolean {
    const ahora = new Date();
    const diff = ahora.getTime() - this._fecha_hora.getTime();
    const horas = diff / (1000 * 60 * 60);
    return horas < 24;
  }

  //  clonar
  clone(): Notificacion {
    return new Notificacion(
      this._id_notificacion,
      this._mensaje,
      new Date(this._fecha_hora),
      this._id_usuario
    );
  }

  //  convertir a JSON
  toJSON(): any {
    return {
      id_notificacion: this._id_notificacion,
      mensaje: this._mensaje,
      fecha_hora: this._fecha_hora.toISOString(),
      id_usuario: this._id_usuario
    };
  }
}