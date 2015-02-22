#!/usr/bin/env python3

import re, requests, sys
from bs4 import BeautifulSoup

#Copied from http://www.presskg.com/bible/bible_kg/
tocRaw = """Эски Осуят

Мусанын беш китеби:
1.	Башталыш
2.	Мисирден чыгуу
3.	Лебилер
4.	Сандар
5.	Мыйзамды кайталоо

Куулганга чейинки тарых:
6.	Нундун уулу Жашыя
7.	Израил соттору
8.	Рут
9.	Биринчи Шемуел китеби
10.	Экинчи Шемуел китеби
11.	Биринчи Падышалар китеби
12.	Экинчи Падышалар китеби
13.	Биринчи жылнаама китеби
14.	Экинчи жылнаама китеби

Куулгандан кийинки тарых:
15.	Эзра
16.	Некемия
17.	Эстер

Жандүйнө китептери:
18.	Аюб
19.	Забур
20.	Сулаймандын накыл сөздөрү
21.	Насаатчы
22.	Ырдын ыры

Залкар Пайгамбарлар:
23.	Ышайа
24.	Жеремия
25.	Жеремиянын ыйы
26.	Жезекиел
27.	Даниел

Куулганга чейинки кичи пайгамбарлар:
28.	Ошуя
29.	Жоел
30.	Амос
31.	Обадия
32.	Жунус
33.	Михей
34.	Накум
35.	Хабакук
36.	Сепания

Куулгандан кийинки кичи пайгамбарлар:
37.	Акай
38.	Захария
39.	Малахия

Жаңы Осуят

Жакшы Кабарлар:
40.	Матайдын Жакшы Кабары
41.	Марктын Жакшы Кабары
42.	Лукмандын Жакшы Кабары
43.	Жакандын Жакшы Кабары
44.	Ыйык Элчилердин иштери

Башка авторлордун каттары:
45.	Жакыптын каты
46.	Петирдин биринчи каты
47.	Петирдин экинчи каты
48.	Жакандын биринчи каты
49.	Жакандын экинчи каты
50.	Жакандын үчүнчү каты
51.	Жүйүттүн каты

Пабылдын жыйындарга жазган каттары:
52.	Римдиктерге жазган каты
53.	Корунттуктарга биринчи каты
54.	Корунттуктарга экинчи каты
55.	Галатиялыктарга жазган каты
56.	Эфестиктерге жазган каты
57.	Филипиликтерге жазган каты
58.	Колосалыктарга жазган каты
59.	Тесалоникалыктарга биринчи каты
60.	Тесалоникалыктарга экинчи каты

Пабылдын жеке адамдарга жазган каттары:
61.	Тиметейге биринчи каты
62.	Тиметейге экинчи каты
63.	Титке жазган каты
64.	Филемонго жазган каты
65.	Эврейлерге жазган каты

Аяндар:
66.	Жакан алган аян

"""

chapterNames = re.findall(r'\d+\.\t(.*)', tocRaw)
chapterNames = dict(zip(range(1, len(chapterNames) + 1), chapterNames))

output = ""
for i, chapterName in chapterNames.items():
    try:
        page = requests.get('http://www.presskg.com/bible/bible_kg/%02d.htm' % i).text
        for e in BeautifulSoup(page).select("td[bgcolor=white]")[3].select("font")[0].findAll():
            if e.name == 'h2':
                if i != 0:
                    output += "\n"
                output += '%s %s' % (chapterName.strip(), re.findall(r'^(\d+)', e.text.strip())[0].strip())
            elif e.name == 'p' or e.name == 'h3':
                if e.text.strip():
                    output += e.text.strip()
    except Exception as e:
        print('Skipping chapter #%s: %s (%s)' % (i, chapterName, e))

with open("kir.bible.presskg.txt", 'w') as bibfile:
    bibfile.write(output)
