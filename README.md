Запуск: <br>
    mvn clean compile package <br>
    java -jar target/goragallery-0.0.1-SNAPSHOT.jar <br>

Загрузка: <br>
    curl -F 'file=@lena.png' -F name="lena" http://localhost:8080/upload <br>
Просмотр фото (на конце id): <br>
    curl http://localhost:8080/image/1 <br>
Список фото: <br>
    curl  http://localhost:8080/list <br>
Удаление (на конце id): <br>
    curl http://localhost:8080/delete/1<br>
При удалении превью оригинал сохраняется, при удалении оригинала удаляется и превью.<br>

БД обнуляется при запуске.
