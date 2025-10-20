import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ArticleComponent } from '../../../general/article/article.component';
import { ListConstantService } from '../../../../service/api/list-constant.service';
import { AuthService } from '../../../../service/local/auth.service';
import { ArticleService } from '../../../../service/api/article.service';
import { Articulo } from '../../../../entities/Customer';
import { Publicacion } from '../../../../entities/Publication';

@Component({
  selector: 'app-register-article',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ArticleComponent],
  templateUrl: './register-article.component.html',
  styleUrl: './register-article.component.scss'
})
export class RegisterArticleComponent implements OnInit {
  articuloForm!: FormGroup;
  categorias: string[] = [];
  estadoArticulo: string[] = [];
  imagenPreviewUrl: string = '';

  // Propiedades para el modo edición
  articuloId: number | null = null;
  isEditMode: boolean = false;

  // Propiedades para mensajes
  mensajeError: string = '';
  mensajeExito: string = '';

  constructor(
    private fb: FormBuilder,
    private constantService: ListConstantService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private articleService: ArticleService
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.cargarConstantes();

    // Detectar si estamos en modo edición
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.articuloId = parseInt(id);
        this.isEditMode = true;
        this.cargarArticulo(this.articuloId);
      }
    });
  }

  initForm(): void {
    this.articuloForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      precio: [0, [Validators.required, Validators.min(0.01)]],
      stock: [0, [Validators.required, Validators.min(0)]],
      imagen: [''],
      id_estado_articulo: [1, Validators.required],
      categorias: this.fb.array([])
    });
  }

  cargarConstantes(): void {
    this.constantService.tiposCategorias$.subscribe(categorias => {
      this.categorias = categorias;
      this.inicializarCategorias();
    });

    this.constantService.estadosArticulo$.subscribe(estados => {
      this.estadoArticulo = estados;
    });
  }

  inicializarCategorias(): void {
    const categoriasArray = this.articuloForm.get('categorias') as FormArray;
    categoriasArray.clear();
    this.categorias.forEach(() => {
      categoriasArray.push(new FormControl(false));
    });
  }

  cargarArticulo(id: number): void {
    const id_user = this.authService.currentUserValue?.id_usuario;
    
    if (!id_user) {
      this.mostrarError('No hay usuario autenticado');
      return;
    }

    this.articleService.getArticleByUserAndId(id_user, id).subscribe({
      next: (articulo) => {
        if (articulo) {
          // Llenar el formulario con los datos del artículo
          this.articuloForm.patchValue({
            nombre: articulo.nombre,
            descripcion: articulo.descripcion,
            precio: articulo.precio,
            stock: articulo.stock,
            imagen: articulo.imagen,
            id_estado_articulo: articulo.id_estado_articulo
          });

          // Marcar las categorías seleccionadas
          const categoriasArray = this.articuloForm.get('categorias') as FormArray;
          articulo.categorias.forEach(cat => {
            const index = this.categorias.indexOf(cat);
            if (index !== -1) {
              categoriasArray.at(index).setValue(true);
            }
          });

          // Si hay imagen, mostrarla
          if (articulo.imagen) {
            this.imagenPreviewUrl = articulo.imagen;
          }
        } else {
          this.mostrarError('Artículo no encontrado o no pertenece al usuario');
          setTimeout(() => {
            this.router.navigate(['/manage-products-sale']);
          }, 2000);
        }
      },
      error: (error) => {
        this.mostrarError('Error al cargar artículo: ' + (error.error?.message || error.message));
      }
    });
  }

  get articuloPreview(): Articulo {
    const formValue = this.articuloForm.value;
    return new Articulo(
      this.articuloId || 0,
      formValue.nombre || 'Nombre del artículo',
      formValue.descripcion || 'Descripción del artículo',
      formValue.precio || 0,
      this.imagenPreviewUrl || formValue.imagen || 'load.jpg',
      formValue.stock || 0,
      formValue.id_estado_articulo || 1,
      this.getCategoriasSeleccionadas(),
      1
    );
  }

  getCategoriasSeleccionadas(): string[] {
    const categoriasArray = this.articuloForm.get('categorias') as FormArray;
    return this.categorias
      .map((cat, i) => categoriasArray.at(i).value ? `${(i+1)}` : null)
      .filter(cat => cat !== null) as string[];
  }

  onImagenSeleccionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];

      // Validar que sea una imagen
      if (!file.type.startsWith('image/')) {
        this.mostrarError('Por favor selecciona un archivo de imagen válido');
        return;
      }

      // Validar tamaño (máximo 5MB)
      const maxSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxSize) {
        this.mostrarError('La imagen es demasiado grande. Tamaño máximo: 5MB');
        return;
      }

      const reader = new FileReader();

      reader.onload = (e) => {
        const base64String = e.target?.result as string;

        // Guardar para el preview
        this.imagenPreviewUrl = base64String;

        // Guardar en el formulario para enviar al backend
        this.articuloForm.patchValue({
          imagen: base64String
        });
      };

      reader.onerror = (error) => {
        this.mostrarError('Error al cargar la imagen');
      };

      // Convertir a base64
      reader.readAsDataURL(file);
    }
  }

  eliminarImagen(): void {
    this.imagenPreviewUrl = '';
    this.articuloForm.patchValue({ imagen: '' });

    // Limpiar el input file
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  guardarArticulo(): void {
    if (this.articuloForm.valid && this.getCategoriasSeleccionadas().length > 0) {
      const articuloData = {
        ...this.articuloForm.value,
        categorias: this.getCategoriasSeleccionadas()
      };

      if (this.isEditMode && this.articuloId) {
        // MODO EDICIÓN: Actualizar artículo existente
        const articuloActualizado = new Articulo(
          this.articuloId,
          articuloData.nombre,
          articuloData.descripcion,
          articuloData.precio,
          articuloData.imagen,
          articuloData.stock,
          articuloData.id_estado_articulo,
          articuloData.categorias,
          1
        );

        this.articleService.updateArticle(this.articuloId, articuloActualizado).subscribe({
          next: (response) => {
            this.mostrarExito('Artículo actualizado correctamente');
            setTimeout(() => {
              this.router.navigate(['/manage-products-sale']);
            }, 1500);
          },
          error: (error) => {
            this.mostrarError('Error al actualizar el artículo: ' + (error.error?.error || error.error?.message || error.message));
          }
        });

      } else {
        // MODO CREACIÓN: Crear nuevo artículo
        const id_user = this.authService.currentUserValue?.id_usuario;

        if (!id_user) {
          this.mostrarError('Error: Usuario no autenticado');
          return;
        }

        const nuevoArticulo = new Articulo(
          0,
          articuloData.nombre,
          articuloData.descripcion,
          articuloData.precio,
          articuloData.imagen,
          articuloData.stock,
          articuloData.id_estado_articulo,
          articuloData.categorias,
          1
        );

        const nuevaPublicacion = new Publicacion(id_user, nuevoArticulo);

        this.articleService.createPublicacion(nuevaPublicacion).subscribe({
          next: (response) => {
            this.mostrarExito('Artículo creado correctamente');
            setTimeout(() => {
              this.router.navigate(['/manage-products-sale']);
            }, 1500);
          },
          error: (error) => {
            this.mostrarError('Error al crear el artículo: ' + (error.error?.error || error.error?.message || error.message));
          }
        });
      }
    } else {
      // Validaciones fallidas
      if (this.getCategoriasSeleccionadas().length === 0) {
        this.mostrarError('Debes seleccionar al menos una categoría');
      }
      if (this.articuloForm.invalid) {
        this.mostrarError('Por favor completa todos los campos requeridos correctamente');
        Object.keys(this.articuloForm.controls).forEach(key => {
          const control = this.articuloForm.get(key);
          if (control?.invalid) {
            control.markAsTouched();
          }
        });
      }
    }
  }

  limpiarFormulario(): void {
    this.articuloForm.reset({
      id_estado_articulo: 1,
      precio: 0,
      stock: 0
    });
    this.imagenPreviewUrl = '';
    this.inicializarCategorias();
    this.limpiarMensajes();
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.articuloForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.articuloForm.get(fieldName);
    if (field?.hasError('required')) {
      return 'Este campo es obligatorio';
    }
    if (field?.hasError('minlength')) {
      const minLength = field.errors?.['minlength'].requiredLength;
      return `Mínimo ${minLength} caracteres`;
    }
    if (field?.hasError('min')) {
      return 'El valor debe ser mayor a 0';
    }
    return '';
  }

  getEstadoNombre(idEstado: number): string {
    if (idEstado > 0 && idEstado <= this.estadoArticulo.length) {
      return this.estadoArticulo[idEstado - 1];
    }
    return 'Sin estado';
  }

  isArticuloDisponible(): boolean {
    return this.articuloForm.get('stock')?.value > 0;
  }

  // ============ MÉTODOS PARA MANEJO DE MENSAJES ============

  /**
   * Muestra un mensaje de error al usuario
   */
  mostrarError(mensaje: string): void {
    this.mensajeError = mensaje;
    this.mensajeExito = '';
    console.error('Error:', mensaje);
    
    // Auto-limpiar después de 5 segundos
    setTimeout(() => {
      this.limpiarMensajes();
    }, 5000);
  }

  /**
   * Muestra un mensaje de éxito al usuario
   */
  mostrarExito(mensaje: string): void {
    this.mensajeExito = mensaje;
    this.mensajeError = '';
    console.log('Éxito:', mensaje);
    
    // Auto-limpiar después de 3 segundos
    setTimeout(() => {
      this.limpiarMensajes();
    }, 3000);
  }

  //Limpia todos los mensajes de error y éxito
  limpiarMensajes(): void {
    this.mensajeError = '';
    this.mensajeExito = '';
  }
}