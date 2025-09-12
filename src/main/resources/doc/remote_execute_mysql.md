远程执行mysql脚本:

mysql -h 10.0.0.50 -P 3306 -u root -p -e "SELECT VERSION();"
mysql -h 10.0.0.50 -P 3306 -u root -p oauth2 <"D:\software\developmentTools\Git\gitee\newpap\demo\flowable-demo\src\main\resources\sql\create_oauth2.sql"

如果是本地的话，可以省略ip和端口号：
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS oauth2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p oauth2 <"D:\software\developmentTools\Git\gitee\newpap\demo\flowable-demo\src\main\resources\sql\create_oauth2.sql"
