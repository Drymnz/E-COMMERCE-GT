import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { ArticleComponent } from '../../general/article/article.component';
import { Articulo } from '../../../entities/Customer';
import { ListConstantService } from '../../../service/list-constant.service';

@Component({
  selector: 'app-register-article',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ArticleComponent],
  templateUrl: './register-article.component.html',
  styleUrls: ['./register-article.component.scss']
})
export class RegisterArticleComponent implements OnInit {
  articuloForm!: FormGroup;

  // Listas desde el servicio
  categorias: string[] = [];
  estadoArticulo: string[] = [];

  // Artículo preview
  articuloPreview: Articulo | null = null;

  // Control de imagen
  imagenFile: File | null = null;
  imagenPreviewUrl: string = '';

  constructor(
    private fb: FormBuilder,
    private constantService: ListConstantService
  ) {}

  ngOnInit(): void {
    // Inicializar formulario reactivo
    this.initForm();

    // Obtener categorías
    this.constantService.tiposCategorias$.subscribe(categorias => {
      this.categorias = categorias;
      this.initCategoriasFormArray();
    });

    // Obtener estados de artículo
    this.constantService.estadosArticulo$.subscribe(estadoArticulo => {
      this.estadoArticulo = estadoArticulo;
    });

    // Actualizar preview al cambiar cualquier valor del formulario
    this.articuloForm.valueChanges.subscribe(() => {
      this.actualizarPreview();
    });

    // Actualizar preview inicial
    this.actualizarPreview();
  }

  // Inicializa el formulario reactivo
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

  // Inicializa el FormArray de categorías
  initCategoriasFormArray(): void {
    const categoriasArray = this.articuloForm.get('categorias') as FormArray;
    categoriasArray.clear();

    this.categorias.forEach(() => {
      categoriasArray.push(this.fb.control(false));
    });
  }

  // Obtiene el FormArray de categorías
  get categoriasFormArray(): FormArray {
    return this.articuloForm.get('categorias') as FormArray;
  }

  // Obtiene las categorías seleccionadas
  getCategoriasSeleccionadas(): string[] {
    return this.categorias.filter((_, index) => 
      this.categoriasFormArray.at(index).value === true
    );
  }

  // Maneja la selección de archivo de imagen
  onImagenSeleccionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.imagenFile = input.files[0];
      
      // Crear URL de preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagenPreviewUrl = e.target?.result as string;
        this.articuloForm.patchValue({ imagen: this.imagenPreviewUrl });
        this.actualizarPreview();
      };
      reader.readAsDataURL(this.imagenFile);
    }
  }

  // Elimina la imagen cargada
  eliminarImagen(): void {
    this.imagenPreviewUrl = '';
    this.imagenFile = null;
    this.articuloForm.patchValue({ imagen: '' });
    this.actualizarPreview();
  }

  // Actualiza el preview del artículo
  actualizarPreview(): void {
    const formValue = this.articuloForm.value;
    
    this.articuloPreview = new Articulo(
      0, 
      formValue.nombre || 'Nombre del artículo',
      formValue.descripcion || 'Descripción del artículo',
      formValue.precio || 0,
      formValue.imagen || '',
      formValue.stock || 0,
      formValue.id_estado_articulo || 1,
      this.getCategoriasSeleccionadas()
    );
  }

  // Obtiene el nombre del estado
  getEstadoNombre(id_estado: number): string {
    if (!this.estadoArticulo || this.estadoArticulo.length === 0) {
      return 'Sin estado';
    }
    const index = id_estado - 1;
    return this.estadoArticulo[index] || 'Desconocido';
  }

  // Verifica si el artículo está disponible
  isArticuloDisponible(): boolean {
    return this.articuloForm.get('stock')?.value > 0;
  }

  // Guarda el artículo
  guardarArticulo(): void {
    // Marcar todos los campos como touched para mostrar errores
    this.articuloForm.markAllAsTouched();

    // Validar que al menos una categoría esté seleccionada
    if (this.getCategoriasSeleccionadas().length === 0) {
      alert('Debe seleccionar al menos una categoría');
      return;
    }

    if (this.articuloForm.valid) {
      const formValue = this.articuloForm.value;
      
      const nuevoArticulo = new Articulo(
        0, 
        formValue.nombre,
        formValue.descripcion,
        formValue.precio,
        formValue.imagen,
        formValue.stock,
        formValue.id_estado_articulo,
        this.getCategoriasSeleccionadas()
      );

      console.log('Artículo a guardar:', nuevoArticulo);
      // RECORATORIO DE COMUNICAR CON LA API AQUI
      alert('Artículo guardado exitosamente!\n\n' + JSON.stringify(nuevoArticulo, null, 2));
      
      // Limpiar formulario
      this.limpiarFormulario();
    } else {
      alert('Por favor, complete todos los campos obligatorios correctamente');
    }
  }

  // Limpia el formulario
  limpiarFormulario(): void {
    this.articuloForm.reset({
      nombre: '',
      descripcion: '',
      precio: 0,
      stock: 0,
      imagen: '',
      id_estado_articulo: 1
    });
    
    // Limpiar checkboxes de categorías
    this.categoriasFormArray.controls.forEach(control => {
      control.setValue(false);
    });

    this.imagenFile = null;
    this.imagenPreviewUrl = '';
    this.actualizarPreview();
  }

  // Verifica si un campo es inválido y fue tocado
  isFieldInvalid(fieldName: string): boolean {
    const field = this.articuloForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  // Obtiene el mensaje de error de un campo
  getErrorMessage(fieldName: string): string {
    const field = this.articuloForm.get(fieldName);
    
    if (field?.hasError('required')) {
      return 'Este campo es obligatorio';
    }
    if (field?.hasError('minlength')) {
      const minLength = field.errors?.['minlength'].requiredLength;
      return `Debe tener al menos ${minLength} caracteres`;
    }
    if (field?.hasError('min')) {
      const min = field.errors?.['min'].min;
      return `El valor mínimo es ${min}`;
    }
    
    return '';
  }
}