#!/usr/bin/env python3

import requests
from bs4 import BeautifulSoup
import unicodedata

#The seed page
url = "http://kumukia.ru/adabiat/index.php"


#storing the response received from the GET request
content = requests.get(url).text


#extracting the initial list on the navigation bar
r = content.find("[\n[")

m = content.find("]\n]")

first_catch = content[r:m+3]



first_catch = first_catch.replace("[","").replace("]","").replace('"',"").replace(","," ").replace(":","")

first_catch = first_catch.replace("p g","p?g")

first_catch = first_catch.split()



#forming complete url along with the parameters
list1=[]
for i in first_catch:
        list1.append("http://kumukia.ru/adabiat/%s"%(i))



#function to make list of parameters from a string (In this case, HTML content of the webpage)
def make_list(l):
        soup = BeautifulSoup(l)
        m=""
        p=""

        
                
        for i in soup.find_all("a"):
                m=i.get("onclick" or "onClick")
                if m==None:
                        return []
                else:
                        p += m


        p = p.replace("doLoads","").replace("(","").replace(")","").replace("amp;","").replace(";"," ").replace("'","").replace("doLoad2","").replace(","," ")

        p = p.split()
        return p



#To form a list of urls given the list of parameters
def refiner(p):

        k=[]
        
        for i in range(len(p)):
                if p[i]=="getpage.php" or p[i]== "getmenu.php" or p[i].find("search=worklist")==0 or p[i].find("search=workpage")==0:
                        k.append(p[i])
        hp = []

        for i in range(len(k)):
                if i%2!=0:
                        hp.append("http://kumukia.ru/adabiat/%s?%s" %  (k[0], k[i]))

        return hp


newlist = []
                        
last = []               
        
final = []               
        
h = []        
        
s=[]

urls = []


#iterating over the initial list to retrieve the child links
for i in list1:
       p = make_list(requests.get(i).text)
       s+=refiner(p)

#iterating over the list of first childs to retrieve their child links
for i in s:
        p = make_list(requests.get(i).text)
        newlist+=refiner(p)
        
#iterating over the links to the work of the authors to retrieve the pages with text.
for i in newlist:
        p = make_list(requests.get(i).text)
        last+=refiner(p)

#eliminating Copies
for i in last:
        if i not in urls:
                urls.append(i)


print("The list of urls of works by various authors on the page http://kumukia.ru/adabiat/index.php :")


for i in urls:
        print(i)
