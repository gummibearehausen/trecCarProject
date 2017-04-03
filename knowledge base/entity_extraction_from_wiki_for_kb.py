from annotations import *
from pprint import pprint
import codecs
import time
import sys

def recursion(children, links, article, kb_base_dict):
    for i in children:
        try:
            title = i.title
            article.append('*'+title)
            recursion(i.children, links, article, kb_base_dict)
        except:
            try:
                para_text = "".join(b.__str__() for b in i.para_bodies)
                para_t = para_text
                if len(para_text) > 0:
                    article.append('-'+para_t)
                for para in i.para_bodies:
                    q = para
                    try:
                        m = q.page.strip()
                        n = q.anchor_text.strip()
                        if m[:8] != u"Category":
                            if m[:5] != u"Image":
                                links.append(m)
                                if m.lower() != n.lower():
                                    kb_base_dict.append(n.lower() + "\t" + m)
                    except:
                        pass
            except:
                pass


def section_constructor(corpus, key, kb_dict_file, kb_graph_f):
    try:
        redirect_flag = 0
        ske = corpus.get(key).skeleton
        links = []
        article = []
        knowledge_base_dictionary = []
        recursion(ske, links, article, knowledge_base_dictionary)

        page_section_dictionary = {}
        s_t = '*Introduction'
        para = []
        for i in range(len(article)):
            if i != len(article)-1:
                if article[i][0] == '*':
                    if article[i+1][0] != '*':
                        s_t = article[i]
                else:
                    if article[i+1][0] != '*':
                        para.append(article[i])
                    else:
                        para.append(article[i])
                        page_section_dictionary[s_t] = para
                        s_t=''
                        para = []
            else:
                if article[i][0] != '*':
                    para.append(article[i])
                    page_section_dictionary[s_t] = para
        pprint(article)
        # print('-'*20)
        # pprint(page_section_dictionary)
        # print("-"*20)
        # pprint(set(links))
        # print("-" * 20)

        redirect_text_set = {u"-#redirect", u"-#Redirect"}
        # "r" :Englisch, "R" :Fremdsprachen
        try:

            if page_section_dictionary['*Introduction'][0][:10] in redirect_text_set:
                if len(links) != 0:
                    redirected_entity = links[0]
                    knowledge_base_dictionary.append(key.lower()+"\t".encode("utf-8")+redirected_entity)
                    redirect_flag = 1
            else:
                pass
        except KeyError:
            pass

        # pprint(knowledge_base_dictionary)
        # print('\n' * 2)

        if len(page_section_dictionary.keys()) != 0:
            if redirect_flag==1:
                if len(knowledge_base_dictionary) != 0:
                    kb_dict_file.write(knowledge_base_dictionary[0]+"\n")
            else:
                if len(knowledge_base_dictionary) != 0:
                    for pair in knowledge_base_dictionary:
                        kb_dict_file.write(pair+"\n")
                if len(links) != 0:
                    entities = "\t".join([ entity for entity in set(links)])
                    kb_graph_f.write(key+"\t"+entities+"\n")

            return page_section_dictionary
    except AttributeError:
        print("Couldn't fine %s" % key)

if __name__ == '__main__':
    global Corpus
  
    corpus_name=sys.argv[1]
    knowledgebase_dict_output_name = sys.argv[2]
    knowledgebase_graph_output_name =sys.argv[3]
    number_of_wikipedia_articles = int(sys.argv[4])
    Corpus = AnnotationsFile(corpus_name)
    num_of_pages = len(Corpus.keys())
    print("Number of the pages: %s" +str(num_of_pages))
    knowledgebase_dict_file=codecs.open(knowledgebase_dict_output_name, "w", encoding="utf-8")
    knowledgebase_graph_file = codecs.open(knowledgebase_graph_output_name, "w", encoding="utf-8")
    num_of_page = 0
    time_start = time.time()

    for k in Corpus.keys()[:number_of_wikipedia_articles]:
        if k!="":
            if k[:8]!=u"Category":
                if k[:5] != u"Image":
                    if k[:8]!=u"Template":
                        if k[:9]!=u"Wikipedia":
                            if k[-3:]!=u"jpg":
                                if k[:4] !=u"List":
                                    if k[:4] !=u"File":
                                        #d is a pagename:section of paras dictionary
                                        d = section_constructor(Corpus,k,knowledgebase_dict_file,knowledgebase_graph_file)
                                        num_of_page+=1
                                        print(str(num_of_page)+'\t' +'*'*10+'*'*10)
    knowledgebase_dict_file.close()
    knowledgebase_graph_file.close()
    time_end = time.time()
    process_time = time_end-time_start
    print("It takes %s seconds to process" %process_time)
