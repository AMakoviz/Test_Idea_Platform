# Tickets

Консольная программа на Java, которая читает `tickets.json` и считает:

1) **Минимальное время полёта** между городами **Владивосток** и **Тель‑Авив** для каждого авиаперевозчика.  
2) **Разницу между средней ценой и медианой** для перелётов по этому направлению.

Вывод — в **текстовом виде**.

## Требования
- Java 17+
- Maven 3.8+
- Linux / macOS / Windows (запуск в Linux обязателен по ТЗ; ниже есть команды)

## Сборка
```bash
mvn -q -DskipTests package
```
Собирается «толстый» JAR с манифестом (через `maven-shade-plugin`).

## Запуск (Linux)
```bash
java -jar target/Test_Idea_Platform-1.0-SNAPSHOT.jar /path/to/tickets.json
```
Если путь содержит пробелы — возьмите его в кавычки:
```bash
java -jar target/Test_Idea_Platform-1.0-SNAPSHOT.jar "/home/user/Idea Platform/tickets.json"
```

## Формат входных данных
Ожидается JSON‑объект с массивом `tickets`, в котором у каждого билета есть поля:
```json
{
  "tickets": [
    {
      "origin": "VVO",
      "origin_name": "Владивосток",
      "destination": "TLV",
      "destination_name": "Тель-Авив",
      "departure_date": "12.05.18",
      "departure_time": "16:20",
      "arrival_date": "12.05.18",
      "arrival_time": "22:10",
      "carrier": "TK",
      "stops": 3,
      "price": 12400
    }
  ]
}
```
- Дата: `dd.MM.yy`  
- Время: `H:mm` (поддерживает и `9:40`, и `09:40`)  
- Направление фильтруется по IATA‑кодам: `origin = "VVO"`, `destination = "TLV"`.

## Формат вывода
Пример табличного вывода минимальной длительности и статистики цен:
```
Carrier   | Min Duration
----------+------------
SU        | 00d 06h 00m
S7        | 00d 06h 30m
TK        | 00d 05h 50m
BA        | 00d 08h 05m
Difference between average and median price is: 460.0
```

## Пример результатов для приложенного `tickets.json`
По данным из задания:

- Минимальные времена полёта:
  - **TK**: 5ч 50м  
  - **S7**: 6ч 30м  
  - **SU**: 6ч 00м  
  - **BA**: 8ч 05м
  - **Разница (avg − median)**: **460.00**

## Структура проекта
```
src/
  main/
    java/
      org/example/
        Main.java      // точка входа, чтение JSON, расчёты, вывод
        Ticket.java    // модель билета (поля соответствуют JSON, @JsonProperty)
    resources/
pom.xml
```
## Лицензия
Для учебных целей.

## Автор
Маковиз Анастасия
