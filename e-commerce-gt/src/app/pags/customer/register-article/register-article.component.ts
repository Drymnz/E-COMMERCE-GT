import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Articulo } from '../../../entities/Customer';
import { ListConstantService } from '../../../service/api/list-constant.service';
import { ArticleComponent } from '../../general/article/article.component';
import { AuthService } from '../../../service/local/auth.service';
import { ArticleService } from '../../../service/api/article.service';
import { Publicacion } from '../../../entities/Publication';

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

  // Nuevas propiedades para el modo edición
  articuloId: number | null = null;
  isEditMode: boolean = false;

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
    // Aquí deberías cargar el artículo desde tu servicio
    // Por ahora simulo la carga con datos de ejemplo

    // Simulación de datos (reemplaza con tu servicio real)
    const articuloMock = new Articulo(
      id,
      'Laptop Dell Inspiron 15',
      'Laptop potente para trabajo y estudio',
      4500.00,
      'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400',
      5,
      1,
      ['Electrónica', 'Computadoras']
    );

    // Llenar el formulario con los datos del artículo
    this.articuloForm.patchValue({
      nombre: articuloMock.nombre,
      descripcion: articuloMock.descripcion,
      precio: articuloMock.precio,
      stock: articuloMock.stock,
      imagen: articuloMock.imagen,
      id_estado_articulo: articuloMock.id_estado_articulo
    });

    // Marcar las categorías seleccionadas
    const categoriasArray = this.articuloForm.get('categorias') as FormArray;
    articuloMock.categorias.forEach(cat => {
      const index = this.categorias.indexOf(cat);
      if (index !== -1) {
        categoriasArray.at(index).setValue(true);
      }
    });

    // Si hay imagen, mostrarla
    if (articuloMock.imagen) {
      this.imagenPreviewUrl = articuloMock.imagen;
    }
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
      this.getCategoriasSeleccionadas()
    );
  }

  getCategoriasSeleccionadas(): string[] {
    const categoriasArray = this.articuloForm.get('categorias') as FormArray;
    return this.categorias
      .map((cat, i) => categoriasArray.at(i).value ? `${(i+1)}` : null)
      .filter(cat => cat !== null) as string[];
  }

  // Método actualizado para manejar la selección de imagen
  onImagenSeleccionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];

      // Validar que sea una imagen
      if (!file.type.startsWith('image/')) {
        alert('Por favor selecciona un archivo de imagen válido');
        return;
      }

      // Validar tamaño (opcional, por ejemplo máximo 5MB)
      const maxSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxSize) {
        alert('La imagen es demasiado grande. Tamaño máximo: 5MB');
        return;
      }

      const reader = new FileReader();

      reader.onload = (e) => {
        const base64String = e.target?.result as string;

        // Guardar para el preview
        this.imagenPreviewUrl = base64String;

        // IMPORTANTE: Guardar en el formulario para enviar al backend
        this.articuloForm.patchValue({
          imagen: base64String
        });

      };

      reader.onerror = (error) => {
        console.error('Error al leer la imagen:', error);
        alert('Error al cargar la imagen');
      };

      // Convertir a base64
      reader.readAsDataURL(file);
    }
  }

  // Método actualizado para eliminar imagen
  eliminarImagen(): void {
    this.imagenPreviewUrl = '';
    this.articuloForm.patchValue({ imagen: '' });

    // Limpiar el input file
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  // Método actualizado para guardar el artículo
  guardarArticulo(): void {
    if (this.articuloForm.valid && this.getCategoriasSeleccionadas().length > 0) {
      const articuloData = {
        ...this.articuloForm.value,
        categorias: this.getCategoriasSeleccionadas()
      };

      if (this.isEditMode) {
        console.log('Actualizando artículo:', this.articuloId, articuloData);
        alert('Artículo actualizado correctamente');
      } else {
        const id_user = this.authService.currentUserValue?.id_usuario;

        if (!id_user) {
          alert('Error: Usuario no autenticado');
          return;
        }

        // Crear el artículo
        const nuevoArticulo = new Articulo(
          0,
          articuloData.nombre,
          articuloData.descripcion,
          articuloData.precio,
          articuloData.imagen,
          articuloData.stock,
          articuloData.id_estado_articulo,
          articuloData.categorias
        );

        // Crear la publicación con el artículo
        const nuevaPublicacion = new Publicacion(
          id_user,
          nuevoArticulo
        );

        // DEBUG: Ver el JSON que se enviará
        const jsonToSend = nuevaPublicacion.toJSON();

        // Llamar al servicio para crear la publicación
        this.articleService.createPublicacion(nuevaPublicacion).subscribe({
          next: (response) => {
            this.router.navigate(['/manage-products-sale']);
          },
          error: (error) => {
            console.error('Error al crear la publicación:', error);
            console.error('Detalle del error:', error.error);
            alert('Error al crear el artículo: ' + (error.error?.error || error.error?.message || error.message));
          }
        });
      }
    } else {
      if (this.getCategoriasSeleccionadas().length === 0) {
        alert('Debes seleccionar al menos una categoría');
      }
      if (this.articuloForm.invalid) {
        alert('Por favor completa todos los campos requeridos correctamente');
        Object.keys(this.articuloForm.controls).forEach(key => {
          const control = this.articuloForm.get(key);2
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
}