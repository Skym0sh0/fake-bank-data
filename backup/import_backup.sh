#!/bin/bash

set -e
#set -x

container_name="postgres-database"

db_user="scott"
db_schema="postgres"

backup_file_folder="/var/lib/postgresql/backup"

backup_file=$(find . -type f -name "backup_*.dump" | sort -r | head -n 1)

backup_file_full="$backup_file_folder/$backup_file"

echo "Copying file %backup_file into docker container"

docker cp "$backup_file" "$container_name:$backup_file_full"

echo "Now Importing dump file into database"

echo $backup_file

command="su postgres \
  && createdb -U $db_user $db_schema || true \
  && psql -U $db_user $db_schema < $backup_file_full"

docker exec "$container_name" bash -c "$command"

echo "Dump successfully imported into database"
