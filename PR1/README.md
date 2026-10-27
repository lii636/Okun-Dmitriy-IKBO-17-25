# Эмулятор оболочки

Консольный REPL. Приглашение содержит имя VFS: `default> `.

## Функции и настройки

- `--vfs PATH` — путь к VFS. Файл не читается.
- `--script PATH` — стартовый скрипт. Команды идут по строкам, ошибочная строка пропускается, на экране видны ввод и вывод.
- `--help` — справка.
- `ls`, `cd` — печатают имя команды и аргументы.
- `conf-dump` — параметры `vfs`, `script`, `vfs-name`.
- `exit` — завершает сеанс или скрипт.
- `$ИМЯ` и `${ИМЯ}` раскрываются из окружения ОС.
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
```

```
default> conf-dump
vfs=
script=
vfs-name=default
default> exit
```

```bash
./gradlew run --no-daemon --console=plain --args="--vfs vfs/location"
./gradlew run --no-daemon --console=plain --args="--script fixtures/scripts/stage2_conf.txt"
./scripts/run_no_args.sh
./scripts/run_with_vfs.sh
./scripts/run_with_script.sh
```
