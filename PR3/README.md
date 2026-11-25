# UVM — IR → машинный код

Ассемблер УВМ (вариант 18): разбор ASM → IR → упаковка 5-байтных команд и запись `.bin`.

## Функции и настройки

### Язык ассемблера

| Мнемоника | Синтаксис | Поля |
|-----------|-----------|------|
| `LOAD` | `LOAD reg, const` | A=14, B=reg, C=const |
| `READ` | `READ dst, base, off` | A=5, B=base, C=dst, D=off |
| `WRITE` | `WRITE src, addr` | A=6, B=src, C=addr |
| `ABS` | `ABS dst, addr` | A=15, B=dst, C=addr |

Комментарии: `#` / `;`. Один оператор на строку.

### Машинный код

Каждая команда — 5 байт, little-endian. Поля упакованы от младших битов (A с бита 0).

### CLI

```text
uvm assemble <input.asm> <output.bin> [--test]
uvm --help
```

| Аргумент | Описание |
|----------|----------|
| `input.asm` | исходник |
| `output.bin` | двоичный результат |
| `--test` | печать байтов каждой команды (`0x.., 0x.., …`) |

Всегда печатается размер выходного файла в байтах (первая строка stdout).

## Сборка и тесты

JDK 21+.

```bash
./gradlew test
./run.sh assemble fixtures/asm/spec_tests.asm out.bin --test
```

PowerShell:

```powershell
.\gradlew.bat test --console=plain
.\gradlew.bat installDist --console=plain
.\build\install\uvm\bin\uvm.bat assemble fixtures\asm\spec_tests.asm out.bin --test
```

## Примеры

Фикстура: `fixtures/asm/spec_tests.asm` (все 4 теста спецификации).  
Эталон байтов: `fixtures/expected/spec_bytes.txt`.

```text
20
0x8E, 0x50, 0x08, 0x00, 0x00
0x85, 0x69, 0xCF, 0x07, 0x00
0x76, 0xA8, 0x0E, 0x00, 0x00
0x1F, 0x6A, 0x0E, 0x00, 0x00
```
