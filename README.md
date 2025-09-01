# Pragma PowerUp 2025
## CrediYa
es una plataforma que busca digitalizar y optimizar la gesti�n de solicitudes de pr�stamos personales, eliminando la necesidad de procesos manuales y presenciales.

## Microservicio CrediYa-Autenticacion
### Casos de Uso
- Registrar usuarios en el sistema
- Agregar Autenticaci�n al sistema

# Proyecto Base Implementando Clean Architecture

https://github.com/bancolombia/scaffold-clean-architecture

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

### Test coverage, flujo de ejecuci�n
Ejecutar tests en todos los m�dulos y generar reportes individuales:
```
./gradlew clean test jacocoTestReport
```

Generar el reporte consolidado:
```
./gradlew jacocoMergedReport
```

? Archivo resultante:
```
build/reports/jacocoMergedReport/jacocoMergedReport.xml
build/reports/jacocoMergedReport/html/index.html
```
