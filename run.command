#!/bin/bash
cd "$(dirname "$0")"
export PATH="/opt/homebrew/bin:/usr/local/bin:/usr/local/mysql/bin:$PATH"

if ! mysqladmin -uroot status >/dev/null 2>&1; then
    brew services start mysql >/dev/null 2>&1
    sleep 5
fi

if ! mysql -uroot -e "USE examdb" >/dev/null 2>&1; then
    echo "Creating database..."
    mysql -uroot < database.sql
fi

mkdir -p out
javac -cp "lib/mysql-connector-j-8.4.0.jar" -d out *.java && java -cp "out:lib/mysql-connector-j-8.4.0.jar" LoginPage
