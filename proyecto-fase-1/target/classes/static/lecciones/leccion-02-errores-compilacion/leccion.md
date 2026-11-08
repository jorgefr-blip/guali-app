# Errores de Compilación

No todo el código compila a la primera. Esta lección busca que te
enfrentes a un error de compilación real y aprendas a **leer el mensaje
del compilador** en lugar de temerle.

## Instrucciones

1. Copia el código de referencia en el editor.
2. Elimina intencionalmente un punto y coma (`;`) de cualquier línea.
3. Presiona **Compilar** y observa el error que reporta el compilador
   en el Panel de Consola.

## Código de referencia

```
public class Contador {
    public static void main(String[] args) {
        int total = 0;
        for (int i = 1; i <= 5; i++) {
            total = total + i;
        }
        System.out.println("Total: " + total);
    }
}
```

El objetivo de esta lección se marca como completado cuando el
compilador reporta **al menos un error**.
