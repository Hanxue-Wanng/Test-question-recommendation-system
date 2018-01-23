
# coding: utf-8

# In[2]:

import csv
import jieba
import pandas as pd
from pandas import Series as sr, DataFrame as df 


# In[ ]:




# In[10]:

data=pd.read_csv('E:\questions_list_final3.csv')
l=len(data)
option_cut=[]
title_cut=[]
def stopwordslist(filepath):  
    stopwords = [line.strip() for line in open(filepath, 'r', encoding='utf-8').readlines()]  
    return stopwords  
stopwords = stopwordslist('E:\stopwords.txt')
for i in range(l):
    result=[]
    seg_list=jieba.cut(str(data.ix[i][5]))
    for w in seg_list:
        if w not in stopwords:
            result.append(w)
    option_cut.append(result)
for i in range(l):
    result=[]
    seg_list=jieba.cut(str(data.ix[i][6]))
    for w in seg_list:
        if w not in stopwords:
            result.append(w)
    title_cut.append(result)


# In[11]:

data['title_cut']=title_cut
data['option_cut']=option_cut
del data['que_title']
del data['que_option']
data.to_csv(r"E:\questions_cut.csv",index=False,encoding='utf-8')

