# Эмулятор оболочки

Консольный REPL. Приглашение содержит имя VFS: `default> `.

## Функции и настройки

- `ls`, `cd` — печатают имя команды и аргументы.
- `exit` — завершает сеанс.
- `$ИМЯ` и `${ИМЯ}` раскрываются из окружения ОС.
- Ошибка печатается в stderr, ввод продолжается.

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
default> ls
ls
default> cd /tmp
cd: /tmp
default> exit
```
