Запуск:
    mvn clean compile package
    java -jar target/goragallery-0.0.1-SNAPSHOT.jar

Загрузка:
    curl -F 'file=@lena.png' -F name="lena" http://localhost:8080/upload
Просмотр фото (на конце id):
    curl http://localhost:8080/image/1
Список фото:
    curl  http://localhost:8080/list
Удаление (на конце id):
    curl http://localhost:8080/delete/1
При удалении превью оригинал сохраняется, при удалении оригинала удаляется и превью.