
# coding: utf-8

# In[1]:

from bs4 import BeautifulSoup
import pandas as pd
import requests
import json


# In[2]:

header={
    'Accept':'*/*',
'Accept-Encoding':'gzip, deflate, sdch',
'Accept-Language':'zh-CN,zh;q=0.8',
'Connection':'keep-alive',
'Cookie':'subjectname=%E6%95%B0%E5%AD%A6; gradename=ä¸å¹´çº§; paperSubjectName=%E6%95%B0%E5%AD%A6; PHPSESSID=h33379oetnq550nstsngp2smj3; Hm_lvt_8726663061aed278312e4b173ee4fd39=1512982256,1513330933; Hm_lpvt_8726663061aed278312e4b173ee4fd39=1513330937',
'Host':'www.hitecloud.cn',
'Referer':'http://www.hitecloud.cn/nexam/exampx/knowledgequestion',
'User-Agent':'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36',
'X-Requested-With':'XMLHttpRequest'
}
url_list=[]


# In[3]:

for i in range(2,458):
    url_current='http://www.hitecloud.cn/nexam/knowledgeAjax/getthirdque/thiknowid/'+str(i)+'/type//diff/all'
    url_list.append(url_current)


# In[4]:

pagecounts=[]
import time
i=0
for url in url_list:
    soup=BeautifulSoup(requests.get(url).text,"lxml")
    quenum=soup.select(".quenums span")[0].text
    if (int(quenum)%10)==0:
        pagecount=int(int(quenum)/10)
    else:
         pagecount=int(int(quenum)/10)+1
    print(i)
    i+=1
    pagecounts.append(pagecount)
    time.sleep(0.1)


# In[5]:

import time
i=0
count=0
for url in url_list:
    pagecount=pagecounts[i]
    if pagecount>=2:
         for j in range(2,int(pagecount)+1):
                 url_current=str(url)+'/page/'+str(j)
                 url_list.append(url_current)
                 print(count)
                 count+=1
    i+=1
    time.sleep(0.5)


# In[7]:

import re
soup = BeautifulSoup(requests.get(url_list[0]).text,"lxml" )
url='http://www.hitecloud.cn/nexam/knowledgeAjax/getfirsecknow' 
ii=[1,98,156,220,352,420]
rs=''
for i in range(len(ii)):
    r=requests.post(url,data=[('knowid',int(ii[i]))]).text.replace('\r','').replace('\n','').replace(' ','').replace('<spanclass="chooseonetkbgarrow_down"></span><spanclass="secvalueddown">','')
    rs=rs+r
head=[]
con=[]
type1=[]
info=[]
level=[]
ans=[]
classs=[]
id1=[]
brand=[[1,'数与式'],[98,'方程与不等式'],[156,'函数'],[220,'图形的性质'],[352,'图形的变换'],[420,'统计与概率'],[1000,'']]
brands=[]
brands2=[]
def get_info(url):
    resp = requests.get(url,timeout=15)
    soup = BeautifulSoup(resp.text,"lxml")
    pages=int(url.split('/')[7])
    pattern='<spanclass="thivaluethiknow'+str(pages)+'"data-thirdid="'+str(pages)+'">'+'(.*?)</span>'
    pattern2='<divclass="secknowlist"data-secid="'+str(pages)+'">'+'(.*?)</span>'
    j=0
    while(pages>brand[j][0]):
        j+=1
        brand1=brand[j-1][1]
    while(len(re.findall(pattern2,rs))==0):
        pages-=1
        pattern2='<divclass="secknowlist"data-secid="'+str(pages)+'">'+'(.*?)</span>'
    brand2=re.findall(pattern2,rs)
    for i in range(0,10):
        try:
            brand_current=brand1
        except:
            brand_current=None
        try:
            brand2_current=brand2
        except:
            brand2_current=None
        try:
            info=str(soup.select(".card_item ")[i])
            id_current= re.findall(r"(?<=id=\"ques_).+?(?=\")",info)[0]
        except:
            id_current=None
        try:
            class_current=re.findall(pattern,rs)
        except:
            class_current=None
        try:
            ans_current=[s.text for s in soup.select(".card_ans_btn")][i]
        except:
            ans_current=None
        try:
            head_current=soup.select(".card_head")[i].text.replace("\xa0","")
        except:
            head_current=None
        try:
            con_current=soup.select(".card_con")[i].text.replace("\xa0","").replace("\n","")
        except:
            con_current=None
        try:
            type_current=re.findall("题型：(.+)\n",ans_current)[0]
        except:
            type_current=None
        try:
            level_current=re.findall("难度：(.+)\n",ans_current)[0]
        except:
            level_current=None
           
        head.append(head_current)
        con.append(con_current)
        ans.append(ans_current)
        type1.append(type_current)
        level.append(level_current)
        id1.append(id_current)
        classs.append(class_current)
        brands.append(brand_current)
        brands2.append(brand2_current)


# In[8]:

import time
for url in url_list:
    get_info(url)
    print("running")
    time.sleep(0.1)


# In[37]:

df = pd.DataFrame(dict(head=head,con=con,type1=type1,level=lev el,id1=id1,classs=classs,brands=brands,brands2=brands2))
df.dropna(thresh=4,inplace=True)
df.to_csv(r"E:\questions_list_final.csv",index=False,encoding='utf-8')


# In[38]:

from sqlalchemy import create_engine
engine = create_engine("mysql+pymysql://root:whx19960110@localhost:3306/test",echo=True,connect_args={'charset':'utf8'},pool_size=30)


# In[39]:

import pandas as pd

df.to_sql(con=engine,name="question_list",if_exists="append")

