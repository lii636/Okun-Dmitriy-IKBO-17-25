# UVM — интерпретатор + память

Ассемблер и интерпретатор УВМ (вариант 18). Выполнение LOAD/READ/WRITE, раздельная память команд/данных и XML-дамп.

## Функции и настройки

### Язык ассемблера

| Мнемоника | Синтаксис | Поля |
|-----------|-----------|------|
| `LOAD` | `LOAD reg, const` | A=14, B=reg, C=const |
| `READ` | `READ dst, base, off` | A=5, B=base, C=dst, D=off |
| `WRITE` | `WRITE src, addr` | A=6, B=src, C=addr |
| `ABS` | `ABS dst, addr` | A=15, B=dst, C=addr (разбор есть, выполнение — позже) |

### Память

- память команд — байты загруженного `.bin`;
- память данных — отдельный массив ячеек;
- регистры — отдельный массив.

### CLI

```text
uvm assemble <input.asm> <output.bin> [--test]
uvm run <program.bin> <dump.xml> <from>-<to>
uvm --help
```

`run` исполняет программу и пишет XML-дамп диапазона `[from, to]` данных.

Формат дампа:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<memory from="100" to="102">
  <cell address="100" value="10"/>
  ...
</memory>
```

## Сборка и тесты

JDK 21+.

```bash
./gradlew test
./run.sh assemble fixtures/asm/copy_array.asm /tmp/copy.bin
./run.sh run /tmp/copy.bin /tmp/dump.xml 100-102
```

PowerShell:

```powershell
.\gradlew.bat test --console=plain
.\gradlew.bat installDist --console=plain
.\build\install\uvm\bin\uvm.bat assemble fixtures\asm\copy_array.asm copy.bin
.\build\install\uvm\bin\uvm.bat run copy.bin dump.xml 100-102
```

Демо: `scripts/copy_array_demo.sh`.

## Примеры

Копирование массива `[10,20,30]` с адресов `0..2` на `100..102`:

- исходник: `fixtures/asm/copy_array.asm`
- эталон дампа: `fixtures/expected/copy_array_dump.xml`
