#!/bin/bash

set -e
#set -x

container_name="postgres-database"

db_user="scott"
db_schema="postgres"

backup_file_folder="/var/lib/postgresql/backup"

datum_string=$(date +%Y-%m-%d)
backup_file="backup_$datum_string.dump"

backup_file_full="$backup_file_folder/$backup_file"

echo "Dumping Database data to file $backup_file_full ..."

command="mkdir -p $backup_file_folder \
  && su postgres \
  && pg_dump --no-privileges --no-acl --no-owner \
      -U $db_user $db_schema > $backup_file_full"

docker exec "$container_name" bash -c "$command"

echo "Copying backup file to local machine ..."

docker cp "$container_name":"$backup_file_full" "$backup_file"

echo "Backup file $backup_file successfully copied."
echo "Process finished with success"
