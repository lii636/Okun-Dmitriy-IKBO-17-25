# UVM — ассемблер → IR

CLI-ассемблер учебной виртуальной машины (УВМ), вариант 18.  
Разбор языка ассемблера и перевод в промежуточное представление (IR). Машинный код пока не пишется.

## Функции и настройки

### Язык ассемблера

Один оператор на строку: `мнемоника arg, arg, …`.  
Комментарии: `#` или `;` до конца строки. Пустые строки игнорируются.  
Аргументы — неотрицательные целые; пробелы вокруг запятых допустимы.

| Мнемоника | Синтаксис | Поля IR | Смысл |
|-----------|-----------|---------|--------|
| `LOAD` | `LOAD reg, const` | A=14, B=reg, C=const | константа → регистр |
| `READ` | `READ dst, base, off` | A=5, B=base, C=dst, D=off | mem[reg[base]+off] → reg[dst] |
| `WRITE` | `WRITE src, addr` | A=6, B=src, C=addr | reg[src] → mem[addr] |
| `ABS` | `ABS dst, addr` | A=15, B=dst, C=addr | abs(mem[addr]) → reg[dst] |

Диапазоны полей ограничены шириной битов из спецификации УВМ.

### CLI

```text
uvm assemble <input.asm> <output.bin> [--test]
uvm --help
```

| Аргумент | Описание |
|----------|----------|
| `input.asm` | исходный текст на языке ассемблера |
| `output.bin` | путь к двоичному результату (принимается; запись бинарника — позже) |
| `--test` | печать IR в формате полей спецификации на stdout |

Пример вывода `--test`:

```text
LOAD A=14 B=8 C=266
READ A=5 B=24 C=109 D=499
WRITE A=6 B=7 C=469
ABS A=15 B=33 C=461
```

### Пакеты

- `uvm.configuration` — CLI / immutable `Config`
- `uvm.asm` — парсер языка
- `uvm.ir` — records инструкций
- `uvm.application` — оркестрация assemble

## Сборка и тесты

Требуется JDK 21+.

```bash
./gradlew test
./gradlew installDist
./run.sh assemble fixtures/asm/spec_tests.asm out.bin --test
```

На Windows (PowerShell), из каталога этапа:

```powershell
.\gradlew.bat test --console=plain
.\gradlew.bat installDist --console=plain
.\build\install\uvm\bin\uvm.bat assemble fixtures\asm\spec_tests.asm out.bin --test
```

Демо-скрипт (Git Bash / WSL): `scripts/assemble_test.sh`.

## Примеры

Фикстура со всеми тестами спецификации: `fixtures/asm/spec_tests.asm`.  
Эталон IR: `fixtures/expected/spec_ir.txt`.

```bash
./run.sh assemble fixtures/asm/spec_tests.asm /tmp/out.bin --test
```

Ожидаемый stdout совпадает с `fixtures/expected/spec_ir.txt`.
