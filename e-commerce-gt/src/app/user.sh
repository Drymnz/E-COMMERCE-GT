#!/bin/bash

# Script de migración a Arquitectura en Capas Simplificada
# Ejecutar desde la raíz del proyecto Angular

echo "🚀 Iniciando migración a arquitectura en capas..."

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Directorio base
BASE_DIR="src/app"

# ============================================
# 1. CREAR ESTRUCTURA DE CARPETAS
# ============================================
echo -e "${BLUE}📁 Creando estructura de carpetas...${NC}"

mkdir -p "$BASE_DIR/core/models"
mkdir -p "$BASE_DIR/core/guards"
mkdir -p "$BASE_DIR/core/interceptors"

mkdir -p "$BASE_DIR/features/auth/pages/login"
mkdir -p "$BASE_DIR/features/auth/pages/register"
mkdir -p "$BASE_DIR/features/auth/services"

mkdir -p "$BASE_DIR/features/products/pages/product-list"
mkdir -p "$BASE_DIR/features/products/pages/product-detail"
mkdir -p "$BASE_DIR/features/products/pages/manage-products"
mkdir -p "$BASE_DIR/features/products/components/product-card"
mkdir -p "$BASE_DIR/features/products/components/register-article"
mkdir -p "$BASE_DIR/features/products/services"

mkdir -p "$BASE_DIR/features/shopping/pages/cart"
mkdir -p "$BASE_DIR/features/shopping/pages/checkout"
mkdir -p "$BASE_DIR/features/shopping/pages/order-tracking"
mkdir -p "$BASE_DIR/features/shopping/pages/card-management"
mkdir -p "$BASE_DIR/features/shopping/services"

mkdir -p "$BASE_DIR/features/admin/pages/dashboard"
mkdir -p "$BASE_DIR/features/admin/pages/users"
mkdir -p "$BASE_DIR/features/admin/pages/reports"
mkdir -p "$BASE_DIR/features/admin/pages/sales"
mkdir -p "$BASE_DIR/features/admin/pages/notifications"
mkdir -p "$BASE_DIR/features/admin/pages/employee-history"
mkdir -p "$BASE_DIR/features/admin/services"

mkdir -p "$BASE_DIR/features/moderation/pages/panel"
mkdir -p "$BASE_DIR/features/moderation/pages/users-list"
mkdir -p "$BASE_DIR/features/moderation/pages/sanctions"
mkdir -p "$BASE_DIR/features/moderation/services"

mkdir -p "$BASE_DIR/features/logistics/pages/order-management"
mkdir -p "$BASE_DIR/features/logistics/services"

mkdir -p "$BASE_DIR/shared/components/navbar"
mkdir -p "$BASE_DIR/shared/components/comments"
mkdir -p "$BASE_DIR/shared/components/modals/select-option"
mkdir -p "$BASE_DIR/shared/components/modals/notify-confirm"
mkdir -p "$BASE_DIR/shared/components/modals/register-card"
mkdir -p "$BASE_DIR/shared/components/ratings-comments"
mkdir -p "$BASE_DIR/shared/directives"

echo -e "${GREEN}✅ Estructura creada${NC}"

# ============================================
# 2. MOVER ENTITIES A CORE/MODELS
# ============================================
echo -e "${BLUE}📦 Moviendo entities a core/models...${NC}"

if [ -d "$BASE_DIR/entities" ]; then
    mv "$BASE_DIR/entities"/*.ts "$BASE_DIR/core/models/" 2>/dev/null
    echo -e "${GREEN}✅ Entities movidas${NC}"
fi

# ============================================
# 3. MOVER GUARDS A CORE
# ============================================
echo -e "${BLUE}🔒 Moviendo guards...${NC}"

if [ -d "$BASE_DIR/guards" ]; then
    mv "$BASE_DIR/guards"/*.ts "$BASE_DIR/core/guards/" 2>/dev/null
    echo -e "${GREEN}✅ Guards movidos${NC}"
fi

# ============================================
# 4. MOVER DIRECTIVES A SHARED
# ============================================
echo -e "${BLUE}📐 Moviendo directives...${NC}"

if [ -d "$BASE_DIR/directives" ]; then
    mv "$BASE_DIR/directives"/*.ts "$BASE_DIR/shared/directives/" 2>/dev/null
    echo -e "${GREEN}✅ Directives movidas${NC}"
fi

# ============================================
# 5. FEATURE: AUTH
# ============================================
echo -e "${BLUE}🔑 Moviendo feature AUTH...${NC}"

# Login
if [ -d "$BASE_DIR/pags/general/login" ]; then
    mv "$BASE_DIR/pags/general/login"/* "$BASE_DIR/features/auth/pages/login/" 2>/dev/null
fi

# Register
if [ -d "$BASE_DIR/pags/general/registro" ]; then
    mv "$BASE_DIR/pags/general/registro"/* "$BASE_DIR/features/auth/pages/register/" 2>/dev/null
fi

# Auth Service
if [ -f "$BASE_DIR/service/local/auth.service.ts" ]; then
    mv "$BASE_DIR/service/local/auth.service.ts" "$BASE_DIR/features/auth/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/local/auth.service.spec.ts" ]; then
    mv "$BASE_DIR/service/local/auth.service.spec.ts" "$BASE_DIR/features/auth/services/" 2>/dev/null
fi

echo -e "${GREEN}✅ Feature AUTH migrado${NC}"

# ============================================
# 6. FEATURE: PRODUCTS
# ============================================
echo -e "${BLUE}📦 Moviendo feature PRODUCTS...${NC}"

# Product List (home + article + article-view)
if [ -d "$BASE_DIR/pags/general/home" ]; then
    mv "$BASE_DIR/pags/general/home"/* "$BASE_DIR/features/products/pages/product-list/" 2>/dev/null
fi
if [ -d "$BASE_DIR/pags/general/article" ]; then
    mv "$BASE_DIR/pags/general/article"/* "$BASE_DIR/features/products/pages/product-list/" 2>/dev/null
fi
if [ -d "$BASE_DIR/pags/general/article-view" ]; then
    mv "$BASE_DIR/pags/general/article-view"/* "$BASE_DIR/features/products/pages/product-list/" 2>/dev/null
fi

# Product Detail
if [ -d "$BASE_DIR/pags/general/see-product" ]; then
    mv "$BASE_DIR/pags/general/see-product"/* "$BASE_DIR/features/products/pages/product-detail/" 2>/dev/null
fi

# Manage Products
if [ -d "$BASE_DIR/pags/customer/manage-products-sale" ]; then
    mv "$BASE_DIR/pags/customer/manage-products-sale"/* "$BASE_DIR/features/products/pages/manage-products/" 2>/dev/null
fi

# Product Card Component
if [ -d "$BASE_DIR/pags/customer/manage-products-sale/product-card-manage" ]; then
    mv "$BASE_DIR/pags/customer/manage-products-sale/product-card-manage"/* "$BASE_DIR/features/products/components/product-card/" 2>/dev/null
fi

# Register Article Component
if [ -d "$BASE_DIR/pags/customer/manage-products-sale/register-article" ]; then
    mv "$BASE_DIR/pags/customer/manage-products-sale/register-article"/* "$BASE_DIR/features/products/components/register-article/" 2>/dev/null
fi

# Services
if [ -f "$BASE_DIR/service/api/article.service.ts" ]; then
    mv "$BASE_DIR/service/api/article.service.ts" "$BASE_DIR/features/products/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/article.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/article.service.spec.ts" "$BASE_DIR/features/products/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/comment.service.ts" ]; then
    mv "$BASE_DIR/service/api/comment.service.ts" "$BASE_DIR/features/products/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/comment.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/comment.service.spec.ts" "$BASE_DIR/features/products/services/" 2>/dev/null
fi

echo -e "${GREEN}✅ Feature PRODUCTS migrado${NC}"

# ============================================
# 7. FEATURE: SHOPPING
# ============================================
echo -e "${BLUE}🛒 Moviendo feature SHOPPING...${NC}"

# Cart
if [ -d "$BASE_DIR/pags/customer/manage-shopping-cart" ]; then
    mv "$BASE_DIR/pags/customer/manage-shopping-cart"/* "$BASE_DIR/features/shopping/pages/cart/" 2>/dev/null
fi

# Card Management (dentro de manage-shopping-cart)
if [ -d "$BASE_DIR/pags/customer/manage-shopping-cart/gestion-tarjetas" ]; then
    mv "$BASE_DIR/pags/customer/manage-shopping-cart/gestion-tarjetas"/* "$BASE_DIR/features/shopping/pages/card-management/" 2>/dev/null
fi

# Checkout (pago)
if [ -d "$BASE_DIR/pags/general/pago" ]; then
    mv "$BASE_DIR/pags/general/pago"/* "$BASE_DIR/features/shopping/pages/checkout/" 2>/dev/null
fi

# Order Tracking
if [ -d "$BASE_DIR/pags/customer/order-tracking" ]; then
    mv "$BASE_DIR/pags/customer/order-tracking"/* "$BASE_DIR/features/shopping/pages/order-tracking/" 2>/dev/null
fi

# Services
if [ -f "$BASE_DIR/service/local/carrito.service.ts" ]; then
    mv "$BASE_DIR/service/local/carrito.service.ts" "$BASE_DIR/features/shopping/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/local/carrito.service.spec.ts" ]; then
    mv "$BASE_DIR/service/local/carrito.service.spec.ts" "$BASE_DIR/features/shopping/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/pedido.service.ts" ]; then
    mv "$BASE_DIR/service/api/pedido.service.ts" "$BASE_DIR/features/shopping/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/pedido.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/pedido.service.spec.ts" "$BASE_DIR/features/shopping/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/card.service.ts" ]; then
    mv "$BASE_DIR/service/api/card.service.ts" "$BASE_DIR/features/shopping/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/card.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/card.service.spec.ts" "$BASE_DIR/features/shopping/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/compra.service.ts" ]; then
    mv "$BASE_DIR/service/api/compra.service.ts" "$BASE_DIR/features/shopping/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/compra.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/compra.service.spec.ts" "$BASE_DIR/features/shopping/services/" 2>/dev/null
fi

echo -e "${GREEN}✅ Feature SHOPPING migrado${NC}"

# ============================================
# 8. FEATURE: ADMIN
# ============================================
echo -e "${BLUE}👨‍💼 Moviendo feature ADMIN...${NC}"

# Dashboard
if [ -d "$BASE_DIR/pags/admin/admin.component.ts" ]; then
    cp "$BASE_DIR/pags/admin/admin.component."* "$BASE_DIR/features/admin/pages/dashboard/" 2>/dev/null
fi

# Users
if [ -d "$BASE_DIR/pags/admin/edicion-usuario" ]; then
    mv "$BASE_DIR/pags/admin/edicion-usuario"/* "$BASE_DIR/features/admin/pages/users/" 2>/dev/null
fi
if [ -d "$BASE_DIR/pags/admin/registro-usuario" ]; then
    mv "$BASE_DIR/pags/admin/registro-usuario"/* "$BASE_DIR/features/admin/pages/users/" 2>/dev/null
fi

# Reports
if [ -d "$BASE_DIR/pags/admin/report" ]; then
    mv "$BASE_DIR/pags/admin/report"/* "$BASE_DIR/features/admin/pages/reports/" 2>/dev/null
fi

# Sales
if [ -d "$BASE_DIR/pags/admin/ventas" ]; then
    mv "$BASE_DIR/pags/admin/ventas"/* "$BASE_DIR/features/admin/pages/sales/" 2>/dev/null
fi

# Notifications
if [ -d "$BASE_DIR/pags/admin/notificacion" ]; then
    mv "$BASE_DIR/pags/admin/notificacion"/* "$BASE_DIR/features/admin/pages/notifications/" 2>/dev/null
fi

# Employee History
if [ -d "$BASE_DIR/pags/admin/historial-empleados" ]; then
    mv "$BASE_DIR/pags/admin/historial-empleados"/* "$BASE_DIR/features/admin/pages/employee-history/" 2>/dev/null
fi

# Services
if [ -f "$BASE_DIR/service/api/user-service.service.ts" ]; then
    mv "$BASE_DIR/service/api/user-service.service.ts" "$BASE_DIR/features/admin/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/user-service.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/user-service.service.spec.ts" "$BASE_DIR/features/admin/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/report.service.ts" ]; then
    mv "$BASE_DIR/service/api/report.service.ts" "$BASE_DIR/features/admin/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/report.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/report.service.spec.ts" "$BASE_DIR/features/admin/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/notificacion.service.ts" ]; then
    mv "$BASE_DIR/service/api/notificacion.service.ts" "$BASE_DIR/features/admin/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/notificacion.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/notificacion.service.spec.ts" "$BASE_DIR/features/admin/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/venta.service.ts" ]; then
    mv "$BASE_DIR/service/api/venta.service.ts" "$BASE_DIR/features/admin/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/venta.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/venta.service.spec.ts" "$BASE_DIR/features/admin/services/" 2>/dev/null
fi

echo -e "${GREEN}✅ Feature ADMIN migrado${NC}"

# ============================================
# 9. FEATURE: MODERATION
# ============================================
echo -e "${BLUE}⚖️ Moviendo feature MODERATION...${NC}"

# Panel
if [ -d "$BASE_DIR/pags/moderator/panel-moderacion" ]; then
    mv "$BASE_DIR/pags/moderator/panel-moderacion"/* "$BASE_DIR/features/moderation/pages/panel/" 2>/dev/null
fi

# Users List
if [ -d "$BASE_DIR/pags/moderator/usuarios-list" ]; then
    mv "$BASE_DIR/pags/moderator/usuarios-list"/* "$BASE_DIR/features/moderation/pages/users-list/" 2>/dev/null
fi

# Sanctions
if [ -d "$BASE_DIR/pags/moderator/list-sanctions" ]; then
    mv "$BASE_DIR/pags/moderator/list-sanctions"/* "$BASE_DIR/features/moderation/pages/sanctions/" 2>/dev/null
fi

# Services
if [ -f "$BASE_DIR/service/api/moderacion.service.ts" ]; then
    mv "$BASE_DIR/service/api/moderacion.service.ts" "$BASE_DIR/features/moderation/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/moderacion.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/moderacion.service.spec.ts" "$BASE_DIR/features/moderation/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/sancion.service.ts" ]; then
    mv "$BASE_DIR/service/api/sancion.service.ts" "$BASE_DIR/features/moderation/services/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/sancion.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/sancion.service.spec.ts" "$BASE_DIR/features/moderation/services/" 2>/dev/null
fi

echo -e "${GREEN}✅ Feature MODERATION migrado${NC}"

# ============================================
# 10. FEATURE: LOGISTICS
# ============================================
echo -e "${BLUE}🚚 Moviendo feature LOGISTICS...${NC}"

if [ -d "$BASE_DIR/pags/logistics/order-management" ]; then
    mv "$BASE_DIR/pags/logistics/order-management"/* "$BASE_DIR/features/logistics/pages/order-management/" 2>/dev/null
fi

echo -e "${GREEN}✅ Feature LOGISTICS migrado${NC}"

# ============================================
# 11. SHARED COMPONENTS
# ============================================
echo -e "${BLUE}🔧 Moviendo componentes compartidos...${NC}"

# Navbar
if [ -d "$BASE_DIR/pags/general/navbar" ]; then
    mv "$BASE_DIR/pags/general/navbar"/* "$BASE_DIR/shared/components/navbar/" 2>/dev/null
fi

# Comments
if [ -d "$BASE_DIR/pags/general/comentarios" ]; then
    mv "$BASE_DIR/pags/general/comentarios"/* "$BASE_DIR/shared/components/comments/" 2>/dev/null
fi

# Modals
if [ -d "$BASE_DIR/pags/general/modal-select-option" ]; then
    mv "$BASE_DIR/pags/general/modal-select-option"/* "$BASE_DIR/shared/components/modals/select-option/" 2>/dev/null
fi
if [ -d "$BASE_DIR/pags/general/notify-confirm" ]; then
    mv "$BASE_DIR/pags/general/notify-confirm"/* "$BASE_DIR/shared/components/modals/notify-confirm/" 2>/dev/null
fi
if [ -d "$BASE_DIR/pags/general/registrar-tarjeta" ]; then
    mv "$BASE_DIR/pags/general/registrar-tarjeta"/* "$BASE_DIR/shared/components/modals/register-card/" 2>/dev/null
fi

# Ratings and Comments
if [ -d "$BASE_DIR/pags/customer/manage-ratings-comments" ]; then
    mv "$BASE_DIR/pags/customer/manage-ratings-comments"/* "$BASE_DIR/shared/components/ratings-comments/" 2>/dev/null
fi

# List Constants Service (podría ir a core o shared)
if [ -f "$BASE_DIR/service/api/list-constant.service.ts" ]; then
    mv "$BASE_DIR/service/api/list-constant.service.ts" "$BASE_DIR/core/" 2>/dev/null
fi
if [ -f "$BASE_DIR/service/api/list-constant.service.spec.ts" ]; then
    mv "$BASE_DIR/service/api/list-constant.service.spec.ts" "$BASE_DIR/core/" 2>/dev/null
fi

echo -e "${GREEN}✅ Componentes compartidos migrados${NC}"

# ============================================
# 12. LIMPIAR CARPETAS VACÍAS
# ============================================
echo -e "${BLUE}🧹 Limpiando carpetas vacías...${NC}"

find "$BASE_DIR/pags" -type d -empty -delete 2>/dev/null
find "$BASE_DIR/service" -type d -empty -delete 2>/dev/null

# Eliminar carpetas antiguas si están completamente vacías
if [ -d "$BASE_DIR/pags" ] && [ -z "$(ls -A $BASE_DIR/pags)" ]; then
    rmdir "$BASE_DIR/pags"
    echo -e "${GREEN}✅ Carpeta 'pags' eliminada${NC}"
fi

if [ -d "$BASE_DIR/service" ] && [ -z "$(ls -A $BASE_DIR/service)" ]; then
    rmdir "$BASE_DIR/service"
    echo -e "${GREEN}✅ Carpeta 'service' eliminada${NC}"
fi

if [ -d "$BASE_DIR/entities" ] && [ -z "$(ls -A $BASE_DIR/entities)" ]; then
    rmdir "$BASE_DIR/entities"
    echo -e "${GREEN}✅ Carpeta 'entities' eliminada${NC}"
fi

if [ -d "$BASE_DIR/guards" ] && [ -z "$(ls -A $BASE_DIR/guards)" ]; then
    rmdir "$BASE_DIR/guards"
    echo -e "${GREEN}✅ Carpeta 'guards' eliminada${NC}"
fi

if [ -d "$BASE_DIR/directives" ] && [ -z "$(ls -A $BASE_DIR/directives)" ]; then
    rmdir "$BASE_DIR/directives"
    echo -e "${GREEN}✅ Carpeta 'directives' eliminada${NC}"
fi

echo -e "${GREEN}✅ Limpieza completada${NC}"

# ============================================
# 13. RESUMEN
# ============================================
echo ""
echo -e "${YELLOW}════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ MIGRACIÓN COMPLETADA EXITOSAMENTE${NC}"
echo -e "${YELLOW}════════════════════════════════════════${NC}"
echo ""
echo -e "${BLUE}📊 Estructura final:${NC}"
echo ""
tree -L 3 "$BASE_DIR" -I "*.spec.ts" 2>/dev/null || find "$BASE_DIR" -maxdepth 3 -type d
echo ""
echo -e "${YELLOW}⚠️  IMPORTANTE:${NC}"
echo "1. Verifica que todos los imports estén actualizados"
echo "2. Ejecuta: ${GREEN}ng build${NC} para verificar errores"
echo "3. Actualiza las rutas en ${GREEN}app.routes.ts${NC}"
echo "4. Revisa los servicios en ${GREEN}app.config.ts${NC}"
echo ""
echo -e "${BLUE}🔧 Siguiente paso:${NC}"
echo "Ejecuta el script de actualización de imports:"
echo "${GREEN}./update-imports.sh${NC}"
echo ""