# Pragma PowerUp 2025
## CrediYa
es una plataforma que busca digitalizar y optimizar la gestión de solicitudes de préstamos personales, eliminando la necesidad de procesos manuales y presenciales.

## Microservicio CrediYa-Autenticacion
### Casos de Uso
- Registrar usuarios en el sistema
- Agregar Autenticación al sistema

# Proyecto Base Implementando Clean Architecture

https://github.com/bancolombia/scaffold-clean-architecture

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

### Test coverage, flujo de ejecución
Ejecutar tests en todos los módulos y generar reportes individuales:
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
