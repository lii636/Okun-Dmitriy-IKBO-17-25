# UVM — АЛУ: abs

Интерпретатор УВМ (вариант 18) с выполнением `ABS`. Константа `LOAD` — знаковая 16-битная (для отрицательных операндов `abs`).

## Функции и настройки

### Язык ассемблера

| Мнемоника | Синтаксис | Поля |
|-----------|-----------|------|
| `LOAD` | `LOAD reg, const` | A=14, B=reg, C=const (signed 16-bit) |
| `READ` | `READ dst, base, off` | A=5, B=base, C=dst, D=off |
| `WRITE` | `WRITE src, addr` | A=6, B=src, C=addr |
| `ABS` | `ABS dst, addr` | A=15, B=dst, C=addr → `reg[dst] = abs(mem[addr])` |

### CLI

```text
uvm assemble <input.asm> <output.bin> [--test]
uvm run <program.bin> <dump.xml> <from>-<to>
uvm --help
```

## Сборка и тесты

```bash
./gradlew test
./run.sh assemble fixtures/asm/abs_demo.asm /tmp/abs.bin
./run.sh run /tmp/abs.bin /tmp/dump.xml 10-12
```

PowerShell:

```powershell
.\gradlew.bat test --console=plain
.\gradlew.bat installDist --console=plain
.\build\install\uvm\bin\uvm.bat assemble fixtures\asm\abs_demo.asm abs.bin
.\build\install\uvm\bin\uvm.bat run abs.bin dump.xml 10-12
```

Демо: `scripts/abs_demo.sh`.

## Примеры

`fixtures/asm/abs_demo.asm` — значения `-7`, `0`, `5` → абсолютные в ячейках `10..12`.  
Эталон: `fixtures/expected/abs_demo_dump.xml` (`7`, `0`, `5`).

Регрессия копирования массива: `fixtures/asm/copy_array.asm`.
