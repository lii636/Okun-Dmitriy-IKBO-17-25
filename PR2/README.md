# Визуализация графа зависимостей

CLI-инструмент для визуализации графа зависимостей менеджера пакетов
(вариант 18). На этапе 5 приложение строит граф (BFS), печатает его в
**PlantUML**, рендерит PNG внешним PlantUML JAR и открывает изображение.
Флаг `--ascii-tree` дополнительно печатает ASCII-дерево. Режим
`--reverse-deps` из этапа 4 сохранён без изменений.

## Параметры запуска

| Параметр | Значение |
| --- | --- |
| `--package NAME` | имя пакета / корень обхода (обязательный) |
| `--repo URL_OR_PATH` | URL репозитория или путь к файлу тестового графа (обязательный) |
| `--version VERSION` | версия корня для PyPI; в `--test-repo` обязателен, но игнорируется |
| `--test-repo` | читать описание графа из файла |
| `--reverse-deps` | вывести пакеты, которые зависят от `--package` (только с `--test-repo`) |
| `--ascii-tree` | дополнительно вывести зависимости ASCII-деревом |
| `--help` | краткая справка и выход с кодом 0 |

Без `--reverse-deps` stdout содержит PlantUML-текст графа; артефакты
пишутся в `out/graph_n/graph_n.puml` и `out/graph_n/graph_n.png`, где
`n` — следующий свободный индекс в `out/`. Путь к PNG дублируется в
stderr; изображение открывается через desktop API (если GUI недоступен —
печатается путь).

PlantUML JAR ищется в `PLANTUML_JAR` или `tools/plantuml.jar`:

```bash
./scripts/fetch_plantuml.sh
```

Формат тестового файла — список смежности, имена `[A-Z]+`:

```
A: B
B: C
C:
```

## Сборка и тесты

```bash
./gradlew assemble --console=plain
./gradlew test --console=plain
./scripts/fetch_plantuml.sh
./run.sh --package A --repo fixtures/repos/branch.txt --version 1 --test-repo --ascii-tree
```

## Примеры визуализации (три пакета)

### 1. Пакет `A` — diamond

```bash
./run.sh --package A --repo fixtures/repos/diamond.txt --version 1 --test-repo
```

```
@startuml
left to right direction
rectangle "A"
rectangle "B"
rectangle "C"
rectangle "D"
"A" --> "B"
"A" --> "C"
"B" --> "D"
"C" --> "D"
@enduml
```

### 2. Пакет `A` — branch (+ ASCII)

```bash
./run.sh --package A --repo fixtures/repos/branch.txt --version 1 --test-repo --ascii-tree
```

```
@startuml
left to right direction
rectangle "A"
rectangle "B"
rectangle "C"
"A" --> "B"
"A" --> "C"
@enduml
A
├── B
└── C
```

### 3. Пакет `requests` 2.31.0 (PyPI)

```bash
./run.sh --package requests --repo https://pypi.org/simple/ --version 2.31.0 --ascii-tree
```

Прямые зависимости корня (из JSON PyPI `requires_dist` без extras):
`charset-normalizer`, `idna`, `urllib3`, `certifi`. Транзитивные узлы
запрашиваются как «latest» версии соответствующего пакета.

Демо всех трёх сценариев:

```bash
./scripts/visualize_demo.sh
```

Прочие скрипты:

```bash
./scripts/reverse_deps_demo.sh
./scripts/bfs_test_repos.sh
./scripts/errors_demo.sh
```

## Сравнение со штатными средствами pip

Для `requests==2.31.0` удобно сравнить с:

```bash
pip index versions requests   # или pip install requests==2.31.0
pip show requests
pipdeptree -p requests        # если установлен pipdeptree
```

| Аспект | `depgraph` (этот проект) | pip / pipdeptree |
| --- | --- | --- |
| Источник метаданных | HTTP JSON PyPI (`…/pypi/{name}/{version}/json`) | установленное окружение / resolver |
| Версии транзитивных пакетов | корень — заданная версия; дети — latest | фиксируются resolver’ом под constraints |
| Маркеры PEP 508 / extras | extras отбрасываются парсером; маркеры среды не оцениваются полностью как в pip | учитываются окружение и extras |
| Визуализация | PlantUML + PNG + опциональное ASCII-дерево | `pip show` (список), `pipdeptree` (дерево установленных) |
| Тестовый репозиторий | учебный граф `A`…`Z` из файла | нет аналога |

Типичные расхождения:

1. Набор рёбер может отличаться, если pip отфильтровал зависимость по
   environment marker (`sys_platform`, `python_version`, …).
2. Глубина и версии транзитивных узлов у `depgraph` не совпадают с lock’ом
   конкретного venv: мы не решаем ranges, а берём latest metadata.
3. `pipdeptree` показывает уже установленный граф; `depgraph` строит граф
   по метаданным репозитория без установки пакетов.

Обратные зависимости (`--reverse-deps`) доступны только для `--test-repo`:
у публичного PyPI нет полного индекса «кто зависит от пакета» без
отдельного источника данных.
