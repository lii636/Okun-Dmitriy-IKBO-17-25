# Эмулятор оболочки

Консольный REPL. Команды работают с VFS в памяти, XML-файл не изменяется.
Приглашение содержит имя VFS.

## Функции и настройки

- `--vfs PATH` — XML-файл VFS. Если путь не задан, корень пустой и имя `default`.
- Имя VFS: атрибут `name` у корня, иначе имя файла, иначе `default`.
- `--script PATH` — стартовый скрипт. Команды идут по строкам, ошибочная строка пропускается, на экране видны ввод и вывод.
- `ls` — содержимое каталога или имя файла.
- `cd` — переход по пути, `.` и `..`. Без аргумента — в корень `/`.
- `conf-dump` — параметры `vfs`, `script`, `vfs-name`.
- `exit` — завершает сеанс или скрипт.
- `$ИМЯ` и `${ИМЯ}` раскрываются из окружения ОС.
- Двоичное содержимое файла: `encoding="base64"`.
- Ошибка загрузки VFS (файл не найден, неверный XML) печатается в stderr, код выхода 1.
- При запуске параметры печатаются в stderr.

## Сборка и тесты

```bash
./gradlew assemble --console=plain
./gradlew test --console=plain
./run.sh
```

## Примеры

```bash
./run.sh
./run.sh --vfs fixtures/vfs/minimal.xml
./run.sh --vfs fixtures/vfs/deep-3levels.xml --script fixtures/scripts/stage3_all.txt
./scripts/run_no_args.sh
./scripts/run_with_vfs.sh
./scripts/run_with_script.sh
./scripts/run_vfs_variants.sh
./scripts/run_vfs_errors.sh
```

Варианты VFS: `fixtures/vfs/minimal.xml`, `multi-file.xml`, `deep-3levels.xml`.
