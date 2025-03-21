## Roaming CDR Service
Микросервис для генерации CDR записей и формирования UDR отчетов.

### Описание задания
Микросервис эмулирует работу коммутатора, генерируя CDR записи о звонках абонентов. На основе этих данных формируются UDR отчеты, которые используются для выставления счетов.

#### Основные функции
1. Генерация CDR записей за год.

2. Формирование UDR отчетов по абонентам.

3. Генерация CDR-отчетов в формате CSV.

### Технологии
- Язык: Java 17.
- Фреймворк: Spring Boot.
- База данных: H2 (встроенная).
- Библиотеки:
	- Spring Data JPA (для работы с БД).
	- Lombok (для упрощения кода).
	- Spring Web (для REST API).
	
### Запуск приложения
#### Требования
- Установленный JDK 17.
- Maven (для сборки проекта).

#### Шаги для запуска
1. Клонируйте репозиторий:

`git clone https://github.com/your-repository/roaming-cdr-service.git
cd roaming-cdr-service`

2. Соберите проект:

`mvn clean install`

3. Запустите приложение:

`java -jar target/roaming-cdr-service.jar`

4. Приложение будет доступно по адресу: http://localhost:8080.

#### Эндпоинты (Swagger)

http://localhost:8080/swagger-ui.html

### Описание технического решения

#### ER-диаграмма баз данных

// TODO: сделать

#### Генератор CDR-записей

// TODO: подробнее описать

В таблицу subscriber добавляются 10 абонентов.

Генерация на протяжении года. 

// TODO: опираться на исследование/статью
В качестве ограничений принимается, что звонки происходят по экспоненциальному закону распределения. 
Средний интервал между звонками — 1 час
Средняя длительность звонка — 10 минут

Учитывается, что абоненты одновременно могут разговаривать только по 1 линии.

#### REST API для работы с UDR

http://localhost:8080/swagger-ui.html

по одному переданному абоненту. 
либо за запрошенный месяц, либо за весь тарифицируемый период.

UDR записи по всем нашим абонентам за запрошенный месяц.

// TODO: описать формат