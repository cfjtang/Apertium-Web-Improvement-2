
# экономика
for i in `seq 1 6`; do 
	echo "2: $i" >> /dev/stderr;
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=2" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# политика
for i in `seq 1 34`; do 
	echo "3: $i" >> /dev/stderr;
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=3" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# сельское хозяйство
for i in `seq 1 5`; do 
	echo "4: $i" >> /dev/stderr;
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=4" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# общество
for i in `seq 1 35`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=5" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# литература
for i in `seq 1 2`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=6" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# культура
for i in `seq 1 8`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=7" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# спорт
for i in `seq 1 14`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=8" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# криминал
for i in `seq 1 10`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=9" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# интервью
for i in `seq 1 6`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=10" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# юбилеи
for i in `seq 1 2`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=11" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# образование
for i in `seq 1 10`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=12" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# здравоохранение
for i in `seq 1 10`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=13" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done

# разное
for i in `seq 1 10`; do 
	wget -q -O - "http://hakikat.etnosmi.ru/catalog_stats.php?page=$i&cid=14" | iconv -f windows-1251 | grep '<a href="one_stat.php?id=' | sed 's/^[\t ]*//g';
done


