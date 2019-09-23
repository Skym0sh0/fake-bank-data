#!/bin/sh

set -e
set -x

docker run --rm \
	--volumes-from postgresql-database-release \
	-v ./backup/:/backup \
	ubuntu \
	ls -al var/lib/postgresql/data
