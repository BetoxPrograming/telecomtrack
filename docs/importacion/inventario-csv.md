# Importación inicial de inventario por CSV

La pantalla está disponible en `/inventario/importar` para Administrador y Bodeguero.

## Herramientas

Encabezado obligatorio:

```text
codigo,nombre,categoria,descripcion,valor_unitario,ubicacion
```

- `codigo`: obligatorio y único.
- `nombre`: obligatorio.
- `categoria`: obligatorio.
- `descripcion`: opcional.
- `valor_unitario`: obligatorio y debe ser un número no negativo.
- `ubicacion`: debe coincidir con una ubicación ya registrada.
- Las herramientas importadas ingresan con estado `Disponible`.

## Materiales

Encabezado obligatorio:

```text
codigo,nombre,descripcion,unidad_medida,stock_actual,stock_minimo,valor_unitario,categoria,proveedor,ubicacion
```

- `codigo`: obligatorio y único.
- `nombre` y `unidad_medida`: obligatorios.
- `stock_actual`, `stock_minimo` y `valor_unitario`: deben ser números no negativos.
- `categoria`, `proveedor` y `ubicacion`: deben coincidir con registros existentes.

## Comportamiento ante errores

El archivo se valida antes de procesar sus datos. Si el encabezado es incorrecto no se importa ninguna fila. Una vez validado el encabezado, cada fila se procesa de forma independiente: las filas válidas se guardan y las filas inválidas se reportan indicando su número y el problema encontrado.

El lector acepta archivos CSV separados por coma o punto y coma y reconoce campos entre comillas.
