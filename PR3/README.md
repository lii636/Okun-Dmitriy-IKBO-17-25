# UVM — тестовая задача

Ассемблер и интерпретатор УВМ (вариант 18).  
Задача: поэлементный `abs()` над вектором длины 10; результат записывается в исходный вектор (адреса `0..9`).

Требуется JDK 21. Проект использует Gradle Wrapper, отдельная установка Gradle не нужна.

## Функции и настройки

### Язык ассемблера

| Мнемоника | Синтаксис | Смысл |
|-----------|-----------|--------|
| `LOAD` | `LOAD reg, const` | константа (signed 16-bit) → регистр |
| `READ` | `READ dst, base, off` | mem[reg[base]+off] → reg[dst] |
| `WRITE` | `WRITE src, addr` | reg[src] → mem[addr] |
| `ABS` | `ABS dst, addr` | abs(mem[addr]) → reg[dst] |

Регистр задаётся числом `0..127`. Константа `LOAD` — signed 16-bit (`-32768..32767`).
Смещение `READ` находится в диапазоне `0..16383`, абсолютный адрес `WRITE/ABS` —
`0..8388607`. Память команд и память данных разделены.

Строка содержит одну команду. Аргументы разделяются запятыми. Пустые строки
игнорируются; `#` и `;` начинают комментарий до конца строки.

Каждая команда кодируется пятью байтами в little-endian. Неиспользуемые старшие
биты должны быть нулевыми. Повреждённый бинарный файл завершается с кодом `1`
и понятным сообщением в stderr.

### CLI

```text
uvm assemble <input.asm> <output.bin> [--test]
uvm run <program.bin> <dump.xml> <from>-<to>
uvm --help
```

`assemble` всегда печатает размер созданного бинарного файла в байтах. С флагом
`--test` после размера выводятся байты каждой команды в формате
`0x8E, 0x50, 0x08, 0x00, 0x00`.

`run` исполняет бинарный файл и сохраняет включительный диапазон ячеек памяти
`from-to` в XML. После успеха печатается путь и диапазон созданного дампа.

## Сборка и тесты

```bash
./gradlew test
./gradlew installDist
./scripts/vector_abs_all.sh
```

PowerShell (один пример):

```powershell
.\gradlew.bat test --console=plain
.\gradlew.bat installDist --console=plain
.\build\install\uvm\bin\uvm.bat assemble fixtures\asm\vector_abs_ex1.asm ex1.bin
.\build\install\uvm\bin\uvm.bat run ex1.bin ex1.xml 0-9
```

Проверка команд из спецификации:

```bash
./scripts/assemble_test.sh
```

## Примеры

Каждый пример: инициализация вектора длины 10 → `ABS` + `WRITE` обратно в те же ячейки → дамп `0-9`.

| Пример | Исходный вектор | Результат | Файлы |
|--------|-----------------|-----------|--------|
| 1 | `-5,3,-1,0,8,-12,7,-4,2,-9` | `5,3,1,0,8,12,7,4,2,9` | `fixtures/asm/vector_abs_ex1.asm`, `fixtures/expected/vector_abs_ex1_dump.xml` |
| 2 | `-10,-20,…,-100` | `10,20,…,100` | `fixtures/asm/vector_abs_ex2.asm`, `fixtures/expected/vector_abs_ex2_dump.xml` |
| 3 | `0,1,-2,3,-4,5,-6,7,-8,9` | `0..9` | `fixtures/asm/vector_abs_ex3.asm`, `fixtures/expected/vector_abs_ex3_dump.xml` |

Демо всех трёх: `scripts/vector_abs_all.sh`.
