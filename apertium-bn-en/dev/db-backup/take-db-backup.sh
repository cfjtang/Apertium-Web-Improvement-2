#!/bin/sh

# we are not using bzipped tar files for backup anymore
# mysqldump -uroot -proot --routines bengali_conjugator | bzip2 > bengali_conjugator$(date +_%Y%m%d_%H%M).sql.bz2
mysqldump -uroot -proot --routines bengali_conjugator | mysqldump.sql

echo 'Press any key to continue ...'
read dummy

