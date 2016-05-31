REM Run as administrator
mysqldump --host=127.0.0.1 --user=root --password=<password> --routines --triggers javaorblog > dumpfile.sql