export class Comentario {
    constructor(
        private _id_comentario: number,
        private _descripcion: string,
        private _puntuacion: number,
        private _id_usuario: number,
        private _id_articulo: number
    ) { }

    // Getters
    get id_comentario(): number { return this._id_comentario; }
    get descripcion(): string { return this._descripcion; }
    get puntuacion(): number { return this._puntuacion; }
    get id_usuario(): number { return this._id_usuario; }
    get id_articulo(): number { return this._id_articulo; }

    // Setters
    set descripcion(valor: string) { this._descripcion = valor; }
    set puntuacion(valor: number) {
        if (valor >= 1 && valor <= 5) this._puntuacion = valor;
        else throw new Error('La puntuación debe estar entre 1 y 5');
    }
    set id_usuario(valor: number) { this._id_usuario = valor; }
    set id_articulo(valor: number) { this._id_articulo = valor; }

    static crearDesdeDatos(id: number, desc: string, punt: number, idUser: number, idArt: number): Comentario {
        return new Comentario(id, desc, punt, idUser, idArt);
    }

    static fromJSON(json: any): Comentario {
        return new Comentario(
            json.id_comentario || json._id_comentario,
            json.descripcion || json._descripcion,
            json.puntuacion || json._puntuacion,
            json.id_usuario || json._id_usuario,
            json.id_articulo || json._id_articulo
        );
    }

    actualizarDatos(descripcion: string, puntuacion: number): void {
        this.descripcion = descripcion;
        this.puntuacion = puntuacion;
    }

    get puntuacionValida(): boolean {
        return this._puntuacion >= 1 && this._puntuacion <= 5;
    }
}